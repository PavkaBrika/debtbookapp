# Goal Feature (app module)

## Назначение

Функция целей накопления. Пользователь создаёт цель с целевой суммой, может вносить депозиты, отслеживать прогресс. Поддерживает дедлайны и фото цели.

---

## Структура файлов

```
app/src/main/java/com/breckneck/debtbook/goal/
├── adapter/
│   ├── GoalAdapter.kt          # Адаптер списка целей
│   └── GoalDepositAdapter.kt   # Адаптер списка транзакций/депозитов
├── presentation/
│   ├── GoalsFragment.kt        # Список целей
│   ├── CreateGoalsFragment.kt  # Создание/редактирование цели
│   └── GoalDetailsFragment.kt  # Детали цели + депозиты
└── viewmodel/
    ├── GoalsFragmentViewModel.kt        # VM списка
    ├── CreateGoalsFragmentViewModel.kt  # VM создания
    └── GoalDetailsFragmentViewModel.kt  # VM деталей
```

### Layouts
- `fragment_goal.xml` — список целей
- `fragment_create_goal.xml` — форма создания/редактирования
- `fragment_goal_details.xml` — детали цели с историей
- `item_goal.xml` / `item_goal_shimmer.xml` — элемент цели
- `item_goal_transaction.xml` / `item_goal_transaction_shimmer.xml` — элемент депозита
- `dialog_add_goal_sum.xml` — диалог добавления суммы

---

## GoalsFragmentViewModel

VM списка целей.

### Зависимости
- `GetAllGoals` — загрузка целей
- `UpdateGoal` — обновление (при добавлении суммы)
- `DeleteGoal` — удаление цели
- `SetGoalDeposit` — создание депозита
- `DeleteGoalDepositsByGoalId` — удаление депозитов при удалении цели

### State
| Поле | Тип | Описание |
|------|-----|---------|
| `goalList` | `LiveData<List<Goal>>` | Список целей |
| `goalListState` | `LiveData<ListState>` | LOADING / EMPTY / SUCCESS |
| `isAddSumDialogOpened` | `Boolean` | Состояние диалога добавления суммы |
| `changedGoal` | `Goal?` | Цель, к которой добавляют сумму |
| `changedGoalPosition` | `Int?` | Позиция в списке |

### Ключевая логика
- Загрузка целей через RxJava `Single`
- При удалении цели — каскадное удаление депозитов
- Диалог быстрого добавления суммы к цели прямо из списка
- Обновление `savedSum` цели при добавлении депозита

### Действия
| Метод | Что делает |
|-------|-----------|
| `getAllGoals()` | Загрузить/обновить список |
| `updateGoal(goal)` | Обновить цель (savedSum) |
| `deleteGoal(goal)` | Удалить цель + депозиты |
| `setGoalDeposit(goalDeposit)` | Создать депозит |

---

## CreateGoalsFragmentViewModel

### Зависимости
- `SetGoal` — создание цели
- `UpdateGoal` — обновление при редактировании
- `GetDefaultCurrency` — валюта по умолчанию

### Ключевая логика
- Создание новой цели: название, целевая сумма, валюта, дедлайн (опционально), фото (опционально)
- Редактирование существующей цели

---

## GoalDetailsFragmentViewModel

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

### GoalAdapter
- RecyclerView для списка целей
- Отображает: название, прогресс (savedSum/sum), валюту, фото
- Callbacks: клик (детали), long press (быстрое добавление суммы)
- Shimmer layout

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
