# Goal Feature (app module)

## Назначение

Функция целей накопления. Пользователь создаёт цель с целевой суммой, может вносить депозиты, отслеживать прогресс. Поддерживает дедлайны и фото цели.

---

## Структура файлов

```
app/src/main/java/com/breckneck/debtbook/goal/
├── main/
│   ├── screen/
│   │   ├── GoalsScreen.kt             # Compose корневой экран списка целей
│   │   ├── GoalItem.kt                # Compose элемент цели
│   │   └── AddGoalSumBottomSheet.kt   # Compose bottom sheet быстрого добавления суммы
│   ├── GoalsAction.kt                 # sealed interface Orbit-интентов
│   ├── GoalsState.kt                  # State + GoalsSideEffect (Orbit MVI)
│   ├── GoalsFragment.kt               # Фрагмент-хост списка целей
│   └── GoalsViewModel.kt              # VM списка (Orbit MVI)
├── create/
│   ├── CreateGoalsFragment.kt         # Создание/редактирование цели
│   ├── CreateGoalsViewModel.kt        # VM создания
│   ├── CreateGoalsState.kt            # State (Orbit MVI)
│   ├── CreateGoalsAction.kt           # sealed interface действий
│   └── CreateGoalsSideEffect.kt       # sealed interface side-эффектов
└── details/
    ├── GoalDepositAdapter.kt          # Адаптер списка транзакций/депозитов
    ├── GoalDetailsFragment.kt         # Детали цели + депозиты
    └── GoalDetailsViewModel.kt        # VM деталей
```

### Layouts
- `fragment_goal.xml` — список целей
- `fragment_create_goal.xml` — форма создания/редактирования
- `fragment_goal_details.xml` — детали цели с историей
- `item_goal.xml` / `item_goal_shimmer.xml` — элемент цели
- `item_goal_transaction.xml` / `item_goal_transaction_shimmer.xml` — элемент депозита
- `dialog_add_goal_sum.xml` — диалог добавления суммы

---

## GoalsViewModel

VM списка целей. Реализует `ContainerHost<GoalsState, GoalsSideEffect>` (Orbit MVI).

### Зависимости
- `GetAllGoals` — загрузка целей
- `UpdateGoal` — обновление (при добавлении суммы)
- `DeleteGoal` — удаление цели
- `SetGoalDeposit` — создание депозита
- `DeleteGoalDepositsByGoalId` — удаление депозитов при удалении цели

### State (`GoalsState`)
| Поле | Тип | Описание |
|------|-----|---------|
| `goalList` | `List<Goal>` | Список целей |
| `listState` | `ListState` | LOADING / EMPTY / SUCCESS |

### Side effects (`GoalsSideEffect`)
| Эффект | Когда |
|--------|-------|
| `NavigateToAddGoal` | Навигация на создание новой цели |
| `NavigateToGoalDetails(goal)` | Навигация на детали цели |

### `GoalsAction` (sealed interface)
| Действие | Что делает |
|----------|-----------|
| `AddGoalClick` | Post side effect навигации на создание цели |
| `GoalClick(goal)` | Post side effect навигации на детали |
| `AddGoalDeposit(goal, sum)` | Создать депозит и обновить savedSum цели |
| `DeleteGoal(goal)` | Удалить цель + депозиты, перезагрузить список |
| `RefreshAfterNavigation(wasModified)` | Перезагрузить список если данные изменились |

### Ключевая логика
- При удалении цели — каскадное удаление депозитов
- Compose bottom sheet быстрого добавления суммы прямо из списка

---

## CreateGoalsViewModel

### Зависимости
- `SetGoal` — создание цели
- `UpdateGoal` — обновление при редактировании
- `GetDefaultCurrency` — валюта по умолчанию

### State (`CreateGoalsState`)
| Поле | Тип | Описание |
|------|-----|---------|
| `name` | `String` | Название цели |
| `nameError` | `String?` | Ошибка валидации имени |
| `sum` | `String` | Целевая сумма (текст) |
| `sumError` | `String?` | Ошибка валидации суммы |
| `savedSum` | `String` | Уже накопленная сумма (текст) |
| `savedSumError` | `String?` | Ошибка валидации savedSum |
| `currency` | `String` | Символ валюты |
| `currencyDisplayName` | `String` | Отображаемое название валюты |
| `selectedCurrencyIndex` | `Int` | Индекс в списке валют |
| `isCurrencySheetVisible` | `Boolean` | Открыт ли bottom sheet выбора валюты |
| `goalDate` | `Date?` | Дата дедлайна |
| `goalDateFormatted` | `String?` | Отформатированная дата |
| `imageUri` | `Uri?` | URI выбранного из галереи фото |
| `imagePath` | `String?` | Путь сохранённого фото (режим редактирования) |
| `hasImage` | `Boolean` | Есть ли изображение |
| `isEditMode` | `Boolean` | Режим редактирования |
| `title` | `String` | Заголовок экрана |

### Side effects (`CreateGoalsSideEffect`)
| Эффект | Когда |
|--------|-------|
| `NavigateBack` | После сохранения или нажатия «Назад» |
| `LaunchImagePicker` | Открыть галерею для выбора фото |
| `ShowDatePicker` | Открыть диалог выбора даты |

### `CreateGoalsAction` (sealed interface)
| Действие | Что делает |
|----------|-----------|
| `NameChanged(value)` | Обновить название |
| `SumChanged(value)` | Обновить целевую сумму |
| `SavedSumChanged(value)` | Обновить накопленную сумму |
| `CurrencyClick` | Показать bottom sheet валют |
| `CurrencySelected(index)` | Выбрать валюту по индексу |
| `DismissCurrencySheet` | Закрыть bottom sheet валют |
| `DateClick` | Показать выбор даты |
| `DateSelected(date)` | Установить дату дедлайна |
| `ImagePicked(uri)` | Установить URI выбранного фото |
| `DeleteImage` | Удалить фото |
| `PhotoCardClick` | Запустить выбор фото из галереи |
| `SaveClick` | Валидировать и сохранить цель |
| `BackClick` | Навигация назад |

### Ключевая логика
- Создание новой цели: название, целевая сумма, валюта, дедлайн (опционально), фото (опционально)
- Редактирование существующей цели

---

## GoalDetailsViewModel

### Зависимости
- `UpdateGoal` — обновление savedSum
- `SetGoalDeposit` — добавление депозита
- `GetGoalDepositsByGoalId` — история депозитов
- `DeleteGoalDepositsByGoalId` — очистка депозитов
- `DeleteGoal` — удаление цели

### Ключевая логика
- Отображение прогресса: `savedSum / sum * 100%`
- История депозитов (список транзакций)
- Добавление / удаление депозитов
- Удаление цели

---

## Adapters

### GoalDepositAdapter
- RecyclerView для транзакций цели
- Отображает: сумму, дату
- Shimmer layout

---

## Доменные модели

Определены в `DOMAIN_MODULE.md` → раздел «Модели»: `Goal`, `GoalDeposit`.

---

## Особенности

- Фото цели загружается через **Glide** по `photoPath`
- `photoPath` задаётся через `FileProvider` (определён в `provider_paths.xml`)
- При удалении цели каскадно удаляются все депозиты через `DeleteGoalDepositsByGoalId`
