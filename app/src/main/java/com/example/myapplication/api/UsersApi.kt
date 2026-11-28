package com.example.myapplication.api

import retrofit2.Response
import retrofit2.http.GET

/** ЛР5. Опис HTTP-запитів до API для Retrofit */
interface UsersApi {

    /** GET https://jsonplaceholder.typicode.com/users → JSON-масив користувачів */
    @GET("users")
    suspend fun getUsers(): Response<List<UserDto>>
}
