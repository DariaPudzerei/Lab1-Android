package com.example.myapplication.api

import com.example.myapplication.db.ContactEntity
import com.google.gson.annotations.SerializedName

/**
 * ЛР5. DTO — модель JSON-відповіді API https://jsonplaceholder.typicode.com/users
 * (поля, які нам потрібні; решту Gson ігнорує).
 */
data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("company") val company: CompanyDto
)

data class CompanyDto(
    @SerializedName("name") val name: String
)

/** Перетворення DTO → сутність БД (таблиця contacts) */
fun UserDto.toEntity(groupId: Long) = ContactEntity(
    groupId = groupId,
    name = name,
    role = company.name,
    isOnline = id % 2 == 0          // API не має статусу — задаємо умовно
)
