---
name: migrate-mvvm-to-orbit-mvi
description: >-
  Migrates an Android ViewModel from MVVM (LiveData/StateFlow) to Orbit MVI
  (ContainerHost + State/Action/SideEffect). Includes rewriting the ViewModel,
  creating MVI contracts, converting the Fragment to a thin ComposeView host,
  building the Compose screen, and deleting legacy XML. Use when the user asks
  to migrate a ViewModel to Orbit MVI, convert from MVVM to MVI, or mentions
  Orbit migration.
---

# Migrate MVVM ViewModel to Orbit MVI

Converts an Android screen from LiveData/StateFlow MVVM to Orbit MVI with Compose UI.

## Prerequisites

Before starting, read the project docs to understand conventions:

- `.cursor/docs/PROJECT_OVERVIEW.md` — navigation, DI
- `.cursor/docs/APP_CORE.md` — shared components, theme
- The doc for the feature being migrated (e.g. `APP_GOAL_FEATURE.md`)

Identify the reference pattern — an already-migrated screen in the project. Find it by searching for `ContainerHost` in `**/*ViewModel.kt`.

## Migration steps

### Step 1: Audit the current code

Read these files fully:

1. The **Fragment** — understand navigation callbacks, `registerForActivityResult`, `setFragmentResult`, arguments
2. The **ViewModel** — catalog every `LiveData`/`StateFlow`, public method, use case
3. The **XML layout** — note every field, card, button, click handler

List every piece of UI state and every user action.

### Step 2: Create MVI contract files

Create **four files** in the feature package.

#### 2a. UI model (`model/<Feature>Ui.kt`)

A plain `data class` holding form/display fields only. No error state, no dialog visibility — those belong in State.

```kotlin
data class ExampleUi(
    val name: String = String.empty,
    val sum: String = String.empty,
    // ... other display fields
) {
    // Derived properties are fine:
    val hasImage: Boolean get() = imageUri != null || imagePath != null
}
```

Use default values so `initial()` in State can just call `ExampleUi()`.

#### 2b. Error enums (`model/<Feature>Error.kt`)

One `enum class` per validated field. **Never use `String` for errors.**

```kotlin
enum class NameError { EMPTY }
enum class SumError { ZERO, INVALID, NEGATIVE }
```

The Compose layer maps enum → `stringResource` via `@Composable` extension:

```kotlin
@Composable
fun NameError.message(): String = when (this) {
    NameError.EMPTY -> stringResource(R.string.youmustentername)
}
```

#### 2c. State (`<Feature>State.kt`)

```kotlin
data class ExampleState(
    val data: ExampleUi,          // form fields grouped into UI model
    val nameError: NameError?,    // typed error enums, not String
    val sumError: SumError?,
    val isSomeSheetVisible: Boolean,
    val isEditMode: Boolean,      // mode flags stay in State
) {
    companion object {
        fun initial() = ExampleState(
            data = ExampleUi(),
            nameError = null,
            // ...
        )
    }
}
```

Rules:
- **Errors**: typed enums, nullable. `null` = no error.
- **UI-only text** (title, labels): compute inside `@Composable` from State fields (e.g. `if (state.isEditMode) stringResource(R.string.edit)`). Don't store localized strings in State.
- **Dialog/sheet visibility**: boolean fields in State, not side effects — so Compose controls them declaratively.
- Group form fields into a UI model `data class` to keep State flat.
- Use extension functions from the project's `common/Kotlin.kt` (e.g. `Date.toDMYFormat()`, `Double.format()`) instead of creating formatters in the ViewModel.

#### 2d. Action (`<Feature>Action.kt`)

```kotlin
sealed interface ExampleAction {
    data class NameChanged(val value: String) : ExampleAction
    data object SaveClick : ExampleAction
    data object BackClick : ExampleAction
    // ...
}
```

One subtype per user interaction. Use `data object` for no-arg actions, `data class` for parameterized ones.

#### 2e. SideEffect (`<Feature>SideEffect.kt`)

Only for **one-shot events that leave the Compose tree**: navigation, launching system intents.

```kotlin
sealed interface ExampleSideEffect {
    data class NavigateBack(val result: SomeResult? = null, val saved: Boolean = false) : ExampleSideEffect
    data object LaunchImagePicker : ExampleSideEffect
}
```

**Do NOT use side effects for**: dialogs, bottom sheets, date pickers — use State booleans instead.

### Step 3: Create form validation object

Extract validation into a pure `object` with functions returning error enums. This makes validation unit-testable without ViewModel or Android context.

```kotlin
object ExampleFormValidation {
    fun validateName(name: String): NameError? =
        if (name.isEmpty()) NameError.EMPTY else null

    fun validateSum(text: String): Pair<Double?, SumError?> { ... }
}
```

### Step 4: Create mappers

Create `mapper/<Feature>Mapper.kt` with extension functions:

- `DomainModel.toFeatureUi(...)` — domain → UI model (for edit mode init)
- `FeatureUi.toDomain(...)` — UI model → domain (for saving)

Keep mapping logic out of the ViewModel.

### Step 5: Rewrite ViewModel

Replace the ViewModel **in place**. Follow this structure:

