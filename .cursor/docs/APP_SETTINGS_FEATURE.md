# Settings & Synchronization Feature (app module)

## Назначение

Настройки приложения и синхронизация данных через Google Drive. Позволяет менять валюты, тему, настройки шаринга, управлять безопасностью и выполнять бэкап/восстановление.

---

## Структура файлов

```
app/src/main/java/com/breckneck/debtbook/settings/
├── adapter/
│   └── SettingsAdapter.kt             # Адаптер для списка настроек (radio-style)
├── presentation/
│   ├── SettingsFragment.kt            # Экран настроек
│   └── SynchronizationFragment.kt     # Экран синхронизации
├── util/
│   └── DriveServiceHelper.kt          # Хелпер Google Drive API
└── viewmodel/
    ├── SettingsViewModel.kt           # VM настроек
    └── SynchronizationViewModel.kt    # VM синхронизации
```

### Layouts
- `fragment_settings.xml` — экран настроек
- `fragment_synchronization.xml` — экран синхронизации
- `dialog_setting.xml` — диалог выбора значения настройки
- `item_setting.xml` — элемент настройки (radio button)

---

## SettingsViewModel

VM экрана настроек.

### Зависимости
- `Get/SetFirstMainCurrency` — основная валюта 1
- `Get/SetSecondMainCurrency` — основная валюта 2
- `Get/SetDefaultCurrency` — валюта по умолчанию для новых долгов
- `Get/SetAddSumInShareText` — включать ли сумму в текст при шаринге
- `Get/SetAppTheme` — тема (светлая/тёмная/системная)
- `GetIsAuthorized` — авторизован ли для Google Drive
- `GetUserData` — данные пользователя (имя, email)
- `Get/SetIsFingerprintAuthEnabled` — включение биометрии

### State
| Поле | Тип | Описание |
|------|-----|---------|
| `firstMainCurrency` | `LiveData<String>` | Валюта 1 |
| `secondMainCurrency` | `LiveData<String>` | Валюта 2 |
| `defaultCurrency` | `LiveData<String>` | Валюта по умолчанию |
| `addSumInShareText` | `LiveData<Boolean>` | Сумма в тексте шаринга |
| `appTheme` | `LiveData<String>` | Текущая тема |
| `isAuthorized` | `LiveData<Boolean>` | Авторизация Google |
| `userName` / `emailAddress` | `LiveData<String>` | Данные пользователя |
| `isFingerprintAuthEnabled` | `LiveData<Boolean>` | Биометрия |
| `isSynchronizationAvailable` | `LiveData<Boolean>` | Доступна ли синхронизация |

### Ключевая логика
- Все настройки загружаются при init
- Изменение настройки мгновенно сохраняется через соответствующий use case
- Диалог настроек: универсальный `dialog_setting.xml` с `SettingsAdapter` (radio-кнопки)
- Тема применяется через `AppCompatDelegate.setDefaultNightMode()` в Fragment

---

## SynchronizationViewModel

VM экрана синхронизации с Google Drive.

### Зависимости
- `GetIsAuthorized` / `SetIsAuthorized` — статус авторизации
- `GetAllDebts` / `GetAllHumansUseCase` — все долги/люди для бэкапа
- `GetAllFinances` / `GetAllFinanceCategories` — все финансы/категории
- `GetAllGoals` / `GetAllGoalDeposits` — все цели/депозиты
- `ReplaceAllDebts` / `ReplaceAllHumans` — восстановление долгов
- `ReplaceAllFinances` / `ReplaceAllFinanceCategories` — восстановление финансов
- `ReplaceAllGoals` / `ReplaceAllGoalDeposits` — восстановление целей
- `SetUserData` — сохранение данных пользователя
- `SetDateUseCase` — форматирование даты
- `Set/GetLastSyncDate` — дата последней синхронизации

### Ключевая логика
- **Бэкап**: сериализация `AppDataLists` (все данные) → JSON → upload на Google Drive
- **Восстановление**: download JSON → десериализация → `replaceAll*` (полная замена данных)
- **Авторизация**: Google Sign-In + Google Drive scope
- **DriveServiceHelper**: обёртка над Google Drive API v3 (upload/download файлов)

---

## DriveServiceHelper

Утилитный класс для работы с Google Drive API.

### Основные операции
- Создание/поиск файла на Drive
- Upload содержимого (JSON backup)
- Download содержимого (JSON restore)
- Использует legacy Google HTTP Client + Gson

---

## SettingsAdapter

RecyclerView адаптер для отображения вариантов настроек в диалоге:
- Radio-style выбор (одно значение из списка)
- Используется для: выбора валют, темы, интервала

---

## Настройки приложения (полный список)

| Настройка | Описание | Тип |
|-----------|---------|-----|
| First Main Currency | Валюта 1 для итогов на главном экране | String |
| Second Main Currency | Валюта 2 для итогов | String |
| Default Currency | Валюта по умолчанию для новых долгов | String |
| Finance Currency | Валюта для финансов | String |
| App Theme | Светлая / Тёмная / Системная | String |
| Debt Order | Сортировка долгов | Pair<DebtOrderAttribute, Boolean> |
| Human Order | Сортировка людей | Pair<HumanOrderAttribute, Boolean> |
| Add Sum In Share Text | Включать сумму в шаринг | Boolean |
| PIN Code Enabled | PIN-замок включён | Boolean |
| PIN Code | Зашифрованный PIN-код | String |
| Fingerprint Auth Enabled | Биометрия включена | Boolean |
| Is Authorized | Авторизован в Google | Boolean |
| User Data | Имя + email | User (JSON) |
| Last Sync Date | Timestamp последней синхронизации | Long |
| Debt Quantity For Rate Dialog | Порог для показа диалога оценки | Int |

---

## Связанные документы

- Auth (PIN/биометрия): см. `APP_CORE.md` → раздел «Auth»
- Полная карта навигации: см. `PROJECT_OVERVIEW.md` → «Экраны и переходы»
- Настройки domain-модели: см. `DOMAIN_MODULE.md` → `SettingsRepository`
