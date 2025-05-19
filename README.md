# Recipe Collection App

A modern Android application built with Jetpack Compose that lets you create, manage and share your favorite recipes, complete with images, ratings, tags and filters.

---

## Features

### Persistent Storage (Room Database)
- **Recipe Entity**  
  - Title, description, ingredients  
  - List of image URIs (supports multiple photos)  
  - `isFavorite` flag  
  - `rating` (0.0–5.0 in 0.5 increments)  
  - `category` (enum: Breakfast, Lunch, Dinner, Dessert, Drink, Vegan, etc.)  
- **Type Converters**  
  - Serializes/deserializes lists of image URIs and enum values  
- **DAO**  
  - CRUD operations  
  - Observes recipes as a `Flow<List<Recipe>>`  

### MVVM Architecture
- **RecipeViewModel**  
  - Exposes `recipes: StateFlow<List<Recipe>>`  
  - `addRecipe()`, `updateRecipe()`, `deleteRecipe()`  
- **Repository Layer** (optional)  

### Compose UI Screens
1. **HomeScreen**  
   - Navigation drawer: Home, Add Recipe, Search Online  
2. **RecipeListScreen**  
   - **Sorting**: by Favorites, Alphabetical, Rating ↑/↓  
   - **Filtering**: by `category` tag or “All”  
   - Shows title, favorite ⭐ toggle, star-rating display  
   - Tap to open details, long-press for Edit/Delete menu  
3. **RecipeDetailScreen**  
   - Full-screen view: title, category tag, carousel of images, rating, description, ingredients  
   - Share button: sends both text and image via Android Sharesheet  
4. **AddRecipeScreen**  
   - Form fields: Title, Description, Ingredients  
   - **Image Picker**: pick multiple images from gallery  
   - **RatingBar** (0–5 ⭐ in 0.5 steps)  
   - **Category Selector** dropdown  
5. **EditRecipeScreen**  
   - Same form as Add, pre-filled  
   - Remove/add individual images  
   - Update rating & category  

### Online Search Integration
- **TheMealDB API**  
  - `MealSearchScreen`: search meals by name, fetch details  
  - Import fetched recipes into local database  

### Advanced UI Interactions
- **Multi-select Image Picker** via `ActivityResultContracts.GetMultipleContents`  
- **Share Intent** with `ACTION_SEND` for text + image URI (with URI permissions)  
- **Tap** interactions to expand/collapse recipe cards  

---

## Architecture Overview


- **ViewModel** holds the single source of truth and exposes recipes as a reactive flow.  
- **Room** persists recipes across app restarts, with destructive migrations for dev.  
- **Compose Navigation** switches between screens based on `currentScreen` state.  

---

## Getting Started

1. **Clone the repo**  
   ```bash
   git clone https://github.com/SperaKrul1/Przepis.git
   
