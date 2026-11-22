package com.example.myapplication.db

import kotlinx.coroutines.flow.Flow

/** ЛР4. Репозиторій — прошарок між ViewModel і DAO. */
class ContactsDbRepository(private val dao: ContactsDao) {

    val groups: Flow<List<GroupEntity>> = dao.observeGroups()
    val contacts: Flow<List<ContactEntity>> = dao.observeContacts()

    /** Початкові тестові дані — лише при першому запуску (порожня БД) */
    suspend fun seedIfEmpty() {
        if (dao.countGroups() > 0) return
        val team = dao.insertGroup(GroupEntity(title = "Команда проєкту"))
        val group = dao.insertGroup(GroupEntity(title = "Одногрупники КТ-23"))
        dao.insertContact(ContactEntity(groupId = team, name = "Олена Коваль", role = "Android-розробниця", isOnline = true))
        dao.insertContact(ContactEntity(groupId = team, name = "Максим Бондар", role = "QA-інженер"))
        dao.insertContact(ContactEntity(groupId = group, name = "Ірина Шевчук", role = "Студентка", isOnline = true))
        dao.insertContact(ContactEntity(groupId = group, name = "Андрій Мельник", role = "Студент"))
    }

    suspend fun addGroup(title: String) = dao.insertGroup(GroupEntity(title = title))
    suspend fun updateGroup(group: GroupEntity) = dao.updateGroup(group)
    suspend fun deleteGroup(group: GroupEntity) = dao.deleteGroup(group)

    suspend fun addContact(groupId: Long, name: String, role: String) =
        dao.insertContact(ContactEntity(groupId = groupId, name = name, role = role))
    suspend fun updateContact(contact: ContactEntity) = dao.updateContact(contact)
    suspend fun deleteContact(contact: ContactEntity) = dao.deleteContact(contact)
}
