# :data Module

## Общие сведения

- **Тип**: Android Library (`com.android.library`)
- **Namespace**: `com.breckneck.data`
- **Пакеты в коде**: `com.breckneck.deptbook.data.storage` (интерфейсы), `database`, `entity`, `repository`, `sharedprefs`, `util` (flat-пакеты)
- **SDK**: compileSdk 34, minSdk 21, targetSdk 34
- **Java target**: 17
- **Зависимости**: Room 2.8.4 + KSP, зависимость на `:domain`

Модуль отвечает за персистентность данных: Room БД, SharedPreferences, и реализации репозиториев.

---

## Структура

```
data/src/main/java/
├── com/breckneck/deptbook/data/storage/   # Интерфейсы Storage (порты data-слоя)
├── database/                               # Room DB, DAO, TypeConverters, Storage Impl
├── entity/                                 # Room @Entity классы
│   └── relations/                          # Room @Relation классы
├── repository/                             # Реализации domain-репозиториев
├── sharedprefs/                            # SharedPreferences-реализации
└── util/                                   # Константы data-слоя
```

---

## Room Database

### `AppDataBase` (`database/AppDataBase.kt`)

- **Version**: 14
- **Entities**: `Human`, `Debt`, `FinanceData`, `FinanceCategoryData`, `GoalData`, `GoalDepositData`
- **TypeConverters**: `Converters` (конвертация `Date` ↔ `Long` и др.)
- **Singleton**: thread-safe через `synchronized` + `@Volatile`
- **Database name**: определено в `util/ConstantsData.kt`

### `AppDao` (`database/AppDao.kt`)

Единый DAO для всех таблиц.

#### Human-запросы
| Метод | SQL / аннотация |
|-------|----------------|
| `getAllHuman()` | `SELECT * FROM human` |
| `getPositiveHumans()` | `WHERE sumDebt >= 0` |
| `getNegativeHumans()` | `WHERE sumDebt <= 0` |
| `getLastHumanId()` | `ORDER BY id DESC LIMIT 1` |
| `addSum(humanId, sum)` | `UPDATE human SET sumDebt = sumDebt + :sum WHERE id = :humanId` |
| `getAllDebtsSum(currency)` | `SELECT sumDebt WHERE currency = :currency` |
| `getHumanSumDebt(humanId)` | `SELECT sumDebt WHERE id = :humanId` |
| `deleteHumanById(id)` | `DELETE FROM human WHERE id = :id` |
| `insertHuman(human)` | `@Insert` |
| `insertAllHumans(list)` | `@Insert(REPLACE)` |
| `updateHuman(human)` | `@Update` |
| `deleteHuman(human)` | `@Delete` |

#### Debt-запросы
| Метод | SQL / аннотация |
|-------|----------------|
| `getAllDebts()` | `SELECT * FROM debt` |
| `getAllDebtsById(id)` | `WHERE idHuman = :id` |
| `deleteDebtsByHumanId(id)` | `DELETE WHERE idHuman = :id` |
| `getDebtQuantity()` | `SELECT count(*) FROM debt` |
| `insertDebt` / `insertAllDebts` | `@Insert` / `@Insert(REPLACE)` |
| `deleteDebt` / `updateDebt` | `@Delete` / `@Update` |

#### Finance-запросы
| Метод | SQL / аннотация |
|-------|----------------|
| `getAllFinances()` | `SELECT * FROM financedata` |
| `getFinanceByCategoryId(categoryId)` | `WHERE financeCategoryId = :categoryId` |
| `deleteAllFinancesByCategoryId(id)` | `DELETE WHERE financeCategoryId` |
| `insertFinance` / `deleteFinance` / `updateFinance` | CRUD |
| `insertAllFinances` / `deleteAllFinances` | Bulk ops |

#### FinanceCategory-запросы
| Метод | SQL / аннотация |
|-------|----------------|
| `getAllFinanceCategories()` | `SELECT * FROM financecategorydata` |
| `getFinanceCategoriesByState(state)` | `WHERE state = :state` |
| `getFinanceCategoriesWithFinancesByState(state)` | `@Transaction` + Relation |
| `getAllFinanceCategoriesWithFinances()` | `@Transaction` — все с вложенными |
| CRUD | insert / delete / update / insertAll / deleteAll |

#### Goal-запросы
`getAllGoals`, `insertGoal`, `deleteGoal`, `updateGoal`, `insertAllGoals`, `deleteAllGoals`

