    # Finance Feature (app module)

## Назначение

Учёт доходов и расходов. Пользователь видит категории с процентным распределением, может переключаться между расходами и доходами, выбирать временной интервал (день/неделя/месяц/год), создавать категории и записи.

---

## Структура файлов

```
app/src/main/java/com/breckneck/debtbook/finance/
├── adapter/
│   ├── FinanceAdapter.kt                # Список финансовых записей
│   ├── FinanceCategoryAdapter.kt        # Категории с прогресс-барами
│   ├── FinanceCategoryColorAdapter.kt   # Сетка цветов при создании категории
│   ├── FinanceCategoryImageAdapter.kt   # Сетка emoji-иконок при создании категории
│   └── UsedFinanceCategoryAdapter.kt    # Выбор категории при создании записи
├── customview/
│   └── FinanceProgressBar.kt            # Кастомный прогресс-бар (% от общей суммы)
├── presentation/
│   ├── FinanceFragment.kt               # Основной экран финансов
│   ├── CreateFinanceFragment.kt         # Создание/редактирование записи
│   ├── CreateFinanceCategoryFragment.kt # Фрагмент-хост для Compose экрана категории
│   ├── CreateFinanceCategoryScreen.kt   # Compose UI создания категории
│   └── FinanceDetailsFragment.kt        # Детали категории (история)
├── util/
│   └── GetFinanceCategoryNameInLocalLanguage.kt  # Локализация имён категорий
└── viewmodel/
    ├── FinanceViewModel.kt              # VM основного экрана
    ├── CreateFinanceViewModel.kt        # VM создания записи
    ├── CreateFinanceCategoryViewModel.kt # VM создания категории
    └── FinanceDetailsViewModel.kt       # VM деталей
```

### Layouts
- `fragment_finance.xml` — основной экран (переключатель + интервал + RecyclerView)
- `fragment_create_finance.xml` — форма записи
- `fragment_create_finance_category.xml` — форма категории
- `fragment_finance_details.xml` — история по категории
- `item_finance.xml` — элемент финансовой записи
- `item_finance_category.xml` — элемент категории
- `item_finance_history.xml` — элемент истории
- `item_category_color.xml` — элемент палитры цветов
- `item_category_image.xml` — элемент палитры emoji
- `item_used_category.xml` / `item_used_category_shimmer.xml` — категория в пикере

---

## FinanceViewModel

Основной VM экрана финансов.

### Зависимости
- `GetFinanceCurrency` / `SetFinanceCurrency` — валюта финансов
- `GetAllCategoriesWithFinances` — все категории с вложенными записями

### State
| Поле | Тип | Описание |
|------|-----|---------|
| `categoriesWithFinancesList` | `LiveData<List<FinanceCategoryWithFinances>>` | Отфильтрованные категории + записи |
| `financeCategoryState` | `LiveData<FinanceCategoryState>` | EXPENSE / INCOME |
| `financeInterval` | `LiveData<FinanceInterval>` | DAY / WEEK / MONTH / YEAR |
| `financeIntervalString` | `LiveData<String>` | Строковое представление интервала |
| `financeIntervalUnix` | `LiveData<Pair<Long, Long>>` | Границы интервала в millis |
| `overallSum` | `LiveData<Double>` | Сумма за интервал |
| `currency` | `LiveData<String>` | Текущая валюта |
| `financeListState` | `LiveData<ListState>` | LOADING / EMPTY / SUCCESS |

### Ключевая логика
- **Интервальная навигация**: `getNextFinanceInterval()` / `getPastFinanceInterval()` — сдвиг интервала вперёд/назад
- **Фильтрация категорий**: по `FinanceCategoryState` (расходы/доходы) + по временному интервалу
- **Процент по категории**: `categoryPercentage = (categorySum * 100) / overallSum`
- **Сортировка**: категории сортируются по `categoryPercentage`
- Загрузка через `viewModelScope.launch` + `withContext(Dispatchers.IO)` + фильтрация в IO-потоке

### Действия
| Метод | Что делает |
|-------|-----------|
| `onChangeFinanceCategoryState()` | Переключение EXPENSE ↔ INCOME |
| `setInterval(interval)` | Смена интервала (DAY/WEEK/MONTH/YEAR) |
| `getNextFinanceInterval()` | Следующий период |
| `getPastFinanceInterval()` | Предыдущий период |
| `setCurrency(currency)` | Смена валюты |

