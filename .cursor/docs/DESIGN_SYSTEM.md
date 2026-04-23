## DebtBook — Design System (pragmatic)

This document describes the current UI direction and the small set of rules that keep the app consistent **across the whole project** while it runs **two UI stacks** in parallel:
- **Legacy**: XML (Views) + `Theme.MaterialComponents.DayNight.NoActionBar`
- **New**: Compose + Material3 (`DebtBookTheme`)

### Product intent
- Personal finance companion: debts, income/expenses, savings goals.
- Default mood: calm, trustworthy, low‑anxiety. The UI should feel steady and predictable.
- Primary interaction model: fast scan → quick action → clear confirmation (no surprises).

---

## Foundations

### Theme
- **Both themes matter**: light + dark are equally important.
- Compose: Material3 theme via `DebtBookTheme` (dynamic colors optional; currently defaulted off for consistency).
- XML: MaterialComponents DayNight via `Theme.Debtbook` (`values/themes.xml` + `values-night/themes.xml`).

**Migration rule**: visual intent (neutral base + mint accent, calm hierarchy) must match across stacks, even if component libraries differ (M2 vs M3).

### Color tokens (source of truth)
There are currently two token sources:
- **Compose source of truth**: `app/src/main/java/com/breckneck/debtbook/core/ui/theme/Color.kt` (Material3 color scheme + app semantic colors).
- **XML legacy palette**: `app/src/main/res/values/colors.xml` + `Theme.Debtbook` in `values/themes.xml` / `values-night/themes.xml`.

- **Brand intent**:
  - Neutral base (black/white/gray) with restrained mint accent.
  - Semantic colors exist for meaning: `Green`, `Red`, `Yellow`, `Mint` in Compose; `@color/green`, `@color/red`, `@color/yellow`, `@color/mint` in XML.

**Guideline**: don’t use “error” colors for non-error meanings unless it’s intentional (e.g., expenses ≠ error).

### Typography
- Compose: default Material3 type scale (`Typography()`), no overrides yet.
- XML: theme forces `@font/roboto` (`Theme.Debtbook`).

**Guideline**:
- Rely on the type scale for hierarchy (not random sizes).
- Avoid ALL CAPS for long Cyrillic labels where readability drops.

### Spacing
Defined in `core/ui/theme/Spacing.kt` and accessed via `MaterialTheme.spacing`.

Use the spacing tokens instead of hardcoded dp in Compose screens.

**Legacy XML note**: XML layouts currently use many inline `dp` paddings/margins. During migration or touch-ups, prefer extracting repeated spacing values into dimens once it becomes a refactor target.

### Shapes
Defined in `core/ui/theme/Shape.kt` (`Shapes`).

Use `MaterialTheme.shapes.*` instead of ad-hoc `RoundedCornerShape(...)` unless there’s a strong reason.

---

## Component patterns

### Top bars (both stacks)
- Prefer a predictable title + back affordance.
- Compose:
  - `LargeTopAppBar` for detail screens that benefit from collapse.
  - `DebtBookTopBar` for simpler screens.
- XML:
  - Use app bar patterns/styles in `themes.xml` (e.g., `appBarLayoutStyle`) and keep title/back behavior consistent with Compose screens.

**`LargeTopAppBar` scroll pattern** (mandatory):
- Always override `scrolledContainerColor` to `MaterialTheme.colorScheme.surface` — the default Material3 tonal shift to `surfaceContainerHigh` looks dirty and conflicts with the calm tone.
- Add a `HorizontalDivider` with `alpha = collapsedFraction` directly below the bar (inside a `Column` in the `topBar` slot) — this provides a clean separator that fades in as the bar collapses instead of a sudden color change.

```kotlin
Column {
    LargeTopAppBar(
        colors = TopAppBarDefaults.largeTopAppBarColors(
            scrolledContainerColor = MaterialTheme.colorScheme.surface,
        ),
        ...
    )
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = collapsedFraction)
    )
}
```

### Bottom sheets
- Always use `dragHandle = null` on every `ModalBottomSheet`. The default drag pill is visually noisy and inconsistent with the calm design language.
- Swipe-to-dismiss still works without the handle — users can dismiss via back gesture or tapping the scrim.
- **Compensate for removed handle with `padding(top = 24.dp)` on the sheet content.** The M3 drag handle occupies ~24dp (4dp pill + 10dp top + 10dp bottom); without it the content sits flush against the sheet edge.

```kotlin
ModalBottomSheet(
    dragHandle = null,
    ...
) {
    Column(
        modifier = Modifier
            .padding(top = 24.dp)  // compensates for removed drag handle
            ...
    ) { ... }
}
```

### Lists & data scan
- Use `LazyColumn` with clear scanable rows.
- Provide:
  - LOADING skeleton (`ShimmerListPlaceholder`)
  - EMPTY state (`EmptyListPlaceholder`) with a clear next step (CTA or navigation hint)
  - RECEIVED state (content)

### Secondary actions (Edit / Delete / Cancel)
- Compose: use `ExtraFunctionsBottomSheet`.
- XML legacy equivalent: `dialog_extra_functions.xml` (historical pattern).

**Guideline (calm & trust)**:
- Destructive actions must have guardrails:
  - Prefer **Undo** (Snackbar) when possible, otherwise a confirm dialog.
  - Never make delete a “single accidental tap” with no recovery.

---

## Cross-stack consistency rules (XML ↔ Compose)

### Copy & localization
- All user-visible copy must come from `strings.xml` (already localized into multiple languages).
- Keep tone steady and non-blaming (“Что-то пошло не так” is fine; add an actionable next step when possible).

### Color semantics
- Expenses/incomes may use red/green semantics, but avoid mapping “expense” to Material `error*` tokens.
- Reserve `error` palette for true error states (validation, network failures, destructive warnings).

### States & resilience
- Every data screen should define: LOADING → EMPTY/CONTENT and an error recovery path when applicable.
- Empty states should answer: “what is this?”, “why empty?”, “what can I do next?”

### Migration guardrails
- When migrating a screen from XML to Compose:
  - Preserve information order and primary action placement.
  - Match spacing rhythm and density to existing screens.
  - Keep colors and copy aligned with the legacy theme unless a deliberate redesign is planned.

