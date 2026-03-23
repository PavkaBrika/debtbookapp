# Core, DI & Auth (app module)

## Назначение

Корневые компоненты приложения: Application class, MainActivity, DI-конфигурация (Koin), Compose-тема, кастомные View, навигация, авторизация (PIN + биометрия).

---

## Структура файлов

```
app/src/main/java/com/breckneck/debtbook/
├── app/
│   └── App.kt                        # Application subclass
├── auth/
│   ├── presentation/
│   │   ├── AuthorizationActivity.kt  # Launcher Activity (hosts Compose)
│   │   └── AuthorizationScreen.kt    # Compose UI: PIN/biometric
│   ├── viewmodel/
│   │   └── AuthorizationViewModel.kt # VM авторизации
│   └── util/
│       ├── BiometricPromptManager.kt # AndroidX BiometricPrompt wrapper
│       └── CryptoManager.kt          # AES + Keystore шифрование PIN
├── core/
│   ├── activity/
│   │   └── MainActivity.kt           # Основная Activity с NavHostFragment
│   ├── viewmodel/
│   │   └── MainActivityViewModel.kt  # Общий VM для MainActivity
│   ├── customview/
│   │   └── CustomSwitchView.kt       # Кастомный переключатель (расходы/доходы)
│   └── ui/theme/                      # Compose Material3 тема
│       ├── Color.kt
│       ├── Shape.kt
│       ├── Type.kt
│       └── Theme.kt
└── di/
    ├── AppModule.kt                   # Koin: ViewModels
    ├── DataModule.kt                  # Koin: Storage + Repository bindings
    └── DomainModule.kt                # Koin: Use Cases
```

---

## App.kt

`Application` subclass. Инициализация при старте:

1. **Yandex Mobile Ads SDK**: `MobileAds.initialize()`
2. **Koin DI**: `startKoin { modules(appModule, domainModule, dataModule) }`

---

## MainActivity

Основная Activity приложения (не launcher — launcher это `AuthorizationActivity`).

### Характеристики
- Хостит `NavHostFragment` с `nav_graph.xml`
- Bottom Navigation с 4 вкладками: Долги, Финансы, Цели, Настройки
- `hardwareAccelerated="true"`, `windowSoftInputMode="adjustPan"`
- Использует `MainActivityViewModel` для общего состояния

### Основные обязанности
- Управление видимостью bottom navigation bar
- Координация обновлений данных между фрагментами
- Показ диалогов оценки приложения (`dialog_rate_app.xml`, `dialog_low_app_rate.xml`)
- Управление рекламой (подсчёт кликов, показ по порогу)
- Передача callback'ов от фрагментов

---

## MainActivityViewModel

Общий ViewModel, живущий на уровне Activity.

### Зависимости
- `GetDebtQuantity` — кол-во долгов (для логики показа диалога оценки)
- `Get/SetDebtQuantityForAppRateDialogShow` — порог показа
- `GetClicksUseCase` / `SaveClicksUseCase` — счётчик кликов по рекламе
- `GetAppTheme` — тема приложения
- `Get/SetPINCodeEnabled` — управление PIN
- `SetIsFingerprintAuthEnabled` — управление биометрией

### State
| Поле | Тип | Описание |
|------|-----|---------|
| `debtQuantity` | `LiveData<Int>` | Общее кол-во долгов |
| `debtQuantityForAppRateDialogShow` | `LiveData<Int>` | Порог для диалога оценки |
| `adClicksCounter` | `LiveData<Int>` | Счётчик кликов для рекламы |
| `appTheme` | `LiveData<String>` | Текущая тема |
| `isBottomNavViewVisible` | `LiveData<Boolean>` | Видимость bottom nav |
| `isPINCodeEnabled` | `LiveData<Boolean>` | PIN активен |
| `isNeedDebtDataUpdate` | `LiveData<Boolean>` | Флаг обновления данных |
| `isNeedUpdateDebtSums` | `LiveData<Boolean>` | Флаг обновления сумм |
| `isAppRateDialogShow` | `LiveData<Boolean>` | Показ диалога оценки |
| `appRate` / `appReviewText` | `LiveData` | Данные оценки |

