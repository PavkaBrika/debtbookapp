# DebtBook App — Project Overview

## Назначение

Android-приложение для учёта долгов, финансов и целей накопления. Позволяет вести список людей и их долгов, отслеживать расходы/доходы по категориям, ставить цели накопления с депозитами, синхронизировать данные через Google Drive и защищать приложение PIN-кодом / биометрией.

---

## Архитектура

**Clean Architecture + MVVM**, три Gradle-модуля:

```
:app  (com.android.application)
  ├── Presentation (Fragments, Adapters, ViewModels)
  ├── DI (Koin modules)
  └── Core (MainActivity, Theme, Navigation)

:domain  (java-library)
  ├── Model (доменные модели)
  ├── Repository (интерфейсы-порты)
  ├── UseCase (бизнес-логика)
  └── Util (enum'ы, хелперы, константы)

:data  (com.android.library)
  ├── Entity (Room @Entity)
  ├── Database (Room DB, DAO, Storage Impl)
  ├── Repository Impl (маппинг entity ↔ domain)
  ├── SharedPrefs (настройки, реклама)
  └── Storage interfaces (порт между DB и Repository)
```

Зависимости модулей: `app → domain`, `app → data`, `data → domain`.

---

## Технологии

| Область | Технология | Версия |
|---------|-----------|--------|
| Язык | Kotlin | 2.1.0 |
| Build | AGP / Gradle | 8.8.0 / 8.10.2 |
| DI | Koin (core, android, compose) | 3.5.3 |
| БД | Room + KSP compiler | 2.4.2 |
| UI (classic) | View Binding, Material, ConstraintLayout, RecyclerView | — |
| UI (Compose) | Compose BOM, Material3, Activity Compose | 2024.02.01 |
| Навигация | Navigation Component (single nav_graph.xml) | 2.7.7 |
| Lifecycle | ViewModel, LiveData | 2.5.0 |
| Реактивность | RxJava 3 + Kotlin Coroutines/Flow | — |
| Изображения | Glide | 4.16.0 |
| Shimmer | Facebook Shimmer | 0.1.0 |
| Безопасность | androidx.biometric (BiometricPrompt) | — |
| Облако | Google Sign-In, Google Drive API v3 | — |
| Реклама | Yandex Mobile Ads | 6.4.0 |
| Таблицы | Apache POI (экспорт .xls) | 4.0.0 |
| JSON | Gson | 2.10.1 |
| Тесты | JUnit 5, Mockito, Espresso, Compose UI test | — |

---

## Навигация

Единый `nav_graph.xml`. Start destination: `MainFragment` (список долгов).

### Экраны и переходы

```
AuthorizationActivity (launcher, Compose)
  └── [PIN/Biometric] → MainActivity

MainActivity (NavHostFragment + BottomNav)
  ├── MainFragment (долги) 
  │     ├── → NewDebtFragment (создание/редактирование долга)
  │     └── → DebtDetailsFragment (детали по человеку)
  │           └── → NewDebtFragment
  ├── FinanceFragment (финансы)
  │     ├── → CreateFinanceFragment
  │     │     └── → CreateFinanceCategoryFragment
  │     └── → FinanceDetailsFragment
  │           └── → CreateFinanceFragment
  ├── GoalsFragment (цели)
  │     ├── → CreateGoalsFragment
  │     └── → GoalDetailsFragment
  │           └── → CreateGoalsFragment
  └── SettingsFragment (настройки)
        └── → SynchronizationFragment
```

### Bottom Navigation Tabs

1. Долги (`MainFragment`)
2. Финансы (`FinanceFragment`)  
3. Цели (`GoalsFragment`)
4. Настройки (`SettingsFragment`)

---

## Пакетная структура `:app`

```
com.breckneck.debtbook
├── app/                  # Application class (Koin + Yandex Ads init)
├── auth/
│   ├── presentation/     # AuthorizationActivity, AuthorizationScreen (Compose)
│   ├── viewmodel/        # AuthorizationViewModel
│   └── util/             # BiometricPromptManager, CryptoManager
├── core/
│   ├── activity/         # MainActivity
│   ├── viewmodel/        # MainActivityViewModel
│   ├── customview/       # CustomSwitchView
│   └── ui/theme/         # Compose Color, Shape, Type, Theme
├── debt/
│   ├── adapter/          # HumanAdapter, DebtAdapter, ContactsAdapter
│   ├── presentation/     # MainFragment, DebtDetailsFragment, NewDebtFragment
│   └── viewmodel/        # MainFragmentViewModel, DebtDetailsViewModel, NewDebtViewModel
├── finance/
│   ├── adapter/          # FinanceAdapter, FinanceCategoryAdapter и др.
│   ├── customview/       # FinanceProgressBar
│   ├── presentation/     # FinanceFragment, CreateFinance*, FinanceDetailsFragment
│   ├── util/             # GetFinanceCategoryNameInLocalLanguage
│   └── viewmodel/        # FinanceViewModel, CreateFinanceViewModel и др.
├── goal/
│   ├── adapter/          # GoalAdapter, GoalDepositAdapter
│   ├── presentation/     # GoalsFragment, CreateGoalsFragment, GoalDetailsFragment
│   └── viewmodel/        # GoalsFragmentViewModel и др.
├── settings/
│   ├── adapter/          # SettingsAdapter
│   ├── presentation/     # SettingsFragment, SynchronizationFragment
│   ├── util/             # DriveServiceHelper
│   └── viewmodel/        # SettingsViewModel, SynchronizationViewModel
└── di/                   # AppModule, DataModule, DomainModule (Koin)
```

