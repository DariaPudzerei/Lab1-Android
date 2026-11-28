package com.example.myapplication.db

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

/**
 * ЛР4. DAO: запити до обох таблиць.
 * Room 3 вимагає suspend-функцій для запису, а для читання повертаємо Flow,
 * щоб список оновлювався автоматично після кожної зміни в БД.
 */
@Dao
interface ContactsDao {

    // ---------- Групи ----------
    @Query("SELECT * FROM contact_groups ORDER BY id")
    fun observeGroups(): Flow<List<GroupEntity>>

    @Query("SELECT COUNT(*) FROM contact_groups")
    suspend fun countGroups(): Int

    @Query("SELECT * FROM contact_groups WHERE title = :title LIMIT 1")
    suspend fun findGroupByTitle(title: String): GroupEntity?

    @Insert
    suspend fun insertGroup(group: GroupEntity): Long

    @Update
    suspend fun updateGroup(group: GroupEntity)

    @Delete
    suspend fun deleteGroup(group: GroupEntity)

    // ---------- Контакти ----------
    @Query("SELECT * FROM contacts ORDER BY name")
    fun observeContacts(): Flow<List<ContactEntity>>

    @Insert
    suspend fun insertContact(contact: ContactEntity): Long

    /** ЛР5: пакетна вставка контактів, отриманих з API */
    @Insert
    suspend fun insertContacts(contacts: List<ContactEntity>)

    @Update
    suspend fun updateContact(contact: ContactEntity)

    @Delete
    suspend fun deleteContact(contact: ContactEntity)
}