### Ключевая логика
- **Реклама**: счётчик кликов, при достижении порога (`CLICKS_QUANTITY_FOR_AD_SHOW = 9`) показ рекламы, сброс
- **Оценка**: при достижении порога кол-ва долгов показ диалога оценки, при отказе — увеличение порога на `DEBT_QUANTITY_FOR_NEXT_SHOW_APP_RATE_DIALOG = 10`
- При `onCleared()` сохраняет счётчик кликов

---

## DI — Koin Modules

### AppModule (`di/AppModule.kt`)
Регистрирует **14 ViewModel'ов** через `viewModel<T> { T(...use cases...) }`. Все — Koin `viewModel` scope (пересоздаются с ViewModelStore владельца).

Полный список: `MainFragmentViewModel`, `MainActivityViewModel`, `DebtDetailsViewModel`, `SettingsViewModel`, `NewDebtViewModel`, `SynchronizationViewModel`, `FinanceViewModel`, `CreateFinanceViewModel`, `CreateFinanceCategoryViewModel`, `FinanceDetailsViewModel`, `GoalsFragmentViewModel`, `CreateGoalsFragmentViewModel`, `GoalDetailsFragmentViewModel`, `AuthorizationViewModel`.

### DataModule (`di/DataModule.kt`)
Binding Storage → Implementation, Repository → Implementation:

| Binding | Type | Реализация |
|---------|------|-----------|
| `HumanStorage` | factory | `DataBaseHumanStorageImpl` |
| `HumanRepository` | factory | `HumanRepositoryImpl` |
| `DebtStorage` | factory | `DataBaseDebtStorageImpl` |
| `DebtRepository` | factory | `DebtRepositoryImpl` |
| `SettingsStorage` | **single** | `SharedPrefsSettingsStorageImpl` |
| `SettingsRepository` | **single** | `SettingsRepositoryImpl` |
| `FinanceStorage` / `FinanceRepository` | factory | DB impl |
| `FinanceCategoryStorage` / `FinanceCategoryRepository` | factory | DB impl |
| `AdStorage` / `AdRepository` | factory | SharedPrefs impl |
| `GoalStorage` / `GoalRepository` | factory | DB impl |
| `GoalDepositStorage` / `GoalDepositRepository` | factory | DB impl |

**Важно**: `SettingsStorage` и `SettingsRepository` — **singleton**, остальные — **factory**.

### DomainModule (`di/DomainModule.kt`)
Регистрирует **80+ use cases**:
- Human (8): `GetAllHumansUseCase`, `SetHumanUseCase`, `DeleteHumanUseCase` и др.
- Debt (15): `GetAllDebts`, `SetDebtUseCase`, `ExportDebtDataInExcelUseCase` и др.
- Settings (31): Get/Set пары для каждой настройки — все **single**
- Finance (7): CRUD + bulk replace
- FinanceCategory (6): CRUD + bulk replace
- Ad (2): Get/SaveClicks
- Goal (5): CRUD + replace
- GoalDeposit (5): CRUD + replace

**Важно**: Settings use cases — **single**, остальные — **factory**.

---

## Compose Theme (`core/ui/theme/`)

Используется только в `AuthorizationScreen`. `DebtBookTheme` — `@Composable` функция: `Color.kt` (цвета), `Shape.kt` (формы), `Type.kt` (типографика), `Theme.kt` (объединение + dark/light через `isSystemInDarkTheme()`).

---

## CustomSwitchView

Кастомный `View` — анимированный переключатель EXPENSE/INCOME в финансовом экране. Атрибуты: `custom_view_attrs.xml`. Используется в `fragment_finance.xml`.

---

## Auth — Авторизация (PIN + Биометрия)

### Поток запуска
```
App Launch → AuthorizationActivity (launcher)
  ├── [PIN disabled] → MainActivity (instant redirect)
  └── [PIN enabled]
        ├── [Biometric enabled] → BiometricPrompt
        │     ├── [success] → MainActivity
        │     └── [fail/cancel] → PIN input
        └── [PIN input]
              ├── [correct] → MainActivity
              └── [wrong] → shake + retry
```

