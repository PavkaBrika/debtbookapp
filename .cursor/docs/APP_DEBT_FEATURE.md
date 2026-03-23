# Debt Feature (app module)

## Назначение

Основная фича приложения — учёт долгов. Позволяет вести список людей (должников / кредиторов), добавлять/редактировать/удалять записи долгов, фильтровать, сортировать, искать, экспортировать и делиться.

---

## Структура файлов

```
app/src/main/java/com/breckneck/debtbook/debt/
├── adapter/
│   ├── HumanAdapter.kt       # Адаптер списка людей (главный экран)
│   ├── DebtAdapter.kt        # Адаптер списка долгов (детали человека)
│   └── ContactsAdapter.kt    # Адаптер выбора контакта из телефонной книги
├── presentation/
│   ├── MainFragment.kt       # Главный экран — список людей с долгами
│   ├── DebtDetailsFragment.kt # Детали долгов конкретного человека
│   └── NewDebtFragment.kt    # Создание/редактирование долга
└── viewmodel/
    ├── MainFragmentViewModel.kt    # VM главного экрана
    ├── DebtDetailsViewModel.kt     # VM деталей
    └── NewDebtViewModel.kt         # VM создания долга
```

### Layouts
- `fragment_main.xml` — главный экран (RecyclerView + итоговые суммы)
- `fragment_debt_details.xml` — детали долгов человека
- `fragment_create_human_debt.xml` — форма создания/редактирования
- `item_human.xml` / `item_human_shimmer.xml` — элемент списка человека
- `item_debt.xml` / `item_debt_shimmer.xml` — элемент списка долга
- `item_contact.xml` — элемент контакта

### Диалоги
- `dialog_sort.xml` — сортировка и фильтрация
- `dialog_contacts.xml` — выбор контакта
- `dialog_change_debt_name.xml` — переименование
- `dialog_share.xml` — поделиться
- `dialog_extra_functions.xml` — доп. функции для записи долга
- `dialog_are_you_sure.xml` — подтверждение удаления

---

## MainFragmentViewModel

Главный ViewModel экрана долгов.

### Зависимости (use cases)
- `GetAllHumansUseCase` — загрузка всех людей
- `GetAllDebtsSumUseCase` — суммы по двум основным валютам
- `GetFirstMainCurrency` / `GetSecondMainCurrency` — валюты для отображения итогов
- `GetHumanOrder` / `SetHumanOrder` — сохранение/загрузка порядка сортировки
- `UpdateHuman` — обновление имени (переименование)

### State
| Поле | Тип | Описание |
|------|-----|---------|
| `screenState` | `LiveData<ScreenState>` | LOADING / EMPTY / SUCCESS |
| `resultedHumanList` | `LiveData<List<HumanDomain>>` | Итоговый (фильтрованный + отсортированный) список |
| `mainSums` | `LiveData<Pair<String, String>>` | Суммы долгов по двум валютам |
| `humanFilter` | `LiveData<Filter>` | ALL / POSITIVE / NEGATIVE |
| `humanOrder` | `LiveData<Pair<HumanOrderAttribute, Boolean>>` | Сортировка (атрибут + ASC/DESC) |
| `isSearching` | `LiveData<Boolean>` | Режим поиска |

### Ключевая логика
- **State machine** через `MutableStateFlow<DebtLogicListState>`: `Loading → Received → Sorted`
- **Фильтрация**: ALL (все), POSITIVE (мне должны), NEGATIVE (я должен)
- **Сортировка**: по имени, сумме, дате; ASC/DESC — через `sortHumans()` из domain
- **Поиск**: debounce 500ms через `StateFlow`, фильтр по имени (case-insensitive)
- **Async**: RxJava `Single` + `CompositeDisposable` для загрузки; Coroutines `StateFlow` для state machine и поиска

### Действия
| Метод | Что делает |
|-------|-----------|
| `setListStateLoading()` | Перезагрузка данных |
| `onSetHumanSort(filter, order)` | Применить фильтр + сортировку |
| `saveHumanOrder(order)` | Сохранить порядок в настройки |
| `updateHuman(human)` | Обновить человека (например, имя) |
| `onStartSearch()` / `onStopSearch()` | Управление поиском |

---

## DebtDetailsViewModel

ViewModel деталей долгов одного человека.

### Зависимости (use cases)
- `GetAllDebtsByIdUseCase` — долги по humanId
- `GetLastHumanIdUseCase` — ID нового человека (при создании)
- `GetHumanSumDebtUseCase` — общая сумма
- `DeleteHumanUseCase` / `DeleteDebtsByHumanIdUseCase` — удаление человека + долгов
- `DeleteDebtUseCase` — удаление конкретного долга
- `AddSumUseCase` — корректировка суммы при удалении долга
- `GetDebtOrder` / `SetDebtOrder` — сортировка долгов
- `SavedStateHandle` — аргументы навигации (humanId, name, currency, newHuman)

### State
| Поле | Тип | Описание |
|------|-----|---------|
| `resultedDebtList` | `LiveData<List<DebtDomain>>` | Список долгов |
| `overallSum` | `LiveData<Double>` | Суммарный долг человека |
| `debtOrder` | `LiveData<Pair<DebtOrderAttribute, Boolean>>` | Сортировка |
| `debtFilter` | `LiveData<Filter>` | Фильтр |
| `humanName` | `LiveData<String>` | Имя человека |

### Ключевая логика
- State machine аналогичная: `Loading → Received → Sorted`
- При удалении долга: вычитание суммы из human через `AddSumUseCase`
- Поддержка `SavedStateHandle` для навигационных аргументов
- Диалоги: удаление человека, настройки долга, сортировка, шаринг

---

## NewDebtViewModel

ViewModel создания/редактирования долга.

### Зависимости
- `GetDefaultCurrency` — валюта по умолчанию
- `GetCurrentDateUseCase` — текущая дата
- `SetDateUseCase` — установка произвольной даты

### State
| Поле | Тип | Описание |
|------|-----|---------|
| `currency` | `LiveData<String>` | Выбранная валюта |
| `date` | `LiveData<String>` | Дата долга |
| `isCurrencyDialogOpened` | `LiveData<Boolean>` | Состояние диалога валют |

### Примечание
Сохранение долга происходит в `NewDebtFragment` / `DebtDetailsFragment` напрямую через use cases, полученные из Koin.

---

## Adapters

### HumanAdapter
- RecyclerView adapter для списка людей
- Поддержка drag & reorder (для изменения порядка)
- Отображает: имя, суммарный долг, валюту
- Callbacks: клик (переход в детали), long press (доп. функции)
- Shimmer layout для загрузки

### DebtAdapter
- RecyclerView adapter для записей долгов
- Отображает: сумму, дату, описание
- Callbacks: клик (настройки долга — edit / delete)
- Shimmer layout

### ContactsAdapter
- RecyclerView adapter для выбора контакта из телефонной книги
- Используется в диалоге при создании долга
- Требует `READ_CONTACTS` permission

---

## Навигация

См. полную карту в `PROJECT_OVERVIEW.md` → «Экраны и переходы». Навигационные аргументы:
- `MainFragment → NewDebtFragment`: idHuman, idDebt, currency, sum, date, info, name
- `MainFragment → DebtDetailsFragment`: humanId, newHuman
- `DebtDetailsFragment → NewDebtFragment`: добавление нового долга к человеку
