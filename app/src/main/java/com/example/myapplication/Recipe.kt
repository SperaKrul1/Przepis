package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val description: String,
    val ingredients: String,
    val imageUrl: String? = null,

    val isFavorite: Boolean = false,
    val isCustom: Boolean = true,

    val tags: String = "",

    val rating: Float = 0f   // ocena 0.0–5.0 w krokach co 0.5
)
