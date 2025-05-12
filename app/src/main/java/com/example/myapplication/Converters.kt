// Converters.kt
package com.example.myapplication

import androidx.room.TypeConverter

object Converters {
    // (jeśli masz już inne konwertery, zostaw je tu obok)
    @TypeConverter fun fromRecipeType(type: RecipeType): String = type.name
    @TypeConverter fun toRecipeType(name: String): RecipeType = RecipeType.valueOf(name)
}
