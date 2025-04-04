// GitResponse.kt (Data Model)
package com.example.githubrest

import com.squareup.moshi.Json

data class GitResponse(
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String?,
    @Json(name = "owner") val owner: Owner
)

data class Owner(
    @Json(name = "login") val login: String
)

