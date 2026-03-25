# Core, DI & Auth (app module)

## Назначение

Корневые компоненты приложения: Application class, MainActivity, DI-конфигурация (Hilt), Compose-тема, кастомные View, навигация, авторизация (PIN + биометрия).

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
    ├── DataModule.kt                  # Hilt: Storage + Repository bindings
    └── DomainModule.kt                # Hilt: Use Cases
```

---

## App.kt

`Application` subclass. Инициализация при старте:

1. **Yandex Mobile Ads SDK**: `MobileAds.initialize()`
2. **Hilt DI**: аннотирован `@HiltAndroidApp` — Hilt автоматически инициализирует граф зависимостей.

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

## DI — Hilt Modules

DI мигрирован с **Koin 4.x** на **Hilt 2.55** (Фаза 2.1). Все ViewModels аннотированы `@HiltViewModel @Inject constructor`, Activities и Fragments — `@AndroidEntryPoint`.

### ViewModels (14 штук)
Провайдятся автоматически через `@HiltViewModel`. Полный список: `MainFragmentViewModel`, `MainActivityViewModel`, `DebtDetailsViewModel`, `SettingsViewModel`, `NewDebtViewModel`, `SynchronizationViewModel`, `FinanceViewModel`, `CreateFinanceViewModel`, `CreateFinanceCategoryViewModel`, `FinanceDetailsViewModel`, `GoalsFragmentViewModel`, `CreateGoalsFragmentViewModel`, `GoalDetailsFragmentViewModel`, `AuthorizationViewModel`.

### DataModule (`di/DataModule.kt`)
`@Module @InstallIn(SingletonComponent::class)`. Binding Storage → Implementation, Repository → Implementation через `@Provides`:

| Binding | Scope | Реализация |
|---------|-------|-----------|
| `HumanStorage` | **`@Singleton`** | `DataBaseHumanStorageImpl` |
| `HumanRepository` | **`@Singleton`** | `HumanRepositoryImpl` |
| `DebtStorage` | **`@Singleton`** | `DataBaseDebtStorageImpl` |
| `DebtRepository` | **`@Singleton`** | `DebtRepositoryImpl` |
| `SettingsStorage` | **`@Singleton`** | `SharedPrefsSettingsStorageImpl` |
| `SettingsRepository` | **`@Singleton`** | `SettingsRepositoryImpl` |
| `FinanceStorage` / `FinanceRepository` | **`@Singleton`** | DB impl |
| `FinanceCategoryStorage` / `FinanceCategoryRepository` | **`@Singleton`** | DB impl |
| `AdStorage` / `AdRepository` | **`@Singleton`** | SharedPrefs impl |
| `GoalStorage` / `GoalRepository` | **`@Singleton`** | DB impl |
| `GoalDepositStorage` / `GoalDepositRepository` | **`@Singleton`** | DB impl |

**Важно**: все Storage и Repository — **`@Singleton`**, т.к. каждый `DataBase*StorageImpl` создаёт `RoomDatabase` внутри.

### DomainModule (`di/DomainModule.kt`)
`@Module @InstallIn(SingletonComponent::class)`. Регистрирует use cases через `@Provides`:
- Human (9 use cases): transient
- Debt (12 use cases): transient
- Settings (22 use cases): все **`@Singleton`** — Get/Set пары для каждой настройки
- Finance (7 use cases): **`@Singleton`**
- FinanceCategory (6 use cases): **`@Singleton`**
- Ad (2 use cases): transient
- Goal (5 use cases): transient
- GoalDeposit (5 use cases): transient

**Важно**: Settings и Finance use cases — **`@Singleton`**, остальные — transient.

---

## Compose Theme (`core/ui/theme/`)

Используется только в `AuthorizationScreen`. `DebtBookTheme` — `@Composable` функция: `Color.kt` (цвета), `Shape.kt` (формы), `Type.kt` (типографика), `Theme.kt` (объединение + dark/light через `isSystemInDarkTheme()`).

---

## Shared Compose Components (`core/ui/components/`)

Переиспользуемые Compose-компоненты для экранов фазы 2.2:

| Файл | Описание | Используется в |
|------|----------|----------------|
| `DebtBookTopBar.kt` | `TopAppBar` с опциональной back-кнопкой, заголовком и слотом для action-иконок | Все экраны |
| `SettingsPickerBottomSheet.kt` | `ModalBottomSheet` с `LazyColumn` + `RadioButton` для выбора одного значения из списка | NewDebt (валюта), CreateGoals (валюта), Finance (валюта, интервал), Settings (валюта x3, тема) |
| `ConfirmationBottomSheet.kt` | `ModalBottomSheet` с заголовком, сообщением и кнопками Да/Нет | DebtDetails, CreateFinance, Synchronization |
| `ExtraFunctionsBottomSheet.kt` | `ModalBottomSheet` с кнопками Удалить / Изменить / Отмена | DebtDetails, FinanceDetails, GoalDetails |
| `SortFilterBottomSheet.kt` | `ModalBottomSheet` с двумя секциями RadioGroup (сортировка + фильтр) и чекбоксом "Запомнить выбор" | MainFragment, DebtDetails |
| `EmptyListPlaceholder.kt` | Empty state (иконка + текст); `ShimmerListPlaceholder` (скелетон-анимация при загрузке) | Все экраны со списками |
| `AdBannerComposable.kt` | `AndroidView`-обёртка для Yandex `BannerAdView` | Экраны с баннерной рекламой |

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

## Unit-тесты ViewModel

**Инструменты**: JUnit 4, Mockito 5, `core-testing:2.2.0` (InstantTaskExecutorRule), `kotlinx-coroutines-test:1.9.0`  
**Итог**: 23 теста, 0 failures

| Файл | ViewModel | Покрытие |
|------|-----------|---------|
| `MainFragmentViewModelTest.kt` | `MainFragmentViewModel` | getHumanOrder, saveHumanOrder, setIsSearching, sort dialog open/close |
| `SettingsViewModelTest.kt` | `SettingsViewModel` | init загрузка данных, set/get валют, тема, share text, fingerprint, getUserData, диалог настроек |

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
