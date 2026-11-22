package com.example.myapplication.data

import androidx.lifecycle.ViewModel

/**
 * ЛР3: ViewModel списку. Отримує дані з репозиторію один раз
 * і зберігає їх при повороті екрана та переходах між вкладками.
 */
class FeedViewModel(
    repository: FeedRepository = FeedRepository()
) : ViewModel() {
    val items: List<FeedItem> = repository.getItems()
}
