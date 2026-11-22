package com.example.myapplication.db

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

/**
 * ЛР4. Таблиця 1 (батьківська): групи контактів.
 * Назву "groups" не використовуємо — це зарезервоване слово SQLite.
 */
@Entity(tableName = "contact_groups")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String
)

/**
 * ЛР4. Таблиця 2 (дочірня): контакти.
 * groupId — зовнішній ключ на contact_groups.id:
 *  - ON DELETE CASCADE: видалення групи видаляє всі її контакти;
 *  - ON UPDATE CASCADE: зміна id групи оновлює groupId у контактах.
 * Індекс по groupId пришвидшує вибірку та перевірку зовнішнього ключа.
 */
@Entity(
    tableName = "contacts",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("groupId")]
)
data class ContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "groupId") val groupId: Long,
    val name: String,
    val role: String,
    val isOnline: Boolean = false
)
