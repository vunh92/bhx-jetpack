# AGENTS.md - BHX Jetpack Android Project

This file contains guidelines for AI agents working in this repository.

## Project Overview

- **Architecture**: Clean Architecture with MVVM
- **Tech Stack**: Kotlin, Jetpack Compose, Hilt, Room, Retrofit, Navigation Compose
- **Min SDK**: 33 (Android 13)
- **Target SDK**: 35

## Directory Structure

```
app/src/main/java/com/vunh/jetpack/bhx/
├── data/           # Data layer (repositories impl, remote, local)
├── di/             # Hilt dependency injection modules
├── domain/         # Domain layer (models, repositories interfaces, use cases)
├── presentation/   # UI layer (screens, viewmodels, common components)
└── ui/theme/       # Compose theme (colors, typography)
```

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run a specific test class
./gradlew test --tests "com.vunh.jetpack.bhx.ExampleUnitTest"

# Run a specific test method
./gradlew test --tests "com.vunh.jetpack.bhx.ExampleUnitTest.addition_isCorrect"

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Clean and rebuild
./gradlew clean assembleDebug

# Run lint
./gradlew lint

# Build with dependency report
./gradlew dependencies > dependencies.txt
```

## Code Style Guidelines

### Kotlin Conventions

- **Use `object` over companion objects** for module-level singletons
- **Prefer extension functions** for utility operations
- **Use `invoke` operator** in UseCases to make them callable like functions
- **Use `@Inject constructor`** for all injectable classes
- **Prefer `flow` over LiveData** for reactive data streams

### Naming Conventions

- **ViewModels**: `ScreenName + ViewModel` (e.g., `HomeViewModel`)
- **Screens**: `ScreenName + Screen` (e.g., `HomeScreen`)
- **UseCases**: `Action + UseCase` (e.g., `SyncPostsUseCase`, `ObservePostsUseCase`)
- **Repository interfaces**: `DomainName + Repository` (e.g., `HomeRepository`)
- **Repository implementations**: `DomainName + RepositoryImpl`
- **State classes**: `Name + State` (e.g., `UiState`)
- **Event classes**: `Name + Event`

### Compose Guidelines

- **Screen composables**: Public, take ViewModel as parameter with default `hiltViewModel()`
- **Helper composables**: Private or internal, prefixed with descriptive name
- **Use `Modifier`** consistently as the last parameter
- **Extract complex UI** into separate composable functions
- **Use `remember` and `rememberSaveable`** appropriately for state
- **Prefer `collectAsState()`** over `collectAsStateWithLifecycle()`

### Import Organization

```kotlin
// 1. Android/Compose imports
import androidx.compose.foundation.background
import androidx.compose.material3.*

// 2. Hilt/Dagger imports
import dagger.hilt.android.lifecycle.HiltViewModel

// 3. Kotlin Standard Library
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// 4. Project imports (grouped by package depth)
// - domain.models
// - domain.repository
// - domain.usecase
// - presentation.screens
// - presentation.common
import com.vunh.jetpack.bhx.domain.model.*
import com.vunh.jetpack.bhx.presentation.home.*
```

### Error Handling

- **ViewModels**: Wrap async operations in try-catch, emit error states
- **Repositories**: Let exceptions propagate, handle in UseCase/ViewModel
- **UI**: Show user-friendly error messages via Snackbar or Dialog
- **Coroutines**: Use `catch` operator on flows, handle in `finally` blocks

### State Management Pattern

```kotlin
// ViewModel
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observePostsUseCase: ObservePostsUseCase,
    private val syncPostsUseCase: SyncPostsUseCase
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun refreshPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                syncPostsUseCase()
            } catch (e: Exception) {
                // Handle error appropriately
            } finally {
                _isLoading.value = false
            }
        }
    }
}

// Screen
@Composable
fun HomeScreen(
    onMenuClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // UI implementation
}
```

### DI Module Pattern

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
```

### Color Guidelines

- **Primary Green**: `Color(0xFF008848)` - Main brand color
- **Accent Green**: `Color(0xFF8CC63F)` - Secondary brand color
- **Text Colors**: Black for primary, Gray for secondary
- **Use MaterialTheme colors** when possible, fallback to hardcoded colors

### Modifier Chaining

```kotlin
// Preferred: Each modifier on new line for readability
Card(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .clickable { onClick() },
    colors = CardDefaults.cardColors(containerColor = Color.White)
)

// Avoid chaining on same line when complex
```

### Documentation

- Add KDoc comments for public API (ViewModels, Screens, UseCases)
- No comments for implementation details unless complex
- Use `//` for inline comments, not block comments

## Testing Guidelines

- Place unit tests in `app/src/test/java/`
- Place instrumented tests in `app/src/androidTest/java/`
- Use JUnit 4 for unit tests
- Mock dependencies with mocking frameworks compatible with Kotlin
- Test naming: `methodName_condition_expectedResult`

## Common Tasks

### Adding a New Screen

1. Create screen composable in `presentation/screenname/`
2. Create ViewModel with `@HiltViewModel` annotation
3. Create/extend UseCases in `domain/usecase/`
4. Add navigation route in `MainActivity.kt`
5. Add bottom nav item if needed in `AppDestinations`

### Adding a New UseCase

1. Create class in `domain/usecase/`
2. Inject repository in constructor
3. Use `@Inject constructor` annotation
4. Implement `operator fun invoke()` for callable syntax
