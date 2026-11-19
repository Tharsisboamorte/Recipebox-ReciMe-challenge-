# RecipeBox

A native Android app for browsing, searching, and filtering cooking recipes, built with **Kotlin** and **Jetpack Compose**. Recipe data is loaded from a bundled JSON file that is treated as if it were a real search API.

## Features

- Browse recipes in a responsive, adaptive grid.
- Full-text search across recipe **titles, descriptions, and cooking instructions**.
- Filters:
  - Vegetarian only
  - Servings range (min/max)
  - Must-include ingredients
  - Must-not-include ingredients
- Rich recipe detail screen with ingredients and numbered instructions.
- Smooth motion: shared-element transition from list card to detail, crossfading images, press feedback on cards, and animated grid re-ordering as filters change.

## Setup

Requirements: Android Studio (latest stable) with JDK 11+ and an Android SDK that includes API 36.

```bash
# Build a debug APK
./gradlew assembleDebug

# Run the unit tests (search/filter + JSON parsing)
./gradlew testDebugUnitTest
```

The generated APK is at `app/build/outputs/apk/debug/app-debug.apk`. Open the project in Android Studio and run the `app` configuration on an emulator/device (min SDK 26).

> Note: recipe images are loaded from the network (placeholder stock photos), so the device/emulator needs internet access to display them. The app functions fully offline otherwise and shows a themed placeholder when an image is unavailable.

## Architecture

The app follows **MVVM** with a clean separation between data, domain, and presentation, wired together with **Koin** for dependency injection.

```
UI (Compose screens) -> ViewModels -> RecipeRepository -> RecipeApi -> assets/recipes.json
```

```mermaid
flowchart LR
  UI[Compose Screens] --> VM[ViewModels]
  VM --> Repo[RecipeRepository]
  Repo --> Api[RecipeApi]
  Api --> Json[assets/recipes.json]
```

### Package layout (`com.tharsis.recipebox`)

| Package | Responsibility |
|---------|----------------|
| `data/model` | Clean domain models (`Recipe`, `DietaryAttributes`). |
| `data/api` | `RecipeApi` contract, `LocalRecipeApi` (JSON-backed impl), `RecipeSearchQuery`, pure `RecipeFilter`, `AssetReader`. |
| `data/api/dto` | Serialization DTOs + mappers to the domain model. |
| `data/repository` | `RecipeRepository`  single source of truth for the UI. |
| `di` | Koin `appModule`. |
| `ui/list` | List screen, search bar, filter sheet, `RecipeListViewModel`. |
| `ui/detail` | Detail screen + `RecipeDetailViewModel`. |
| `ui/components` | Reusable composables (image, badges, state views, shared-element helpers). |
| `ui/navigation` | `RecipeNavHost` + routes. |
| `ui/preview` | Static sample data for `@Preview`. |

### Why this shape

Even though data comes from a local file, the data layer is modeled as a suspendable, query-driven API (`RecipeApi.searchRecipes(query)`). Swapping the bundled JSON for a real backend is a one-line change in `di/AppModule.kt`, no UI or ViewModel changes required.

## Key design decisions

- **MVVM + unidirectional data flow.** Each screen observes a single immutable UI-state object exposed as a `StateFlow`. ViewModels own all state (search text + filters) and transform it into query results.
- **Koin for DI.** Lightweight and idiomatic for a Compose app; no code generation. The detail ViewModel receives its recipe id as a Koin parameter from the navigation argument, keeping it free of navigation concerns.
- **kotlinx.serialization** for JSON. No Retrofit/OkHttp are used for data, matching the "no real API call" constraint. A DTO layer keeps the JSON contract decoupled from the domain model.
- **Search modeled as an endpoint.** `RecipeFilter` is a pure, framework-free object so the search semantics are unit-testable and identical to what a server-side search would return. Filtering runs off the main thread via the IO dispatcher.
- **Coil 3** for image loading (crossfade, caching, placeholders) the most idiomatic Compose image loader.
- **Native Compose motion** (no extra animation SDK): `SharedTransitionLayout` for the list→detail hero transition, `animateItem()` for grid changes, press-scale on cards, and nav fade/slide transitions. Search input is debounced (250 ms) so results update smoothly while typing.

## Filter semantics

- **Text**: case-insensitive substring match against title + description + instructions.
- **Include ingredients**: a recipe must contain **all** listed ingredients (substring match).
- **Exclude ingredients**: a recipe must contain **none** of the listed ingredients.
- **Servings**: inclusive `min`/`max`. A full-range slider (1–12) means "no servings filter".
- Multiple filters combine with **AND** semantics.

## Assumptions & tradeoffs

- The bundled `assets/recipes.json` plays the role of an API response; parsing happens once and is cached in memory.
- Filtering is done in memory over the full list. For a real backend this would move server-side with pagination; it's intentionally simple here since the dataset is small.
- Image URLs point to placeholder stock photos (picsum.photos) to demonstrate remote loading; production data would ship real recipe photography.
- Dietary attributes are modeled as explicit booleans (vegetarian, vegan, gluten-free, dairy-free) rather than free-form tags, for type-safe filtering. Only the vegetarian filter is surfaced in the UI per the requirements, but the model supports more.
- Servings range caps at 12 in the UI for a usable slider; the data layer itself has no cap.

## Known limitations

- No persistence (favorites, history) or offline image caching beyond Coil's default.
- No server-side pagination/sorting; the full collection is loaded at once.
- Text search is substring-based (no fuzzy matching, stemming, or relevance ranking).
- Instrumented UI tests are not included; coverage focuses on the high-value search/filter and parsing logic via JVM unit tests.

## Tech stack

Kotlin · Jetpack Compose (Material 3) · Koin · kotlinx.serialization · Coroutines/Flow · Navigation Compose · Coil 3
