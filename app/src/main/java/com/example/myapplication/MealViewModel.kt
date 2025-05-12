package com.example.myapplication.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MealViewModel : ViewModel() {

    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals: StateFlow<List<Meal>> = _meals

    fun searchMeals(query: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.mealApi.searchMeal(query)
                _meals.value = response.meals ?: emptyList()
            } catch (e: Exception) {
                _meals.value = emptyList() // obsługa błędu
            }
        }
    }
}
