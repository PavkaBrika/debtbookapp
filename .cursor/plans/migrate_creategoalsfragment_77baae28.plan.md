---
name: Migrate CreateGoalsFragment
overview: Migrate CreateGoalsFragment from XML/ViewBinding to Jetpack Compose and convert its ViewModel from MVVM (LiveData) to MVI Orbit, following the established project patterns from GoalsScreen/GoalsViewModel.
todos:
  - id: create-state
    content: Create CreateGoalsState.kt, CreateGoalsAction.kt, CreateGoalsSideEffect.kt in goal/create/
    status: completed
  - id: rewrite-vm
    content: Rewrite CreateGoalsViewModel to Orbit MVI ContainerHost with State/Action/SideEffect, move validation + save logic inside intents
    status: pending
  - id: create-screen
    content: Create goal/create/screen/CreateGoalsScreen.kt -- Compose UI with DebtBookLargeTopAppBar, form fields, photo (Coil), currency sheet, date picker, FAB, previews
    status: pending
  - id: rewrite-fragment
    content: Rewrite CreateGoalsFragment as thin ComposeView host -- collect sideEffectFlow for navigation, keep registerForActivityResult for image picker
    status: pending
  - id: delete-xml
    content: Delete fragment_create_goal.xml, verify no other references
    status: pending
  - id: update-docs
    content: Update APP_GOAL_FEATURE.md with new file structure and ViewModel changes
    status: pending
isProject: false
---

# Migrate CreateGoalsFragment to Compose + Orbit MVI

## Scope

Convert `CreateGoalsFragment` (XML + LiveData MVVM) into a Compose screen with Orbit MVI, matching the pattern established by `GoalsScreen` / `GoalsViewModel`.

**Current files:**

- [CreateGoalsFragment.kt](app/src/main/java/com/breckneck/debtbook/goal/create/CreateGoalsFragment.kt) -- 435 lines, XML-based
- [CreateGoalsViewModel.kt](app/src/main/java/com/breckneck/debtbook/goal/create/CreateGoalsViewModel.kt) -- 123 lines, LiveData MVVM
- [fragment_create_goal.xml](app/src/main/res/layout/fragment_create_goal.xml) -- 345 lines

**Reference pattern (already migrated):**

- [GoalsViewModel.kt](app/src/main/java/com/breckneck/debtbook/goal/main/GoalsViewModel.kt) -- `ContainerHost<State, SideEffect>`, `onAction(Action)`, `intent { reduce {} }`, `postSideEffect`
- [GoalsScreen.kt](app/src/main/java/com/breckneck/debtbook/goal/main/screen/GoalsScreen.kt) -- `collectAsState()`, delegates actions to VM
- [GoalsFragment.kt](app/src/main/java/com/breckneck/debtbook/goal/main/GoalsFragment.kt) -- `ComposeView`, collects `sideEffectFlow` for navigation

---

## Step 1: Create State / Action / SideEffect files

**Package:** `com.breckneck.debtbook.goal.create`

### CreateGoalsState.kt

```kotlin
data class CreateGoalsState(
    val name: String,
    val nameError: String?,
    val sum: String,
    val sumError: String?,
    val savedSum: String,
    val savedSumError: String?,
    val currency: String,
    val currencyDisplayName: String,
    val selectedCurrencyIndex: Int,
    val isCurrencySheetVisible: Boolean,
    val goalDate: Date?,
    val goalDateFormatted: String?,
    val imageUri: Uri?,
    val imagePath: String?,
    val hasImage: Boolean,
    val isEditMode: Boolean,
    val title: String,       // "Add new goal" or "Edit"
) {
    companion object {
        fun initial() = CreateGoalsState(...)
    }
}
```

### CreateGoalsAction.kt

```kotlin
sealed interface CreateGoalsAction {
    data class NameChanged(val value: String) : CreateGoalsAction
    data class SumChanged(val value: String) : CreateGoalsAction
    data class SavedSumChanged(val value: String) : CreateGoalsAction
    data object CurrencyClick : CreateGoalsAction
    data class CurrencySelected(val index: Int) : CreateGoalsAction
    data object DismissCurrencySheet : CreateGoalsAction
    data object DateClick : CreateGoalsAction
    data class DateSelected(val date: Date) : CreateGoalsAction
    data class ImagePicked(val uri: Uri) : CreateGoalsAction
    data object DeleteImage : CreateGoalsAction
    data object PhotoCardClick : CreateGoalsAction
    data object SaveClick : CreateGoalsAction
    data object BackClick : CreateGoalsAction
}
```

### CreateGoalsSideEffect.kt

```kotlin
sealed interface CreateGoalsSideEffect {
    data object NavigateBack : CreateGoalsSideEffect
    data object LaunchImagePicker : CreateGoalsSideEffect
    data object ShowDatePicker : CreateGoalsSideEffect
}
```

---

## Step 2: Rewrite ViewModel as Orbit ContainerHost

**File:** [CreateGoalsViewModel.kt](app/src/main/java/com/breckneck/debtbook/goal/create/CreateGoalsViewModel.kt) -- rewrite in place

Key changes from current LiveData VM:

