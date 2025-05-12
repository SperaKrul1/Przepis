package com.example.myapplication

import android.net.Uri
import android.widget.RatingBar
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import coil.compose.rememberAsyncImagePainter

@Composable
fun AddRecipeScreen(
    viewModel: RecipeViewModel,
    onRecipeAdded: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var rating by remember { mutableStateOf(0f) }     // ← tu dodajemy ocenę
    var category    by remember { mutableStateOf(RecipeType.LUNCH) }
    var menuExpanded by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val isFormValid = title.isNotBlank()
            && description.isNotBlank()
            && ingredients.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dodaj nowy przepis", style = MaterialTheme.typography.headlineSmall)
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
        Spacer(Modifier.height(8.dp))

        Button(onClick = { launcher.launch("image/*") }) {
            Text("Wybierz zdjęcie z galerii")
        }
        imageUri?.let {
            Spacer(Modifier.height(8.dp))
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

// --- sekcja: wybór kategorii ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // statyczny label
            Text(
                "Kategoria:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(0.3f)
            )
            // obszar klikany do rozwijania menu
            Row(
                modifier = Modifier
                    .weight(0.7f)
                    .clickable { menuExpanded = true }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    category.label,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Wybierz kategorię"
                )
            }
        }

// sam dropdown pod spodem
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

        // --- nowa sekcja: ocena przepisu ---
        Text("Twoja ocena: ${"%.1f".format(rating)} / 5", style = MaterialTheme.typography.bodyLarge)
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
            update = { it.rating = rating },
            modifier = Modifier.padding(vertical = 8.dp)
        )
        // ---------------------------------------

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val recipe = Recipe(
                    title = title.trim(),
                    description = description.trim(),
                    ingredients = ingredients.trim(),
                    imageUrl = imageUri?.toString(),
                    isCustom = true,
                    tags = "",
                    rating = rating     // ← i tu przekazujemy ocenę
                )
                viewModel.addRecipe(recipe)
                onRecipeAdded()
            },
            enabled = isFormValid
        ) {
            Text("Zapisz przepis")
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = onRecipeAdded) {
            Text("Anuluj i wróć")
        }
    }
}