---

## CreateFinanceViewModel

### Зависимости
- `SetFinance` / `UpdateFinance` / `DeleteFinance` — CRUD записей
- `GetAllFinanceCategories` / `GetFinanceCategoriesByState` — список категорий
- `DeleteFinanceCategoryUseCase` / `DeleteAllFinancesByCategoryId` — удаление категории

### Ключевая логика
- Создание новой финансовой записи с привязкой к категории
- Выбор категории из списка (фильтрованного по state)
- Удаление категории с каскадным удалением записей

---

## CreateFinanceCategoryViewModel

Реализует `ContainerHost<CreateFinanceCategoryState, CreateFinanceCategorySideEffect>` (Orbit MVI).

### Зависимости
- `SetFinanceCategory` — создание категории
- `orbit-viewmodel` / `orbit-core` — контейнер состояния

### State (`CreateFinanceCategoryState`)

Дефолтное состояние: `CreateFinanceCategoryState.initial()`.

| Поле | Тип | Описание |
|------|-----|---------|
| `categoryName` | `String` | Текущее имя категории |
| `selectedImage` | `Int?` | Unicode codepoint выбранного emoji |
| `selectedColor` | `String?` | HEX-строка выбранного цвета |
| `financeCategoryState` | `FinanceCategoryState` | EXPENSE / INCOME |
| `isNameErrorVisible` | `Boolean` | Показывать ошибку валидации имени |
| `isImageErrorVisible` | `Boolean` | Показывать ошибку валидации иконки |
| `isColorErrorVisible` | `Boolean` | Показывать ошибку валидации цвета |

Строки ошибок резолвятся в UI через `stringResource` только когда флаг `true`.

### Side effects (`CreateFinanceCategorySideEffect`)
| Эффект | Когда |
|--------|-------|
| `CategorySaved` | После успешной записи в БД — инициирует навигацию назад |

### Intent-функции
| Метод | Что делает |
|-------|-----------|
| `onNameChange(value)` | Обновляет имя, сбрасывает `isNameErrorVisible` если непусто; ограничение 20 символов |
| `onImageSelected(image)` | Сохраняет выбор emoji (codepoint), сбрасывает `isImageErrorVisible` |
| `onColorSelected(color)` | Сохраняет выбор цвета (hex), сбрасывает `isColorErrorVisible` |
| `setFinanceCategoryState(state)` | Задаёт EXPENSE / INCOME |
| `onSaveClick()` | Валидация → `reduce` флагов ошибок → `withContext(IO) { setFinanceCategory.execute(...) }` → `postSideEffect(CategorySaved)` |

---

## FinanceDetailsViewModel

### Зависимости
- `GetFinanceByCategoryId` — записи по категории
- `DeleteFinance` — удаление записи

### Ключевая логика
- Отображение истории финансовых записей конкретной категории
- Удаление отдельных записей

---

## Custom Views

### FinanceProgressBar
Кастомный `View` для отображения процента категории от общей суммы. Отрисовывает цветную полосу пропорционально `categoryPercentage`. Атрибуты задаются через `finance_progress_bar_attrs.xml`.

---

## Особенности

- Дефолтные категории создаются при первом запуске (7 расходов + 4 дохода, определены в `ConstantsDomain.kt`)
- Имена категорий локализуются через `GetFinanceCategoryNameInLocalLanguage`
- Emoji отображаются как Unicode codepoints через `String(Character.toChars(codepoint))`
- Доступны **24 цвета** (19 пастельных Material 200 + 5 accent оттенков) и **80 эмодзи** (8 категорий × 10)
- Эмодзи-пикер группирует иконки по **8 категориям** (Food, Shopping, Fun, Health, Education, Finance, Home, Nature) с **10 эмодзи в каждой** (80 всего). Определены в `EmojiGroups.kt`. UI: `SecondaryScrollableTabRow` с текстовыми лейблами + `HorizontalPager` + фиксированная сетка 5 колонок (2 ряда)
- Цветовой пикер: `FlowRow` с круглыми `Surface(onClick)` элементами 40dp, анимация масштабирования при выборе
