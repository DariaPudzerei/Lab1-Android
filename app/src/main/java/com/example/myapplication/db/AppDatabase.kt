package com.example.myapplication.db

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver
import kotlinx.coroutines.Dispatchers

/**
 * ЛР4. База даних Room 3 (SQLite-файл contacts.db) з двома таблицями.
 * Room 3 потребує SQLiteDriver — використовуємо вбудований в Android (AndroidSQLiteDriver).
 */
@Database(
    entities = [GroupEntity::class, ContactEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun contactsDao(): ContactsDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        /** Одна спільна копія БД на весь застосунок (Singleton) */
        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder<AppDatabase>(
                    context = context.applicationContext,
                    name = "contacts.db"
                )
                    .setDriver(AndroidSQLiteDriver())
                    .setQueryCoroutineContext(Dispatchers.IO)
                    .build()
                    .also { instance = it }
            }
    }
}