- `ContainerHost<CreateGoalsState, CreateGoalsSideEffect>` + `container(initialState, onCreate = { init logic })`
- Single `fun onAction(action: CreateGoalsAction)` entry point (like `GoalsViewModel.onAction`)
- All mutations via `intent { reduce { state.copy(...) } }`
- Navigation/launching via `postSideEffect(...)`
- `SavedStateHandle` to receive `isEditGoal: Boolean` and `goal: Goal` args (replaces `arguments?.getBoolean/getSerializable`)
- Validation logic (`isAllFieldsFilledRight`) moves into VM `onSaveClick()` intent
- `savePhotoToInternalStorage` -- accept `Context` from the Compose side or inject `@ApplicationContext`; keep I/O in `withContext(Dispatchers.IO)`
- Use cases stay the same: `SetGoal`, `UpdateGoal`, `GetDefaultCurrency`
- Currency list/names: pass from resources via `@ApplicationContext` context, or accept as a constructor/init parameter

---

## Step 3: Create Compose Screen

**New file:** `app/src/main/java/com/breckneck/debtbook/goal/create/screen/CreateGoalsScreen.kt`

### Structure

```
CreateGoalsScreen(vm)             -- collectAsState(), delegates actions
  CreateGoalsContent(state, onAction)
    Scaffold
      DebtBookLargeTopAppBar(title, onBackClick)  -- reuse existing component
      Column (scrollable)
        NameCard       -- OutlinedTextField, maxLength=20, error
        PhotoCard      -- AsyncImage(coil) / placeholder, delete button
        SumCard        -- OutlinedTextField x2 (sum, savedSum) + currency text button
        DateCard       -- date display, clickable
      FAB (check icon) -- onAction(SaveClick)
    if (state.isCurrencySheetVisible)
      SettingsPickerBottomSheet(...)   -- reuse existing shared component
```

### Key Compose elements

- `DebtBookLargeTopAppBar` with `onBackClick` -- already exists in [DebtBookLargeTopAppBar.kt](app/src/main/java/com/breckneck/debtbook/core/ui/components/DebtBookLargeTopAppBar.kt)
- `SettingsPickerBottomSheet` for currency -- already exists in [SettingsPickerBottomSheet.kt](app/src/main/java/com/breckneck/debtbook/core/ui/components/SettingsPickerBottomSheet.kt)
- `AsyncImage` (Coil) replaces Glide for goal photo
- `rememberLauncherForActivityResult(GetContent())` for image picker -- triggered by `LaunchImagePicker` side effect
- `DatePickerDialog` (Material3 Compose) -- triggered by `ShowDatePicker` side effect, or inline state-based
- `OutlinedTextField` with `isError` and `supportingText` for validation
- Decimal filtering in `onValueChange` for sum fields (replaces `TextWatcher`)

### Previews

- Light + dark via `@Preview` / `@PreviewDark`
- Create mode preview
- Edit mode preview (with pre-filled fields)

---

## Step 4: Update Fragment as thin ComposeView host

**File:** [CreateGoalsFragment.kt](app/src/main/java/com/breckneck/debtbook/goal/create/CreateGoalsFragment.kt) -- rewrite in place

Follow `GoalsFragment` pattern:

- `onCreateView` returns `ComposeView` with `DebtBookTheme { CreateGoalsScreen(vm) }`
- `onViewCreated` collects `vm.container.sideEffectFlow`:
  - `NavigateBack` -> `onClickListener.onBackButtonClick()` + `setFragmentResult("goalsFragmentKey", ...)` + `setFragmentResult("goalDetailsFragmentKey", ...)`
  - `LaunchImagePicker` -> launch `getImageUriActivityResult`
  - `ShowDatePicker` -> show Material3 DatePickerDialog (can be state-driven in Compose instead)
- Remove `OnButtonClickListener` interface -- simplify to only `onBackButtonClick()` and `onTickVibration()` (or remove vibration if no longer needed for radio buttons, since `SettingsPickerBottomSheet` handles this internally)
- Keep `registerForActivityResult(GetContent())` in Fragment for image picking (Android API requirement)
- Pass `isEditGoal` and `goal` args to VM via `SavedStateHandle` (or pass during init)

---

## Step 5: Delete XML layout

- Delete [fragment_create_goal.xml](app/src/main/res/layout/fragment_create_goal.xml)
- Verify no other file references `R.layout.fragment_create_goal` (only `CreateGoalsFragment` uses it)
- `dialog_setting.xml` stays -- still used by non-migrated fragments

---

## Step 6: Update documentation

Per workspace rules, update:

- [APP_GOAL_FEATURE.md](.cursor/docs/APP_GOAL_FEATURE.md) -- new file structure (`screen/CreateGoalsScreen.kt`, state/action/sideEffect files), updated ViewModel description
- [APP_CORE.md](.cursor/docs/APP_CORE.md) -- if DI bindings change (unlikely, Hilt auto-discovers `@HiltViewModel`)

---

## File change summary


| Action  | File                                                           |
| ------- | -------------------------------------------------------------- |
| NEW     | `goal/create/CreateGoalsState.kt`                              |
| NEW     | `goal/create/CreateGoalsAction.kt`                             |
| NEW     | `goal/create/CreateGoalsSideEffect.kt`                         |
| REWRITE | `goal/create/CreateGoalsViewModel.kt` (LiveData -> Orbit MVI)  |
| NEW     | `goal/create/screen/CreateGoalsScreen.kt`                      |
| REWRITE | `goal/create/CreateGoalsFragment.kt` (XML -> ComposeView host) |
| DELETE  | `res/layout/fragment_create_goal.xml`                          |
| UPDATE  | `.cursor/docs/APP_GOAL_FEATURE.md`                             |


