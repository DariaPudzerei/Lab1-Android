package com.example.myapplication.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Елемент спільного списку: рядок з таблиці груп або з таблиці контактів */
sealed interface DbListItem {
    val key: String

    data class GroupRow(val group: GroupEntity, val count: Int) : DbListItem {
        override val key = "g${group.id}"
    }

    data class ContactRow(val contact: ContactEntity) : DbListItem {
        override val key = "c${contact.id}"
    }
}

/**
 * ЛР4. ViewModel: об'єднує дані обох таблиць в один список
 * (група → її контакти) і виконує операції додавання / зміни / видалення.
 */
class DbViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ContactsDbRepository(AppDatabase.get(application).contactsDao())

    val items: StateFlow<List<DbListItem>> =
        combine(repository.groups, repository.contacts) { groups, contacts ->
            groups.flatMap { group ->
                val members = contacts.filter { it.groupId == group.id }
                listOf(DbListItem.GroupRow(group, members.size)) +
                    members.map { DbListItem.ContactRow(it) }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch { repository.seedIfEmpty() }
    }

    // ---------- Групи ----------
    fun addGroup(title: String) = viewModelScope.launch { repository.addGroup(title) }
    fun renameGroup(group: GroupEntity, title: String) =
        viewModelScope.launch { repository.updateGroup(group.copy(title = title)) }
    fun deleteGroup(group: GroupEntity) = viewModelScope.launch { repository.deleteGroup(group) }

    // ---------- Контакти ----------
    fun addContact(groupId: Long, name: String, role: String) =
        viewModelScope.launch { repository.addContact(groupId, name, role) }
    fun editContact(contact: ContactEntity, name: String, role: String) =
        viewModelScope.launch { repository.updateContact(contact.copy(name = name, role = role)) }
    fun toggleOnline(contact: ContactEntity) =
        viewModelScope.launch { repository.updateContact(contact.copy(isOnline = !contact.isOnline)) }
    fun deleteContact(contact: ContactEntity) = viewModelScope.launch { repository.deleteContact(contact) }
}
