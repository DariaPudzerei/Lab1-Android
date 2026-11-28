package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.db.ApiState
import com.example.myapplication.db.ContactEntity
import com.example.myapplication.db.DbListItem
import com.example.myapplication.db.DbViewModel
import com.example.myapplication.db.GroupEntity

/* ===================== ЛР4: екран роботи з БД Room ===================== */

/** Який діалог зараз відкрито */
private sealed interface DbDialog {
    data object NewGroup : DbDialog
    data class EditGroup(val group: GroupEntity) : DbDialog
    data class NewContact(val groupId: Long) : DbDialog
    data class EditContact(val contact: ContactEntity) : DbDialog
}

@Composable
fun DatabaseScreen(
    modifier: Modifier = Modifier,
    vm: DbViewModel = viewModel()
) {
    val items by vm.items.collectAsState()
    val apiState by vm.apiState.collectAsState()          // ЛР5
    var dialog by remember { mutableStateOf<DbDialog?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("База даних", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Room · SQLite · таблиці «Групи» та «Контакти»",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(onClick = { dialog = DbDialog.NewGroup }) { Text("+ Група") }
        }
        Spacer(Modifier.height(8.dp))

        // ЛР5: завантаження даних з зовнішнього API
        OutlinedButton(
            onClick = { vm.loadFromApi() },
            enabled = apiState !is ApiState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (apiState is ApiState.Loading) "Завантаження…" else "⇣ Завантажити користувачів з API")
        }
        when (val s = apiState) {
            is ApiState.Success -> Text(
                "Отримано та збережено в БД: ${s.count} записів",
                fontSize = 13.sp, color = Color(0xFF2E7D32)
            )
            is ApiState.Error -> Text(
                "Помилка завантаження: ${s.message}",
                fontSize = 13.sp, color = MaterialTheme.colorScheme.error
            )
            else -> Unit
        }

        // Спільний список: записи з обох таблиць
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(items, key = { it.key }, contentType = { it::class }) { item ->
                when (item) {
                    is DbListItem.GroupRow -> GroupHeader(
                        row = item,
                        onAddContact = { dialog = DbDialog.NewContact(item.group.id) },
                        onEdit = { dialog = DbDialog.EditGroup(item.group) },
                        onDelete = { vm.deleteGroup(item.group) }
                    )
                    is DbListItem.ContactRow -> DbContactRow(
                        contact = item.contact,
                        onToggle = { vm.toggleOnline(item.contact) },
                        onEdit = { dialog = DbDialog.EditContact(item.contact) },
                        onDelete = { vm.deleteContact(item.contact) }
                    )
                }
            }
        }
    }

    // Діалоги додавання / редагування
    when (val d = dialog) {
        null -> Unit
        DbDialog.NewGroup -> TextFieldsDialog(
            title = "Нова група", labels = listOf("Назва"), initial = listOf(""),
            onDismiss = { dialog = null },
            onConfirm = { v -> vm.addGroup(v[0]); dialog = null }
        )
        is DbDialog.EditGroup -> TextFieldsDialog(
            title = "Перейменувати групу", labels = listOf("Назва"), initial = listOf(d.group.title),
            onDismiss = { dialog = null },
            onConfirm = { v -> vm.renameGroup(d.group, v[0]); dialog = null }
        )
        is DbDialog.NewContact -> TextFieldsDialog(
            title = "Новий контакт", labels = listOf("Ім'я", "Роль"), initial = listOf("", ""),
            onDismiss = { dialog = null },
            onConfirm = { v -> vm.addContact(d.groupId, v[0], v[1]); dialog = null }
        )
        is DbDialog.EditContact -> TextFieldsDialog(
            title = "Редагувати контакт", labels = listOf("Ім'я", "Роль"),
            initial = listOf(d.contact.name, d.contact.role),
            onDismiss = { dialog = null },
            onConfirm = { v -> vm.editContact(d.contact, v[0], v[1]); dialog = null }
        )
    }
}

/* ---------- Рядок таблиці «Групи» ---------- */
@Composable
private fun GroupHeader(
    row: DbListItem.GroupRow,
    onAddContact: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(start = 12.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(row.group.title, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text("Контактів: ${row.count}", fontSize = 12.sp)
        }
        TextButton(onClick = onAddContact) { Text("+") }
        TextButton(onClick = onEdit) { Text("✎") }
        TextButton(onClick = onDelete) { Text("✕", color = MaterialTheme.colorScheme.error) }
    }
}

/* ---------- Рядок таблиці «Контакти» ---------- */
@Composable
private fun DbContactRow(
    contact: ContactEntity,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onToggle)          // натискання змінює статус (UPDATE)
            .padding(start = 12.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                contact.name.firstOrNull()?.toString() ?: "?",
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(contact.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(
                "${contact.role} · ${if (contact.isOnline) "у мережі" else "не в мережі"}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(if (contact.isOnline) Color(0xFF2E7D32) else Color(0xFF9E9E9E))
        )
        TextButton(onClick = onEdit) { Text("✎") }
        TextButton(onClick = onDelete) { Text("✕", color = MaterialTheme.colorScheme.error) }
    }
}

/* ---------- Універсальний діалог з текстовими полями ---------- */
@Composable
private fun TextFieldsDialog(
    title: String,
    labels: List<String>,
    initial: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (List<String>) -> Unit
) {
    val values = remember { mutableStateListOf(*initial.toTypedArray()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                labels.forEachIndexed { i, label ->
                    OutlinedTextField(
                        value = values[i],
                        onValueChange = { values[i] = it },
                        label = { Text(label) },
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(values.map { it.trim() }) },
                enabled = values.first().isNotBlank()
            ) { Text("Зберегти") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Скасувати") } }
    )
}
