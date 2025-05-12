package com.example.myapplication

import android.widget.RatingBar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun EditRecipeScreen(
    recipe: Recipe,
    viewModel: RecipeViewModel,
    onDone: () -> Unit
) {
    var title by remember { mutableStateOf(recipe.title) }
    var description by remember { mutableStateOf(recipe.description) }
    var ingredients by remember { mutableStateOf(recipe.ingredients) }
    var rating by remember { mutableStateOf(recipe.rating) }
    // stan dropdowna dla kategorii
    var category by remember { mutableStateOf(recipe.category) }
    var menuExpanded by remember { mutableStateOf(false) }

    val isFormValid = title.isNotBlank() && description.isNotBlank() && ingredients.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Edytuj przepis", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Tytuł") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Opis") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = ingredients,
            onValueChange = { ingredients = it },
            label = { Text("Składniki") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        // --- sekcja: wybór kategorii ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Kategoria:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(0.3f)
            )
            Row(
                modifier = Modifier
                    .weight(0.7f)
                    .clickable { menuExpanded = true }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(category.label, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Wybierz kategorię"
                )
            }
        }
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            RecipeType.values().forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt.label) },
                    onClick = {
                        category = opt
                        menuExpanded = false
                    }
                )
            }
        }
        // ----------------------

        Spacer(Modifier.height(16.dp))

        // --- sekcja oceny (wyśrodkowana) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Twoja ocena: ${"%.1f".format(rating)} / 5",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(4.dp))
            AndroidView(
                factory = { ctx ->
                    RatingBar(ctx).apply {
                        numStars = 5
                        stepSize = 0.5f
                        this.rating = rating
                        setOnRatingBarChangeListener { _, newRating, _ ->
                            rating = newRating
                        }
                    }
                },
                update = { view ->
                    if (view.rating != rating) view.rating = rating
                },
                modifier = Modifier.wrapContentWidth()
            )
        }
        // ----------------------

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val updated = recipe.copy(
                    title = title.trim(),
                    description = description.trim(),
                    ingredients = ingredients.trim(),
                    rating = rating,
                    category = category
                )
                viewModel.updateRecipe(updated)
                onDone()
            },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Zapisz zmiany")
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Anuluj")
        }
    }
}
