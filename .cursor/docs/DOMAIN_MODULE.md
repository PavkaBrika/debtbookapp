# :domain Module

## Общие сведения

- **Тип**: JVM Kotlin library (`java-library`)
- **Пакет**: `com.breckneck.deptbook.domain` (внимание: **dept**book, не debtbook)
- **Java target**: 17
- **Зависимости**: Apache POI 4.0.0 (экспорт Excel), Gson 2.10.1 (сериализация)
- **Тесты**: JUnit 5, Mockito

Модуль не зависит от Android SDK. Содержит чистую бизнес-логику, доменные модели, интерфейсы репозиториев и use cases.

---

## Структура

```
domain/src/main/java/com/breckneck/deptbook/domain/
├── model/          # Доменные модели (data class)
├── repository/     # Интерфейсы репозиториев (порты)
├── usecase/        # Use Cases по фичам
│   ├── Ad/
│   ├── Debt/
│   ├── Finance/
│   ├── FinanceCategory/
│   ├── Goal/
│   ├── GoalDeposit/
│   ├── Human/
│   └── Settings/
└── util/           # Enum'ы, константы, хелперы сортировки
```

---

## Модели (`model/`)

### `HumanDomain`
Человек, которому должны или который должен.
```kotlin
data class HumanDomain(
    var id: Int,
    var name: String,
    var sumDebt: Double,   // суммарный долг (+ = мне должны, - = я должен)
    var currency: String   // код валюты
)
```

### `DebtDomain`
Одна запись долга, привязанная к человеку.
```kotlin
data class DebtDomain(
    var id: Int,
    var sum: Double,       // сумма (+ = дал, - = взял)
    var idHuman: Int,      // FK → HumanDomain.id
    var info: String?,     // описание
    var date: String       // дата строкой
)
```

### `Finance`
Финансовая запись (доход или расход).
```kotlin
data class Finance(
    var id: Int = 0,
    var sum: Double,
    var date: Date,
    var info: String?,
    var financeCategoryId: Int  // FK → FinanceCategory.id
) : Serializable
```

### `FinanceCategory`
Категория финансов (Здоровье, Еда, Зарплата и т.д.).
```kotlin
data class FinanceCategory(
    var id: Int = 0,
    var name: String,
    var state: FinanceCategoryState,  // EXPENSE / INCOME
    var color: String,                // HEX цвет (#EF9A9A)
    var image: Int,                   // Unicode emoji codepoint
    var isChecked: Boolean = false
)
```

### `FinanceCategoryWithFinances`
Агрегат: категория + список её финансовых записей.

### `Goal`
Цель накопления.
```kotlin
data class Goal(
    val id: Int = 0,
    var name: String,
    var sum: Double,          // целевая сумма
    var savedSum: Double,     // накопленная сумма
    var currency: String,
    var photoPath: String?,   // путь к фото цели
    var creationDate: Date,
    var goalDate: Date?       // дедлайн (nullable)
) : Serializable
```

### `GoalDeposit`
Транзакция (вклад/снятие) по цели.
```kotlin
data class GoalDeposit(
    var id: Int = 0,
    var sum: Double,
    var date: Date,
    var goalId: Int  // FK → Goal.id
)
```

### `User`
Данные пользователя для синхронизации.
```kotlin
data class User(val name: String, val email: String)
```

### `AppDataLists`
Агрегат всех данных для синхронизации/бэкапа.
```kotlin
data class AppDataLists(
    val humanList: List<HumanDomain>,
    val debtList: List<DebtDomain>,
    val financeList: List<Finance>,
    val financeCategoryList: List<FinanceCategory>,
    val goalList: List<Goal>,
    val goalDepositList: List<GoalDeposit>
)
```

---

## Интерфейсы репозиториев (`repository/`)

### `HumanRepository`
| Метод | Описание |
|-------|---------|
| `getAllHumans()` | Все люди |
| `getPositiveHumans()` | Те, кто должен мне |
| `getNegativeHumans()` | Те, кому я должен |
| `insertHuman(humanDomain)` | Вставка |
| `updateHuman(human)` | Обновление |
| `deleteHuman(id)` | Удаление |
| `getLastHumanId()` | ID последнего вставленного |
| `addSum(humanId, sum)` | Изменение суммарного долга |
| `getAllDebtsSum(currency)` | Суммы долгов по валюте |
| `getHumanSumDebt(humanId)` | Сумма долга человека |
| `replaceAllHumans(humanList)` | Полная замена (sync) |

