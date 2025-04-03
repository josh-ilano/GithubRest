// MealResponse.kt (Data Model)
package com.example.composemeal

import com.squareup.moshi.Json

data class MealResponse(
    @Json(name = "meals") val list: List<MealItem>, // List of meal items
)

data class MealItem(
//    val main: Main, // Main meal data
//    val weather: List<Weather>, // List of weather conditions
//    val ingredients: List<String>, // 20 Ingredients
    @Json(name = "strMeal") val mealName: String, // name of meal
    @Json(name = "strInstructions") val instructions: String, // affiliated instructions
    @Json(name = "strMealThumb") val mealImage: String

)

//data class Main(
//    val ingredients: List<String>, // 20 Ingredients
//    @Json(name = "strMeal") val mealName: String, // name of meal
//    @Json(name = "strInstructions") val instructions: String // affiliated instructions
//)


