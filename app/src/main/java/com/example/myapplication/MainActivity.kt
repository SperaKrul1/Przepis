package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.api.MealSearchScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

enum class SortOption(val label: String) {
    ALPHABETICAL("Alfabetycznie"),
    RATING_DESC("Ocena ↓"),
    RATING_ASC("Ocena ↑"),
    FAVORITES_FIRST("Ulubione")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // Inicjalizacja ViewModelu
                val viewModel: RecipeViewModel = viewModel(factory = viewModelFactory {
                    initializer { RecipeViewModel(application) }
                })
                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: RecipeViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val recipes by viewModel.recipes.collectAsState()

    var currentScreen by remember { mutableStateOf("home") }
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Menu", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))
                NavigationDrawerItem(
                    label = { Text("Strona główna") },
                    selected = currentScreen == "home",
                    onClick = {
                        currentScreen = "home"
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Dodaj przepis") },
                    selected = currentScreen == "add",
                    onClick = {
                        currentScreen = "add"
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Szukaj online") },
                    selected = currentScreen == "search",
                    onClick = {
                        currentScreen = "search"
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Zbiór Przepisów") },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (currentScreen) {
                    "home" -> RecipeListScreen(
                        recipes      = recipes,
                        onBack       = { /* tu nic nie musisz robić */ },
                        onDelete     = { viewModel.deleteRecipe(it) },
                        onEdit       = { selectedRecipe = it; currentScreen = "edit" },
                        onOpenDetail = { selectedRecipe = it; currentScreen = "detail" },
                        viewModel    = viewModel
                    )
                    "add" -> AddRecipeScreen(
                        viewModel = viewModel,
                        onRecipeAdded = { currentScreen = "home" }
                    )
                    "search" -> MealSearchScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = "home" }
                    )
                    "edit" -> selectedRecipe?.let { recipe ->
                        EditRecipeScreen(
                            recipe = recipe,
                            viewModel = viewModel,
                            onDone = {
                                currentScreen = "home"
                                selectedRecipe = null
                            }
                        )
                    }
                    "detail" -> selectedRecipe?.let { recipe ->
                        RecipeDetailScreen(
                            recipe = recipe,
                            onBack = {
                                currentScreen = "home"
                                selectedRecipe = null
                            }
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeListScreen(
    recipes: List<Recipe>,
    onBack: () -> Unit,
    onDelete: (Recipe) -> Unit,
    onEdit: (Recipe) -> Unit,
    onOpenDetail: (Recipe) -> Unit,
    viewModel: RecipeViewModel
) {
    // Stan dropdowna
    var sortOption by remember { mutableStateOf(SortOption.FAVORITES_FIRST) }
    var menuExpanded by remember { mutableStateOf(false) }

    // Posortowane przepisy wg wybranej opcji
    val sortedRecipes = remember(recipes, sortOption) {
        when (sortOption) {
            SortOption.ALPHABETICAL    -> recipes.sortedBy    { it.title.lowercase() }
            SortOption.RATING_DESC     -> recipes.sortedByDescending { it.rating }
            SortOption.RATING_ASC      -> recipes.sortedBy    { it.rating }
            SortOption.FAVORITES_FIRST -> recipes.sortedByDescending { it.isFavorite }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Nagłówek + sortowanie
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Twoje przepisy:",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )
                Text("Sortuj według:", Modifier.padding(end = 4.dp))
                Text(sortOption.label)
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = "Wybierz sortowanie")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    SortOption.values().forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt.label) },
                            onClick = {
                                sortOption = opt
                                menuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Lista przepisów
        items(sortedRecipes) { recipe ->
            var expanded by remember { mutableStateOf(false) }
            var showMenu by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .combinedClickable(
                        onClick = { onOpenDetail(recipe) },
                        onLongClick = { showMenu = true }
                    ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Tytuł + ulubione
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            recipe.title,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                viewModel.updateRecipe(
                                    recipe.copy(isFavorite = !recipe.isFavorite)
                                )
                            }
                        ) {
                            if (recipe.isFavorite) {
                                Icon(Icons.Filled.Star, contentDescription = "Ulubione")
                            } else {
                                Icon(Icons.Outlined.StarOutline, contentDescription = "Nieulubione")
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))

                    // Ocena gwiazdkami
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        (1..5).forEach { i ->
                            val half = i - 0.5f
                            when {
                                recipe.rating >= i    -> Icon(Icons.Filled.Star, contentDescription = null)
                                recipe.rating >= half -> Icon(Icons.Filled.StarHalf, contentDescription = null)
                                else                  -> Icon(Icons.Outlined.StarOutline, contentDescription = null)
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "%.1f".format(recipe.rating),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (expanded) {
                        Spacer(Modifier.height(8.dp))
                        recipe.imageUrl?.let { url ->
                            Image(
                                painter = rememberAsyncImagePainter(url),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                        Text(recipe.description, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        Text("Składniki:", style = MaterialTheme.typography.labelLarge)
                        Text(recipe.ingredients, style = MaterialTheme.typography.bodySmall)
                    }
                }

                // Menu kontekstowe
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edytuj") },
                        onClick = {
                            showMenu = false
                            onEdit(recipe)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Usuń") },
                        onClick = {
                            showMenu = false
                            onDelete(recipe)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeDetailScreen(
    recipe: Recipe,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {
                // budujemy tekst
                val shareText = buildString {
                    appendLine("Przepis: ${recipe.title}")
                    appendLine()
                    appendLine(recipe.description)
                    appendLine()
                    append("Składniki: ${recipe.ingredients}")
                }

                // tworzymy Intent
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND

                    // dołączamy tekst
                    putExtra(Intent.EXTRA_TEXT, shareText)

                    // jeżeli mamy URI obrazka, dołączamy go
                    recipe.imageUrl
                        ?.let { Uri.parse(it) }
                        ?.let { imageUri ->
                            putExtra(Intent.EXTRA_STREAM, imageUri)
                            type = context.contentResolver.getType(imageUri) ?: "image/*"
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        ?: run {
                            type = "text/plain"
                        }
                }

                context.startActivity(Intent.createChooser(intent, "Udostępnij przepis"))
            }) {
                Icon(Icons.Default.Share, contentDescription = "Udostępnij")
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(recipe.title, style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(4.dp))

        // **Nowa linia z wyświetleniem kategorii/tagu**
        Text(
            text = "Kategoria: ${recipe.category.label}",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(8.dp))

        recipe.imageUrl?.let { url ->
            Image(
                painter = rememberAsyncImagePainter(url),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
            Spacer(Modifier.height(8.dp))
        }

        Text("Ocena: %.1f".format(recipe.rating), style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(8.dp))

        Text(recipe.description, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))

        Text("Składniki:", style = MaterialTheme.typography.labelLarge)
        Text(recipe.ingredients, style = MaterialTheme.typography.bodySmall)
    }
}