### `DebtRepository`
| Метод | Описание |
|-------|---------|
| `getAllDebts()` | Все долги |
| `getAllDebtsById(id)` | Долги по humanId |
| `setDebt(debtDomain)` | Вставка |
| `editDebt(debtDomain)` | Обновление |
| `deleteDebt(debtDomain)` | Удаление |
| `deleteDebtsByHumanId(id)` | Удаление всех долгов человека |
| `getDebtQuantity()` | Количество долгов |
| `replaceAllDebts(debtList)` | Полная замена (sync) |

### `FinanceRepository`
CRUD + `getFinanceByCategoryId()` (`suspend`), `deleteFinance()` (`suspend`), `deleteAllFinancesByCategoryId()`, `replaceAllFinances()`.

### `FinanceCategoryRepository`
CRUD + `getFinanceCategoriesByState()`, `getAllCategoriesWithFinances()`, `replaceAllFinanceCategories()`.

### `GoalRepository`
CRUD + `replaceAllGoals()`.

### `GoalDepositRepository`
CRUD + `getGoalDepositsByGoalId()`, `deleteGoalDepositsByGoalId()`, `replaceAllGoalsDeposits()`.

### `SettingsRepository`
Key-value хранилище настроек:
- Валюты: `firstMainCurrency`, `secondMainCurrency`, `defaultCurrency`, `financeCurrency`
- Тема: `appTheme`
- Сортировка: `debtOrder` (Pair<DebtOrderAttribute, Boolean>), `humanOrder`
- Безопасность: `PINCodeEnabled`, `PINCode`, `isFingerprintAuthEnabled`
- Синхронизация: `isAuthorized`, `userData` (User), `lastSyncDate`
- UI: `addSumInShareText`, `debtQuantityForAppRateDialogShow`, `appIsRated`

### `AdRepository`
Хранение количества кликов по рекламе: `getClicks()`, `saveClicks()`.

---

## Use Cases (`usecase/`)

Каждый use case — отдельный класс с `execute()` / `operator fun invoke()`. Принимает репозиторий через конструктор.

### `usecase/Human/` (9 use cases)
| Класс | Действие |
|-------|---------|
| `GetAllHumansUseCase` | Получить всех людей |
| `GetLastHumanIdUseCase` | Получить ID последнего человека |
| `SetHumanUseCase` | Создать человека |
| `UpdateHuman` | Обновить |
| `DeleteHumanUseCase` | Удалить |
| `AddSumUseCase` | Изменить сумму долга |
| `GetAllDebtsSumUseCase` | Агрегация сумм долгов по валюте |
| `GetHumanSumDebtUseCase` | Сумма долга одного человека |
| `ReplaceAllHumans` | Полная замена (sync) |

### `usecase/Debt/` (15 use cases)
| Класс | Действие |
|-------|---------|
| `GetAllDebts` | Все долги |
| `GetAllDebtsByIdUseCase` | Долги по humanId |
| `SetDebtUseCase` | Создать |
| `EditDebtUseCase` | Редактировать |
| `DeleteDebtUseCase` | Удалить |
| `DeleteDebtsByHumanIdUseCase` | Удалить все долги человека |
| `GetDebtQuantity` | Количество |
| `FilterDebts` | Фильтрация (ALL / POSITIVE / NEGATIVE) |
| `FormatDebtSum` | Форматирование суммы для отображения |
| `GetDebtShareString` | Текст для «поделиться» |
| `ExportDebtDataInExcelUseCase` | Экспорт в .xls (Apache POI) |
| `GetCurrentDateUseCase` | Текущая дата |
| `SetDateUseCase` | Установка даты |
| `UpdateCurrentSumUseCase` | Обновление текущей суммы |
| `ReplaceAllDebts` | Полная замена (sync) |

### `usecase/Finance/` (7 use cases)
CRUD + `GetFinanceByCategoryId` (`suspend execute`), `DeleteFinance` (`suspend execute`), `DeleteAllFinancesByCategoryId`, `ReplaceAllFinances`.

### `usecase/FinanceCategory/` (6 use cases)
CRUD + `GetAllCategoriesWithFinances`, `GetFinanceCategoriesByState`, `ReplaceAllFinanceCategories`.

### `usecase/Goal/` (5 use cases)
CRUD + `ReplaceAllGoals`.

