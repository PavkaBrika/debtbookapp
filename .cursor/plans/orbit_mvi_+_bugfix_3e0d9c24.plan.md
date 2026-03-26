---
name: Orbit MVI + Bugfix
overview: Add Orbit MVI library to the project, rewrite CreateFinanceCategoryViewModel to use Orbit's ContainerHost pattern (State + SideEffect), update the Compose screen to subscribe via collectAsState/collectSideEffect, and fix all bugs identified in the code review.
todos:
  - id: orbit-deps
    content: Add orbit-core, orbit-viewmodel, orbit-compose, orbit-test to app/build.gradle
    status: completed
  - id: orbit-contract
    content: Define CreateFinanceCategoryState data class and CreateFinanceCategorySideEffect sealed class
    status: completed
  - id: orbit-vm
    content: Rewrite CreateFinanceCategoryViewModel as ContainerHost with intent functions and validation logic
    status: completed
  - id: orbit-screen
    content: Update CreateFinanceCategoryScreen to use collectAsState/collectSideEffect instead of LiveData + rememberSaveable
    status: pending
  - id: fix-listener-npe
    content: Fix !! on onClickListener in CreateFinanceCategoryFragment (use safe call)
    status: pending
  - id: fix-check-contrast
    content: Compute checkmark tint based on color luminance in CategoryColorItem
    status: pending
  - id: fix-maxitems
    content: Remove hardcoded maxItemsInEachRow from FlowRow
    status: pending
  - id: fix-ripple
    content: Use Surface(onClick) instead of Modifier.clickable in CategoryImageItem
    status: pending
  - id: extract-section-error
    content: Extract repeated AnimatedVisibility error pattern into SectionError composable
    status: pending
  - id: update-docs
    content: Update APP_FINANCE_FEATURE.md with new Orbit MVI contract
    status: pending
  - id: verify-build
    content: Verify project compiles and previews render
    status: pending
isProject: false
---

# Orbit MVI Migration + Bugfix for CreateFinanceCategory

## 1. Add Orbit MVI dependencies

In [app/build.gradle](app/build.gradle), add to the `dependencies` block:

```groovy
// Orbit MVI
implementation "org.orbit-mvi:orbit-core:10.0.0"
implementation "org.orbit-mvi:orbit-viewmodel:10.0.0"
implementation "org.orbit-mvi:orbit-compose:10.0.0"
testImplementation "org.orbit-mvi:orbit-test:10.0.0"
```

No additional plugins required. Orbit works with existing `@HiltViewModel` -- it just adds the `ContainerHost` interface on top.

## 2. Rewrite ViewModel with Orbit MVI

File: [CreateFinanceCategoryViewModel.kt](app/src/main/java/com/breckneck/debtbook/finance/viewmodel/CreateFinanceCategoryViewModel.kt)

**Current problems:**

- State split between LiveData in VM and `rememberSaveable` in UI (two sources of truth)
- Validation logic lives in the composable lambda (35 lines)
- Save is async (`viewModelScope.launch`) but `onCategorySaved()` fires immediately (race)

**New contract:**

```kotlin
data class CreateFinanceCategoryState(
    val categoryName: String = "",
    val selectedImageIndex: Int? = null,
    val selectedImage: Int? = null,
    val selectedColorIndex: Int? = null,
    val selectedColor: String? = null,
    val financeCategoryState: FinanceCategoryState = FinanceCategoryState.EXPENSE,
    val nameError: String? = null,
    val imageError: String? = null,
    val colorError: String? = null,
)

sealed class CreateFinanceCategorySideEffect {
    data object CategorySaved : CreateFinanceCategorySideEffect()
}
```

**New ViewModel structure:**

- `ContainerHost<CreateFinanceCategoryState, CreateFinanceCategorySideEffect>`
- `container = container(CreateFinanceCategoryState())`
- Intent functions: `onNameChange(value)`, `onImageSelected(index, image)`, `onColorSelected(index, color)`, `setFinanceCategoryState(state)`, `onSaveClick()`
- `onSaveClick()` validates inside `intent { }`, calls `reduce` to set errors, and only after `withContext(IO) { setFinanceCategory.execute(...) }` succeeds does it `postSideEffect(CategorySaved)`
- Error strings: pass them as parameters to `onSaveClick()` from the UI layer (since string resources are a UI concern), OR define error types as sealed class and resolve strings in UI

**Fix -- race on save (bug P2):** `postSideEffect(CategorySaved)` is called AFTER `setFinanceCategory.execute()` returns inside the `intent` block, guaranteeing the DB write completes before navigation.

**Fix -- single source of truth (bug P1):** All form state (`categoryName`, errors, selections) now lives in Orbit State. No more `rememberSaveable` in the UI.

## 3. Update Compose Screen

File: [CreateFinanceCategoryScreen.kt](app/src/main/java/com/breckneck/debtbook/finance/presentation/CreateFinanceCategoryScreen.kt)

**Changes to `CreateFinanceCategoryScreen` (stateful wrapper):**

- Replace all `observeAsState()` + `rememberSaveable` with:

```kotlin
  val state by vm.collectAsState()
  vm.collectSideEffect { effect ->
      when (effect) {
          CreateFinanceCategorySideEffect.CategorySaved -> onCategorySaved()
      }
  }
  

```

- Pass `state.*` fields and `vm::onNameChange`, `vm::onImageSelected`, etc. to `CreateFinanceCategoryContent`
- Remove `LocalContext` import (no more Toasts)

`**CreateFinanceCategoryContent` signature stays the same** (pure stateless) -- only the caller changes.

## 4. Update Fragment

File: [CreateFinanceCategoryFragment.kt](app/src/main/java/com/breckneck/debtbook/finance/presentation/CreateFinanceCategoryFragment.kt)

**Fix -- `!!` on onClickListener (bug P1):**

- Change `onClickListener!!.onBackButtonClick()` to `onClickListener?.onBackButtonClick()` (2 places: lines 55, 64)

## 5. Fix UI bugs

All in [CreateFinanceCategoryScreen.kt](app/src/main/java/com/breckneck/debtbook/finance/presentation/CreateFinanceCategoryScreen.kt):

**Fix -- white checkmark invisible on light colors (bug P2):**
In `CategoryColorItem`, compute tint dynamically:

```kotlin
val luminance = parsedColor.luminance()
val checkTint = if (luminance > 0.5f) Color.Black else Color.White
```

**Fix -- hardcoded `maxItemsInEachRow = 7` (bug P2):**
Remove `maxItemsInEachRow` parameter from `FlowRow` to let it wrap naturally based on available width.

**Fix -- clickable vs Surface onClick (minor):**
In `CategoryImageItem`, replace `Modifier.clickable(onClick = onClick)` with `Surface(onClick = onClick, ...)` for proper ripple clipping.

**Fix -- extract `SectionError` composable (minor):**
Extract the repeated `AnimatedVisibility { Text(error) }` pattern (3 occurrences) into a private `SectionError(error: String?, startPadding: Dp)` composable.

## 6. Update docs

Per workspace rule, update [.cursor/docs/APP_FINANCE_FEATURE.md](.cursor/docs/APP_FINANCE_FEATURE.md) with:

- New Orbit MVI dependency
- Updated ViewModel state/side-effect contract
- Removed LiveData from CreateFinanceCategoryViewModel

