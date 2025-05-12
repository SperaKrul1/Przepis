package com.example.myapplication

import com.example.myapplication.api.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {
    @GET("search.php")
    suspend fun searchMeal(@Query("s") query: String): MealResponse
}
