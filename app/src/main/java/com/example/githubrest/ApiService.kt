// ApiService.kt
package com.example.githubrest

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory // Added import

// https://api.github.com/users/josh-ilano/repos
interface ApiService {
    // Define the GET endpoint for meal data

    @GET("users/{username}/repos")
    fun getRepos(
        @Path("username") username: String, // Query parameter for username
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Call<List<GitResponse>> // Return a Call object for the ForecastResponse
}


object ApiClient {
    private const val BASE_URL = "https://api.github.com/" // Base URL for the OpenWeather API
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // Explicitly add KotlinJsonAdapterFactory
        .build() // Build the Moshi instance

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL) // Set the base URL
        .addConverterFactory(MoshiConverterFactory.create(moshi)) // Add the Moshi converter factory
        .build() // Build the Retrofit instance

    val apiService: ApiService = retrofit.create(ApiService::class.java) // Create an instance of the ApiService
}