#### GoalDeposit-запросы
`getAllGoalDeposits`, `getGoalDepositsByGoalId(goalId)`, `deleteGoalDepositsByGoalId(goalId)`, CRUD, bulk ops

---

## Entities (`entity/`)

Поля entity-классов **зеркалят** доменные модели из `DOMAIN_MODULE.md` (1:1 маппинг в Repository Impl). Все PK — `autoGenerate = true`.

| Entity | Таблица Room | Domain Model | Особенности |
|--------|-------------|--------------|-------------|
| `Human` | `human` | `HumanDomain` | — |
| `Debt` | `debt` | `DebtDomain` | `date` хранится как `String` |
| `FinanceData` | `financedata` | `Finance` | `date` хранится как `Long` через TypeConverter |
| `FinanceCategoryData` | `financecategorydata` | `FinanceCategory` | `state` — enum `FinanceCategoryStateData` |
| `GoalData` | `GoalData` | `Goal` | `creationDate`/`goalDate` — через TypeConverter |
| `GoalDepositData` | `GoalDepositData` | `GoalDeposit` | — |
| `FinanceCategoryDataWithFinanceData` | (relation) | `FinanceCategoryWithFinances` | `@Relation` через `financeCategoryId` |

---

## Storage Interfaces (`com.breckneck.deptbook.data.storage/`)

Порты между Repository и конкретными реализациями (Room / SharedPrefs).

| Интерфейс | Реализация | Хранилище |
|-----------|-----------|-----------|
| `HumanStorage` | `DataBaseHumanStorageImpl` | Room |
| `DebtStorage` | `DataBaseDebtStorageImpl` | Room |
| `FinanceStorage` | `DataBaseFinanceStorageImpl` | Room |
| `FinanceCategoryStorage` | `DataBaseFinanceCategoryStorageImpl` | Room |
| `GoalStorage` | `DataBaseGoalStorageImpl` | Room |
| `GoalDepositStorage` | `DataBaseGoalDepositStorageImpl` | Room |
| `SettingsStorage` | `SharedPrefsSettingsStorageImpl` | SharedPreferences |
| `AdStorage` | `SharedPrefsAdStorageImpl` | SharedPreferences |

Каждая `DataBase*StorageImpl` получает `Context`, создаёт/получает `AppDataBase` singleton и делегирует вызовы в `AppDao`.

---

## Repository Implementations (`repository/`)

Маппинг `entity` ↔ `domain model` и делегирование в соответствующий `Storage`.

| Класс | Domain interface | Storage |
|-------|-----------------|---------|
| `HumanRepositoryImpl` | `HumanRepository` | `HumanStorage` |
| `DebtRepositoryImpl` | `DebtRepository` | `DebtStorage` |
| `FinanceRepositoryImpl` | `FinanceRepository` | `FinanceStorage` |
| `FinanceCategoryRepositoryImpl` | `FinanceCategoryRepository` | `FinanceCategoryStorage` |
| `GoalRepositoryImpl` | `GoalRepository` | `GoalStorage` |
| `GoalDepositRepositoryImpl` | `GoalDepositRepository` | `GoalDepositStorage` |
| `SettingsRepositoryImpl` | `SettingsRepository` | `SettingsStorage` |
| `AdRepositoryImpl` | `AdRepository` | `AdStorage` |

Каждый `*RepositoryImpl` конвертирует entity ↔ domain model при чтении/записи.

---

## SharedPreferences (`sharedprefs/`)

### `SharedPrefsSettingsStorageImpl`
Все настройки приложения хранятся в SharedPreferences:
- Валюты (first/second main, default, finance)
- Тема, сортировка, флаги
- PIN-код (зашифрованный), fingerprint enable
- User data (JSON через Gson)
- Last sync date, isAuthorized

### `SharedPrefsAdStorageImpl`
Счётчик кликов по рекламе в SharedPreferences.

---

## Утилиты (`util/`)

### `ConstantsData.kt`
- `DATA_BASE_NAME` — имя файла Room БД

### `FinanceCategoryStateData`
Data-слой enum, зеркало `FinanceCategoryState` из domain. Используется в Room entity и DAO queries.

---

## Тесты

- `ExampleUnitTest.kt` — placeholder
- `ExampleInstrumentedTest.kt` — placeholder

Тесты data-модуля минимальны (шаблонные).
