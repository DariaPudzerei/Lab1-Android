package com.example.myapplication.data

/**
 * ЛР3: моделі даних для списку.
 * Спільний sealed-інтерфейс дозволяє зберігати різні типи в одному списку
 * і вибирати розмітку через when (item) { is ... }.
 */
sealed interface FeedItem {
    val id: Int
}

/** Контакт: ім'я, роль і статус у мережі */
data class Contact(
    override val id: Int,
    val name: String,
    val role: String,
    val isOnline: Boolean
) : FeedItem

/** Нотатка: заголовок, текст і дата */
data class Note(
    override val id: Int,
    val title: String,
    val text: String,
    val date: String
) : FeedItem

/** Галерея: заголовок і набір кольорів (ARGB) — показується горизонтальним списком */
data class Gallery(
    override val id: Int,
    val title: String,
    val colors: List<Long>
) : FeedItem
