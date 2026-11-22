package com.example.myapplication.data

/**
 * ЛР3: репозиторій — єдине джерело даних для списку.
 * Зараз повертає тестові дані; пізніше його можна замінити на БД або мережу,
 * не змінюючи ViewModel та UI.
 */
class FeedRepository {

    fun getItems(): List<FeedItem> = listOf(
        Contact(1, "Олена Коваль", "Android-розробниця", isOnline = true),
        Note(2, "Лабораторна №3", "Створити список з LazyColumn і кількома типами елементів.", "22.11.2026"),
        Gallery(3, "Палітра проєкту", listOf(0xFF4E5D8CL, 0xFF2E7D32L, 0xFFC62828L, 0xFFF9A825L, 0xFF6A1B9AL, 0xFF00838FL)),
        Contact(4, "Максим Бондар", "QA-інженер", isOnline = false),
        Contact(5, "Ірина Шевчук", "UI/UX дизайнерка", isOnline = true),
        Note(6, "Нагадування", "Перед захистом перевірити роботу на емуляторі.", "23.11.2026"),
        Contact(7, "Андрій Мельник", "Backend-розробник", isOnline = false),
        Gallery(8, "Відтінки синього", listOf(0xFF0D47A1L, 0xFF1565C0L, 0xFF1E88E5L, 0xFF42A5F5L, 0xFF90CAF9L)),
        Note(9, "Ідея", "Додати пошук по списку контактів.", "25.11.2026"),
        Contact(10, "Софія Ткаченко", "Project manager", isOnline = true),
        Note(11, "Корисне", "LazyColumn створює лише видимі елементи, тому працює швидко навіть з великими списками.", "26.11.2026"),
        Contact(12, "Денис Лисенко", "DevOps-інженер", isOnline = false),
    )
}
