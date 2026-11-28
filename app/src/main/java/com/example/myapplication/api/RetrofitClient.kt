package com.example.myapplication.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** ЛР5. Єдиний екземпляр Retrofit на весь застосунок */
object RetrofitClient {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())   // JSON → Kotlin-об'єкти
            .build()
    }

    val usersApi: UsersApi by lazy { retrofit.create(UsersApi::class.java) }
}
