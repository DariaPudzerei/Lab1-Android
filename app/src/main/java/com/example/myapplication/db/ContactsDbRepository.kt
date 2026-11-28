package com.example.myapplication.db

import com.example.myapplication.api.RetrofitClient
import com.example.myapplication.api.UsersApi
import com.example.myapplication.api.toEntity
import kotlinx.coroutines.flow.Flow

/** ЛР4. Репозиторій — прошарок між ViewModel і DAO. */
class ContactsDbRepository(
    private val dao: ContactsDao,
    private val api: UsersApi = RetrofitClient.usersApi      // ЛР5
) {

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

    // ---------- ЛР5: завантаження з API і збереження в БД ----------

    /**
     * Завантажує користувачів з API (JSON) і зберігає їх у БД як окрему групу.
     * Стара група з API спочатку видаляється — її контакти зникають каскадно (ON DELETE CASCADE),
     * тому повторне завантаження не створює дублікатів.
     * @return кількість збережених записів
     */
    suspend fun importFromApi(): Int {
        val response = api.getUsers()
        if (!response.isSuccessful) {
            throw IllegalStateException("HTTP ${response.code()}")
        }
        val users = response.body().orEmpty()

        dao.findGroupByTitle(API_GROUP_TITLE)?.let { dao.deleteGroup(it) }
        val groupId = dao.insertGroup(GroupEntity(title = API_GROUP_TITLE))

        dao.insertContacts(users.map { it.toEntity(groupId) })
        return users.size
    }

    companion object {
        const val API_GROUP_TITLE = "API: JSONPlaceholder"
    }
}
