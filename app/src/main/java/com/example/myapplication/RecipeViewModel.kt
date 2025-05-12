package com.example.myapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).recipeDao()

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    init {
        loadRecipes()
    }

    fun loadRecipes() {
        viewModelScope.launch {
            _recipes.value = dao.getAll()
        }
    }

    fun addRecipe(recipe: Recipe) {
        viewModelScope.launch {
            dao.insert(recipe)
            loadRecipes() // odśwież listę
        }
    }
    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            dao.delete(recipe)
            loadRecipes()
        }
    }
    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            dao.update(recipe)
            loadRecipes() // aby odświeżyć listę
        }
    }


}
