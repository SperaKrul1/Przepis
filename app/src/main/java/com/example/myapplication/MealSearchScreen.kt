package com.example.myapplication.api

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.Recipe
import com.example.myapplication.RecipeViewModel

@Composable
fun MealSearchScreen(
    viewModel: RecipeViewModel,
    onBack: () -> Unit
) {
    val mealViewModel: MealViewModel = viewModel()
    val meals by mealViewModel.meals.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Szukaj dania (np. chicken, pasta...)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { mealViewModel.searchMeals(searchQuery) }) {
            Text("Szukaj")
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (meals.isEmpty() && searchQuery.isNotBlank()) {
            Text("Brak wyników dla \"$searchQuery\".")
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(meals) { meal ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(meal.strMeal, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        Image(
                            painter = rememberAsyncImagePainter(meal.strMealThumb),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Instrukcje: ${meal.strInstructions.take(200)}...")

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(onClick = {
                            val recipe = Recipe(
                                title = meal.strMeal,
                                description = meal.strInstructions.take(200),
                                ingredients = listOfNotNull(
                                    meal.strIngredient1,
                                    meal.strIngredient2,
                                    meal.strIngredient3,
                                    meal.strIngredient4
                                ).joinToString(", "),
                                imageUrl = meal.strMealThumb,
                                isCustom = false
                            )
                            viewModel.addRecipe(recipe)
                        }) {
                            Text("Zapisz do ulubionych")
                        }

                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Powrót do menu")
                }
            }
        }
    }
}