### AuthorizationActivity
Launcher activity, хостит Jetpack Compose через `setContent { DebtBookTheme { AuthorizationScreen() } }`.

### AuthorizationScreen (единственный Compose-экран в приложении)
Composable-функции: `AuthorizationScreen` (корневой, маршрутизация по `PINCodeAction`), `UnlockScreen` (проверка PIN), `PINCodeSection` (точки + цифровая клавиатура).

### AuthorizationViewModel
**Зависимости**: `GetPINCodeEnabled`, `SetPINCodeEnabled`, `GetPINCode`, `SetPINCode`, `GetIsFingerprintAuthEnabled`.

| State | Тип | Описание |
|-------|-----|---------|
| `enteredPINCode` | `StateFlow<String>` | Текущий вводимый PIN |
| `currentPINCode` | `State<String>` | Сохранённый PIN (дешифрованный) |
| `pinCodeAction` | `State<PINCodeAction>` | ENABLE / DISABLE / CHANGE / CHECK |
| `pinCodeEnterState` | `StateFlow<PINCodeEnterState>` | FIRST / CONFIRMATION / INCORRECT |
| `isFingerprintAuthEnabled` | `State<Boolean>` | Биометрия включена? |

**Логика**: CHECK — сравнение ввода с сохранённым PIN. ENABLE — двойной ввод (FIRST → CONFIRMATION). DISABLE — ввод текущего PIN. CHANGE — проверка + новый ввод.

### CryptoManager
AES шифрование через Android Keystore (API 23+). Alias: `"PIN"`, файл: `crypto.txt`. На API < 23 PIN хранится без шифрования.

### BiometricPromptManager
Обёртка над `androidx.biometric.BiometricPrompt`. Callback-based API (success / error / cancel).

### Связь с Settings
PIN управляется из `SettingsFragment` → `MainActivityViewModel.setIsPINCodeEnabled()`. Биометрия → `SettingsViewModel.setIsFingerprintAuthEnabled()`.

---

## Навигация (nav_graph.xml)

Единый граф навигации. Start destination: `mainFragment`.

### Destinations (12 фрагментов)
| ID | Fragment | Описание |
|----|---------|---------|
| `mainFragment` | `MainFragment` | Список людей/долгов |
| `newDebtFragment` | `NewDebtFragment` | Создание долга |
| `debtDetailsFragment` | `DebtDetailsFragment` | Детали долгов |
| `settingsFragment` | `SettingsFragment` | Настройки |
| `synchronizationFragment` | `SynchronizationFragment` | Синхронизация |
| `financeFragment` | `FinanceFragment` | Финансы |
| `createFinanceFragment` | `CreateFinanceFragment` | Создание записи |
| `createFinanceCategoryFragment` | `CreateFinanceCategoryFragment` | Создание категории |
| `financeDetailsFragment` | `FinanceDetailsFragment` | Детали категории |
| `goalsFragment` | `GoalsFragment` | Цели |
| `createGoalsFragment` | `CreateGoalsFragment` | Создание цели |
| `goalDetailsFragment` | `GoalDetailsFragment` | Детали цели |

### Анимации
- `slide_up.xml` / `slide_down.xml` — для create-экранов
- `slide_in.xml` / `slide_out.xml` — для detail-экранов
- `fade_in.xml` / `fade_out.xml` — для фоновых переходов

---

## Ресурсы

### Локализация
Поддержка: `ru`, `en-US`, `de`, `fr`, `ru-BY`, `ru-KG`, `ru-KZ`, `ru-MD`, `ru-UA`

### Permissions (AndroidManifest)
- `READ_CONTACTS` — доступ к контактам для подсказки имён
- `VIBRATE` — вибрация при ошибке ввода PIN

### Другие XML-ресурсы
- `menu_bottom_nav.xml` — элементы bottom navigation
- `arrays.xml` — массивы валют и прочее
- `data_extraction_rules.xml` — правила бэкапа
- `provider_paths.xml` — пути для FileProvider (фото целей)