---

## Важные особенности

- **Пакетный typo**: `:domain` и `:data` используют `com.breckneck.deptbook`, а `:app` — `com.breckneck.debtbook`. Это отражается в импортах.
- **Hybrid Async**: ViewModel'ы используют и RxJava 3, и Coroutines/Flow.
- **Room DB version 14**: 6 таблиц (`Human`, `Debt`, `FinanceData`, `FinanceCategoryData`, `GoalData`, `GoalDepositData`).
- **Compose** используется только для экрана авторизации (`AuthorizationScreen`); остальной UI — View Binding + XML.
- **Google Drive Sync**: `DriveServiceHelper` + `SynchronizationViewModel` обеспечивают backup/restore всех данных.
- **Yandex Ads**: интегрированы с подсчётом кликов (`AdRepository` / `AdStorage`).
- **Локализация**: ru, en-US, de, fr и региональные варианты (BY, KG, KZ, MD, UA).
- **Excel Export**: через Apache POI (`ExportDebtDataInExcelUseCase`).

---

## Файлы конфигурации

| Файл | Назначение |
|------|-----------|
| `settings.gradle` | Подключение модулей `:app`, `:domain`, `:data` + JitPack |
| `build.gradle` (root) | Версии плагинов (AGP 8.8.0, Kotlin 2.1.0, KSP) |
| `app/build.gradle` | Зависимости app-модуля, Compose, View Binding |
| `data/build.gradle` | Room + KSP, compile SDK 34 |
| `domain/build.gradle` | java-library, POI, Gson |
| `gradle.properties` | AndroidX flags |
| `AndroidManifest.xml` | Permissions: READ_CONTACTS, VIBRATE; FileProvider |

---

## Карта документации

| Файл | Что описывает | Когда читать |
|------|-------------|-------------|
| `PROJECT_OVERVIEW.md` | Архитектура, tech stack, навигация, общая картина | Всегда первым |
| `DOMAIN_MODULE.md` | Модели, репозитории, use cases, enum'ы, util | Работа с бизнес-логикой, добавление use case |
| `DATA_MODULE.md` | Room DB, DAO, entities, storage, маппинг | Работа с БД, миграции, новые таблицы |
| `APP_CORE.md` | App init, MainActivity, DI (Koin), навигация, Auth (PIN/Bio) | DI-конфигурация, добавление VM, auth-логика |
| `APP_DEBT_FEATURE.md` | Фича долгов (3 экрана, 3 VM, 3 адаптера) | Изменения в долгах |
| `APP_FINANCE_FEATURE.md` | Фича финансов (4 экрана, 4 VM, 5 адаптеров) | Изменения в финансах |
| `APP_GOAL_FEATURE.md` | Фича целей (3 экрана, 3 VM, 2 адаптера) | Изменения в целях |
| `APP_SETTINGS_FEATURE.md` | Настройки + Google Drive sync | Изменения в настройках/синхронизации |

---

## Паттерны для добавления нового функционала

### Добавление нового экрана (Fragment)
1. Создать `Fragment` в `app/.../feature/presentation/`
2. Создать layout XML в `res/layout/`
3. Создать `ViewModel` в `app/.../feature/viewmodel/`
4. Зарегистрировать VM в `di/AppModule.kt` через `viewModel<T> { T(...) }`
5. Добавить destination + action в `res/navigation/nav_graph.xml`
6. При необходимости — добавить use cases в `di/DomainModule.kt`

### Добавление новой таблицы (Entity)
1. Создать domain model в `domain/.../model/`
2. Создать domain repository interface в `domain/.../repository/`
3. Создать use cases в `domain/.../usecase/NewFeature/`
4. Создать Room `@Entity` в `data/.../entity/`
5. Добавить entity в `@Database(entities = [...])` в `AppDataBase.kt` + увеличить version
6. Добавить DAO-методы в `AppDao.kt`
7. Создать `Storage` interface в `data/.../storage/`
8. Создать `DataBase*StorageImpl` в `data/.../database/`
9. Создать `*RepositoryImpl` в `data/.../repository/`
10. Зарегистрировать Storage, Repository, UseCases в `di/DataModule.kt` и `di/DomainModule.kt`

### Добавление нового use case
1. Создать класс в `domain/.../usecase/Category/` с `fun execute(...)`
2. Зарегистрировать в `di/DomainModule.kt` как `factory` (или `single` для Settings)
3. Добавить в конструктор нужного ViewModel
4. Обновить binding в `di/AppModule.kt`