### `usecase/GoalDeposit/` (5 use cases)
CRUD + `GetGoalDepositsByGoalId`, `DeleteGoalDepositsByGoalId`, `ReplaceAllGoalsDeposits`.

### `usecase/Settings/` (31 use case)
Getter/Setter пары для каждой настройки. Примеры:
`GetFirstMainCurrency` / `SetFirstMainCurrency`, `GetPINCode` / `SetPINCode`, `GetAppTheme` / `SetAppTheme` и т.д.

### `usecase/Ad/` (2 use cases)
`GetClicksUseCase`, `SaveClicksUseCase`.

---

## Утилиты (`util/`)

### Enum'ы и sealed-классы состояний
| Тип | Значения | Назначение |
|-----|---------|-----------|
| `ScreenState` (enum) | LOADING, EMPTY, SUCCESS | Состояние экрана |
| `ListState` (enum) | LOADING, EMPTY, SUCCESS | Состояние списка |
| `Filter` (enum) | ALL, POSITIVE, NEGATIVE | Фильтр долгов |
| `FinanceCategoryState` (enum) | EXPENSE, INCOME | Тип категории (расходы/доходы) |
| `FinanceInterval` (enum) | DAY, WEEK, MONTH, YEAR | Интервал группировки |
| `DebtOrderAttribute` (enum) | DATE, SUM | Сортировка долгов |
| `HumanOrderAttribute` (enum) | DATE, SUM, NAME | Сортировка людей |
| `PINCodeAction` (enum) | ENABLE, DISABLE, CHANGE, CHECK | Действие с PIN |
| `PINCodeEnterState` (enum) | FIRST, CONFIRMATION, INCORRECT | Этап ввода PIN |
| `CreateFragmentState` (enum) | LOADING, READY | Состояние создания |
| `DebtLogicListState` (sealed class) | Loading, Received(needToSetFilter: Boolean), Sorted | State machine для списков долгов/людей |
| `ChangeGoalSavedSumDialogState` (enum) | — | Состояние диалога цели |
| `FinanceListState` (enum) | — | Состояние списка финансов |

### Константы (`ConstantsDomain.kt`)
- `categoryColorList` — 24 HEX цвета для категорий (19 пастельных Material 200 + 5 accent)
- `categoryImageList` — 21 Unicode emoji для категорий
- `revenuesCategoryEnglishNameList` — дефолтные категории расходов (7)
- `incomesCategoryEnglishNameList` — дефолтные категории доходов (4)
- Пороги для показа диалога оценки и рекламы

### Хелперы сортировки (`order.kt`)
Функции `sortHumans()` / `sortDebts()` — сортировка по выбранному атрибуту и направлению.

---

## Тесты

**Инструменты**: JUnit 5, Mockito 5, mockito-kotlin 5, `junit-platform-launcher`  
**Итог**: 137 тестов, 0 failures — все use cases покрыты (>95% из 80 use cases)

| Файл | Покрытие |
|------|---------|
| `FilterDebtsTest.kt` | Фильтрация долгов (ALL/NEGATIVE/POSITIVE) |
| `FormatDebtSumTest.kt` | Форматирование суммы |
| `GetAllDebtsSumUseCaseTest.kt` | Агрегация сумм по валютам |
| `GetAllHumansUseCaseTest.kt` | Загрузка списка людей |
| `HumanUseCasesTest.kt` | SetHuman, Delete, GetSum, GetLastId, AddSum, Update, ReplaceAll |
| `DebtUseCasesTest.kt` | GetAll, GetById, GetQty, Set, Delete, DeleteByHuman, Edit, Replace, UpdateCurrentSum, GetCurrentDate |
| `FinanceUseCasesTest.kt` | GetAll, GetByCategoryId, Set, Delete, Update, DeleteByCategoryId, ReplaceAll |
| `FinanceCategoryUseCasesTest.kt` | GetAll, GetWithFinances, GetByState, Set, Delete, ReplaceAll |
| `GoalUseCasesTest.kt` | GetAll, Set, Update, Delete, ReplaceAll |
| `GoalDepositUseCasesTest.kt` | GetAll, GetByGoalId, Set, DeleteByGoalId, ReplaceAll |
| `SettingsUseCasesTest.kt` | Все 30 Settings use cases (Get/Set для всех настроек) |
| `AdUseCasesTest.kt` | GetClicks, SaveClicks |
| `SortUtilTest.kt` | sortHumans (Date asc/desc, Sum asc/desc), sortDebts (CreationDate, Sum, Date asc/desc) |