```kotlin
@HiltViewModel
class ExampleViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val someUseCase: SomeUseCase,
) : ViewModel(), ContainerHost<ExampleState, ExampleSideEffect> {

    override val container = container<ExampleState, ExampleSideEffect>(
        initialState = ExampleState.initial(),
        onCreate = { /* read args from savedStateHandle, call init intents */ },
    )

    fun onAction(action: ExampleAction) = when (action) {
        is ExampleAction.NameChanged -> onNameChanged(action.value)
        ExampleAction.SaveClick -> onSaveClick()
        // exhaustive when — compiler enforces handling every action
    }

    private fun onNameChanged(value: String) = intent {
        reduce { state.copy(data = state.data.copy(name = value), nameError = null) }
    }

    private fun onSaveClick() = intent {
        // 1. validate using FormValidation object
        // 2. if errors → reduce with errors, return@intent
        // 3. if valid → call use case in withContext(Dispatchers.IO)
        // 4. postSideEffect(NavigateBack(saved = true))
    }
}
```

Key rules:
- **Single entry point**: `fun onAction(action)` with exhaustive `when`
- **All mutations**: `intent { reduce { state.copy(...) } }`
- **Navigation**: `postSideEffect(...)`
- **Fragment args**: read from `SavedStateHandle`, not `arguments`
- **No `context.getString()`** for error messages — use error enums
- **I/O**: wrap in `withContext(Dispatchers.IO)`
- Remove all `LiveData`/`MutableLiveData`/`MutableStateFlow` fields

### Step 6: Create Compose Screen

New file: `screen/<Feature>Screen.kt`

```kotlin
@Composable
fun ExampleScreen(vm: ExampleViewModel) {
    val state by vm.collectAsState()
    ExampleContent(state = state, onAction = vm::onAction)
}

@Composable
internal fun ExampleContent(
    state: ExampleState,
    onAction: (ExampleAction) -> Unit,
) {
    // Scaffold + DebtBookLargeTopAppBar + content
    // Derive title from state: if (state.isEditMode) stringResource(R.string.edit) else ...
    // Map error enums to strings here: error?.message()
}
```

Rules:
- `ExampleScreen` only wires VM → content. No logic.
- `ExampleContent` takes `state` + `onAction` — fully previewable.
- Reuse project shared components: `DebtBookLargeTopAppBar`, `SettingsPickerBottomSheet`, `DebtBookBottomSheet`.
- Use **Coil** (`AsyncImage`) not Glide for images.
- Use Material3 `DatePickerDialog` driven by `state.isDatePickerVisible`.
- Add `@Preview` + `@PreviewDark` for key states: create, edit, error.

### Step 7: Rewrite Fragment as ComposeView host

Rewrite the Fragment **in place** to be thin:

```kotlin
@AndroidEntryPoint
class ExampleFragment : Fragment() {
    private val vm by viewModels<ExampleViewModel>()

    override fun onCreateView(...): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(DisposeOnViewTreeLifecycleDestroyed)
            setContent { DebtBookTheme { ExampleScreen(vm = vm) } }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launchOnLifecycleStarted {
            vm.container.sideEffectFlow.collect { effect ->
                when (effect) {
                    is NavigateBack -> { setFragmentResult(...); onClickListener?.onBackButtonClick() }
                    LaunchImagePicker -> activityResultLauncher.launch("image/*")
                }
            }
        }
    }
}
```

- Keep `registerForActivityResult` in Fragment (Android API requirement).
- Keep `setFragmentResult` for communicating back to parent fragments.
- Simplify the callback interface — remove methods now handled by Compose (e.g. `onTickVibration` if `SettingsPickerBottomSheet` handles it).

### Step 8: Delete XML layout

1. Delete `res/layout/fragment_<feature>.xml`
2. Remove `tools:layout` reference from `nav_graph.xml`
3. Verify no other file references the deleted layout

### Step 9: Update documentation

Per workspace rules in `update-docs.mdc`, update the relevant doc in `.cursor/docs/`:

- Add new files to the structure tree
- Update ViewModel section with State/Action/SideEffect tables
- Remove deleted XML from layouts list

## File summary template

| Action  | File |
|---------|------|
| NEW     | `feature/model/<Feature>Ui.kt` |
| NEW     | `feature/model/<Feature>Error.kt` |
| NEW     | `feature/<Feature>State.kt` |
| NEW     | `feature/<Feature>Action.kt` |
| NEW     | `feature/<Feature>SideEffect.kt` |
| NEW     | `feature/<Feature>FormValidation.kt` |
| NEW     | `feature/mapper/<Feature>Mapper.kt` |
| NEW     | `feature/screen/<Feature>Screen.kt` |
| REWRITE | `feature/<Feature>ViewModel.kt` |
| REWRITE | `feature/<Feature>Fragment.kt` |
| DELETE  | `res/layout/fragment_<feature>.xml` |
| UPDATE  | `.cursor/docs/APP_<MODULE>_FEATURE.md` |

## Principles (always follow)

1. **State is the single source of truth** — no LiveData, no split state between VM fields and State
2. **Errors are typed enums** — never `String`; resolve to strings in Compose via `@Composable` extensions
3. **UI-only text computed in Compose** — titles, labels derived from State flags + `stringResource`
4. **Dialogs/sheets are state-driven** — boolean in State, not side effects
5. **Side effects only for leaving Compose** — navigation, system intents
6. **Validation is a pure object** — testable without ViewModel or Context
7. **Mappers separate domain ↔ UI** — keep conversion logic out of ViewModel
8. **Use project extensions** — `Date.toDMYFormat()`, `Double.format()`, `String.empty` from `common/Kotlin.kt`
