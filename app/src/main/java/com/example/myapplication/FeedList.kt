package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.Contact
import com.example.myapplication.data.FeedItem
import com.example.myapplication.data.Gallery
import com.example.myapplication.data.Note

/* ===================== ЛР3: список з кількома типами елементів ===================== */

@Composable
fun FeedList(items: List<FeedItem>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = items,
            key = { it.id },                 // стабільний ключ елемента
            contentType = { it::class }      // підказка Compose для повторного використання розміток
        ) { item ->
            // Вибір розмітки відповідно до типу даних
            when (item) {
                is Contact -> ContactRow(item)
                is Note -> NoteCard(item)
                is Gallery -> GalleryRow(item)
            }
        }
    }
}

/* ---------- Розмітка 1: контакт ---------- */
@Composable
fun ContactRow(contact: Contact, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Аватар з першою літерою імені
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.name.first().toString(),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(contact.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(contact.role, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        // Індикатор статусу
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(if (contact.isOnline) Color(0xFF2E7D32) else Color(0xFF9E9E9E))
        )
    }
}

/* ---------- Розмітка 2: нотатка ---------- */
@Composable
fun NoteCard(note: Note, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📝 ${note.title}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(note.text, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text(note.date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/* ---------- Розмітка 3: галерея — горизонтальний список у вертикальному (завдання 9) ---------- */
@Composable
fun GalleryRow(gallery: Gallery, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(gallery.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(gallery.colors) { index, color ->
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(color)),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(
                        text = "#${index + 1}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
