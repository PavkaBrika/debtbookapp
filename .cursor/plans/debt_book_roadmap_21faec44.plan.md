---
name: Debt Book Roadmap
overview: "Долгосрочный план возрождения приложения \"Debt Book\": от устранения критических багов и обновления стека до полной миграции на Compose и нового продуктового вектора. План делится на 4 фазы, каждая является фундаментом для следующей."
todos:
  - id: phase0-bugs
    content: "Фаза 0: Исправить критические баги — имя класса App.kt, импорт темы, targetSdk в data модуле"
    status: completed
  - id: phase1-gradle
    content: "Фаза 1.1: Обновить Gradle 7.3.3→8.9+, AGP 7.2.1→8.8+, Kotlin 1.9.0→2.1+, Java 8→17, KAPT→KSP"
    status: completed
  - id: phase1-deps
    content: "Фаза 1.2: Обновить все зависимости (Room, Lifecycle, Compose BOM, Navigation, Koin, Biometric, Glide/Coil, Yandex Ads)"
    status: completed
  - id: phase1-rx
    content: "Фаза 1.3: Мигрировать RxJava 3 → Kotlin Coroutines + Flow во всех ViewModels и use cases"
    status: completed
  - id: phase1-tests
    content: "Фаза 1.4: Улучшить тестовое покрытие domain (70%+), добавить ViewModel тесты, настроить CI/GitHub Actions"
    status: completed
  - id: phase2-hilt
    content: "Фаза 2.1: Мигрировать DI с Koin на Hilt (AppModule, DataModule, DomainModule, ViewModels)"
    status: pending
  - id: phase2-compose
    content: "Фаза 2.2: Мигрировать все 11 фрагментов с XML на Jetpack Compose (по экрану за раз)"
    status: pending
  - id: phase2-edge-to-edge
    content: "Фаза 2.2: Убрать windowOptOutEdgeToEdgeEnforcement из темы, добавить WindowInsets через Scaffold во всех Compose экранах"
    status: pending
  - id: phase2-nav
    content: "Фаза 2.2: Заменить XML nav_graph на Compose Navigation с type-safe routes"
    status: pending
  - id: phase2-orbit-mvi
    content: "Фаза 2.2.1: Мигрировать ViewModels на Orbit MVI (ContainerHost, intent, reduce, sideEffect)"
    status: pending
  - id: phase2-design
    content: "Фаза 2.3: Создать Design System (core/ui модуль), Material 3, Dynamic Color, токены цвета/типографики"
    status: pending
  - id: phase3-ux
    content: "Фаза 3.1: Переработать UX — главный экран, onboarding, пустые состояния, анимации, виджет (Glance)"
    status: pending
  - id: phase3-features
    content: "Фаза 3.2: Добавить напоминания (WorkManager), итоговый баланс, статистику/графики, быстрое добавление, поиск"
    status: pending
  - id: phase3-monetization
    content: "Фаза 3.3: Внедрить Premium подписку через Google Play Billing Library 7+ (DebtBook Pro)"
    status: pending
  - id: phase4-modularize
    content: "Фаза 4: Разбить app модуль на feature-модули, добавить Baseline Profiles, Firebase Crashlytics + Analytics"
    status: pending
  - id: phase4-sdk36
    content: "Фаза 4: Обновить targetSdk до 36 (Android 16) когда Google Play выставит требование, проверить breaking changes"
    status: pending
isProject: false
---

# Debt Book — Долгосрочный Roadmap

## Текущее состояние проекта (аудит)

**Стек (актуально после Фазы 0 + 1.1):**

- AGP `9.1.0`, Gradle `9.4.1`, Kotlin `2.3.20`, KSP `2.3.6` ✅
- Java compatibility `17`, KSP вместо KAPT ✅
- Compose compiler plugin `2.1.0` (новый механизм, без `composeOptions`) ✅
- `compileSdk 35`, `targetSdk 35` — соответствует требованиям Google Play ✅
- Room `2.4.2`, Lifecycle `2.5.0` — устарели (Фаза 1.2)
- Compose BOM `2024.02.01` — только для экрана авторизации (Фаза 1.2)
- Koin `3.5.3` для DI (Фаза 2.1 → Hilt)
- RxJava 3 в ViewModels (Фаза 1.3 → Coroutines)
- XML + ViewBinding для основного UI, Compose только для `AuthorizationActivity`
- Yandex Mobile Ads для монетизации

**⚠️ Технический долг — edge-to-edge (Android 15+):**

В теме добавлен временный opt-out: `android:windowOptOutEdgeToEdgeEnforcement = true`.
Это официальная временная мера от Google. При миграции на Compose (Фаза 2.2) необходимо:

- Убрать `windowOptOutEdgeToEdgeEnforcement` из темы
- Добавить обработку `WindowInsets` через `Scaffold` / `padding(windowInsets)` во всех экранах
- Google может убрать поддержку этого атрибута в будущих версиях SDK

**📌 targetSdk и будущие версии:**

Сейчас `targetSdk = 35`. SDK 36 (Android 16) уже доступен, но Google Play его не требует.
Обновить до `targetSdk 36` когда Play выставит требование — это 5 минут работы + проверка новых breaking changes Android 16.

**Исправленные критические баги (Фаза 0):**

- ✅ Имя класса `sadfsafApp` → `App`
- ✅ Импорт темы из `pokedex` пакета исправлен
- ✅ `targetSdk` выровнен во всех модулях
- ✅ `namespace` вынесен в `build.gradle` (AGP 8.x требование)

---

## Фаза 0 — Критические исправления (1–2 дня)

Без этого проект может не собираться. Выполняется первым делом.

- Исправить имя класса `sadfsafApp` → `App` в `[App.kt](app/src/main/java/com/breckneck/debtbook/app/App.kt)`
- Исправить импорт темы в `AuthorizationActivity` (убрать ссылку на pokedex пакет)
- Выровнять `targetSdk` в `[data/build.gradle](data/build.gradle)` до `34`
- Убедиться, что проект собирается и запускается

---

## Фаза 1 — Техническая гигиена (2–3 недели)

Обновление стека до современного состояния. Без этого невозможна дальнейшая работа.

### 1.1 Обновление Gradle / AGP / Kotlin ✅ ВЫПОЛНЕНО

- Gradle Wrapper: `7.3.3` → `9.4.1` ✅
- Android Gradle Plugin: `7.2.1` → `9.1.0` ✅
- Kotlin: `1.9.0` → `2.3.20` ✅
- KSP: `kotlin-kapt` → `com.google.devtools.ksp:2.3.6` ✅ (с KSP 2.3.0 версия независима от Kotlin)
- Java compatibility: `1.8` → `17` ✅
- `compileSdk` / `targetSdk`: `34` → `35` ✅
- Compose compiler plugin `org.jetbrains.kotlin.plugin.compose:2.3.20` (вместо `composeOptions`) ✅
- `buildConfig true` явно включён (AGP 8+ отключает по умолчанию) ✅
- `packagingOptions` → `packaging` (новый DSL AGP 8+) ✅
- `namespace` вынесен в `build.gradle` (требование AGP 8+) ✅

Ключевые файлы: `[build.gradle](build.gradle)`, `[app/build.gradle](app/build.gradle)`, `[gradle/wrapper/gradle-wrapper.properties](gradle/wrapper/gradle-wrapper.properties)`

### 1.2 Обновление зависимостей


| Библиотека            | Сейчас       | Цель               |
| --------------------- | ------------ | ------------------ |
| Room                  | `2.4.2`      | `2.7+`             |
| Lifecycle / ViewModel | `2.5.0`      | `2.9+`             |
| Compose BOM           | `2024.02.01` | `2025.04+`         |
| Navigation            | `2.7.7`      | `2.9+`             |
| Koin                  | `3.5.3`      | `4.0+`             |
| Biometric             | `1.1.0`      | `1.4+`             |
| Material              | `1.11.0`     | `1.12+`            |
| Glide                 | `4.16.0`     | `4.16+` или Coil 3 |
| Yandex Ads            | `6.4.0`      | latest             |


### 1.3 Миграция RxJava → Coroutines + Flow

RxJava 3 избыточен при наличии Kotlin Coroutines. Миграция снизит сложность и размер APK.

- Заменить `Single/Observable` в use cases на `suspend fun` / `Flow`
- Заменить `RxJava` подписки во ViewModels на `viewModelScope.launch` и `collectAsStateWithLifecycle`
- Удалить зависимости `rxandroid`, `rxjava`

### 1.4 Улучшение тестового покрытия

Сейчас: 4 unit теста в domain, 1 в app, только stub в data/androidTest.

- Довести покрытие domain use cases до 70%+
- Добавить ViewModel тесты с `Turbine` (для Flow)
- Настроить `MockK` вместо Mockito (лучше для Kotlin)
- CI: настроить GitHub Actions для автозапуска тестов

---

## Фаза 2 — Архитектурная модернизация (4–8 недель)

### 2.1 Миграция DI: Koin → Hilt

Hilt — официальный стандарт Google для Android DI, лучше интеграция с Compose, Navigation, WorkManager.

Порядок миграции:

1. Добавить Hilt в `build.gradle`, настроить плагин
2. Аннотировать `App` как `@HiltAndroidApp`
3. Мигрировать `DataModule` → `@Module @InstallIn(SingletonComponent::class)`
4. Мигрировать `DomainModule`
5. Мигрировать `AppModule` (ViewModels → `@HiltViewModel`)
6. Обновить Activities/Fragments (`@AndroidEntryPoint`)
7. Удалить Koin

Файлы DI: `[app/src/main/java/com/breckneck/debtbook/di/](app/src/main/java/com/breckneck/debtbook/di/)`

### 2.2 Полная миграция UI на Jetpack Compose

41 XML layout → Compose экраны. Мигрировать постепенно, экран за экраном.

**⚠️ Обязательно при миграции каждого экрана:**
Убрать `android:windowOptOutEdgeToEdgeEnforcement` из темы и перейти на нативную обработку `WindowInsets` через `Scaffold`/`padding`. После завершения миграции всех экранов атрибут opt-out полностью удаляется из `themes.xml`.

**Порядок миграции (от простых к сложным):**

1. `SettingsFragment` → `SettingsScreen` (мало интерактивности)
2. `GoalsFragment` / `CreateGoalsFragment` / `GoalDetailsFragment`
3. `FinanceFragment` / `CreateFinanceFragment` / `FinanceCategoryFragment`
4. `DebtDetailsFragment` / `NewDebtFragment`
5. `MainFragment` (самый сложный — список с фильтрами)
6. Диалоги: заменить `AlertDialog` / bottom sheets на Compose `ModalBottomSheet` / `AlertDialog`
7. `MainActivity`: убрать `NavHostFragment` из XML, перейти на Compose Navigation

**Compose Navigation (type-safe routes):**

- Перейти с XML `nav_graph.xml` на Compose Navigation с типобезопасными маршрутами (Kotlin Serialization)
- Убрать callback-интерфейсы из `MainActivity`

### 2.2.1 Миграция на Orbit MVI

После миграции экранов на Compose — унифицировать state management через [Orbit MVI](https://orbit-mvi.org/).

**Зачем:**

- Единый паттерн для всех ViewModels (вместо смеси LiveData + StateFlow)
- Чёткое разделение State / SideEffect
- Встроенная поддержка `SavedStateHandle`
- Отличная интеграция с Compose (`collectAsState`)
- Тестируемость из коробки (`runTest`, `awaitState`, `awaitSideEffect`)

**Порядок миграции:**

1. Добавить зависимость `io.orbit-mvi:orbit-compose` и `orbit-viewmodel`
2. Создать базовые классы: `sealed interface UiState`, `sealed interface UiSideEffect`
3. Мигрировать ViewModels по одному:
  - `MainFragmentViewModel` → `ContainerHost<MainState, MainSideEffect>`
  - Заменить `MutableLiveData` / `MutableStateFlow` на `reduce { state.copy(...) }`
  - Заменить одноразовые события (навигация, Toast) на `postSideEffect`
4. В Compose экранах: `val state by viewModel.collectAsState()`
5. Обработка side effects: `viewModel.collectSideEffect { effect -> ... }`

**Пример:**

```kotlin
// State
data class DebtListState(
    val humans: List<HumanDomain> = emptyList(),
    val isLoading: Boolean = true,
    val filter: Filter = Filter.ALL
)

// Side Effects
sealed interface DebtListSideEffect {
    data class NavigateToDetails(val humanId: Int) : DebtListSideEffect
    data class ShowError(val message: String) : DebtListSideEffect
}

// ViewModel
class MainFragmentViewModel(...) : ViewModel(), ContainerHost<DebtListState, DebtListSideEffect> {
    override val container = container<DebtListState, DebtListSideEffect>(DebtListState())

    fun loadHumans() = intent {
        reduce { state.copy(isLoading = true) }
        val humans = getAllHumansUseCase.execute()
        reduce { state.copy(humans = humans, isLoading = false) }
    }

    fun onHumanClick(humanId: Int) = intent {
        postSideEffect(DebtListSideEffect.NavigateToDetails(humanId))
    }
}
```

### 2.3 Единая Design System

- Создать `core/ui` модуль с переиспользуемыми Compose компонентами
- Material 3 (Material You) с поддержкой Dynamic Color (Android 12+)
- Единые токены: цвет, типографика, отступы, иконки
- Light / Dark / Dynamic theme через `DynamicColorScheme`

---

## Фаза 3 — Продуктовые улучшения (параллельно с фазой 2 и после)

### 3.1 Дизайн и UX

- **Material 3 / Material You**: поддержка Dynamic Color (системный акцентный цвет) на Android 12+
- **Переработка главного экрана**: карточки людей с аватарами (инициалы / фото из контактов), суммой долга, цветовой индикацией (красный = должен ты, зелёный = должны тебе)
- **Onboarding**: экран для новых пользователей (0 state), объясняющий функции
- **Пустые состояния**: иллюстрации вместо пустых списков
- **Анимации**: переходы между экранами (Compose shared element transitions)
- **Виджет для рабочего стола** (Glance API): краткая сводка долгов / баланса

### 3.2 Новые функции (по приоритету)

**Высокий приоритет:**

- **Напоминания и уведомления**: установить дату напоминания для долга → push-уведомление (WorkManager + NotificationManager)
- **Итоговый баланс**: на главном экране — общая сумма "мне должны" vs "я должен" с разбивкой по валютам
- **История изменений долга**: лог каждой транзакции по долгу (уже частично есть через `Debt` сущность)
- **Быстрое добавление**: FAB с bottom sheet для быстрого создания записи долга с минимумом полей

**Средний приоритет:**

- **Статистика и графики**: Compose + Vico / MPAndroidChart → графики расходов по категориям, динамика долгов
- **Улучшенный экспорт**: PDF-экспорт в дополнение к Excel (Apache PDFBox или iText)
- **QR-код для долга**: поделиться деталью долга через QR
- **Поиск**: глобальный поиск по именам людей и суммам

**Низкий приоритет:**

- **Повторяющиеся долги**: авто-создание долга по расписанию (аренда, подписки)
- **Теги/метки** для долгов и финансов
- **Улучшенная синхронизация**: фоновая синхронизация с Google Drive через WorkManager вместо ручной

### 3.3 Монетизация

Текущая схема: Yandex Mobile Ads (баннер + интерстициал).

**Предлагаемая стратегия:**

- **Premium подписка** (`DebtBook Pro`) через Google Play Billing Library 7+:
  - Безлимитное количество людей (сейчас нет ограничений? — оценить)
  - Отключение рекламы
  - PDF экспорт
  - Расширенная статистика
  - Приоритетная поддержка
- **Реклама**: оставить только для free-пользователей, убрать баннер — оставить только интерстициал (меньше раздражает)
- **Разовая покупка** "Убрать рекламу" как альтернатива подписке

### 3.4 Рост и удержание

- **Оценка приложения**: улучшить flow (`ReviewManager` из Play In-App Review API вместо кастомного диалога)
- **Локализация**: уже есть поддержка RU/DE/FR — дополнить ES, PT, UK
- **App Shortcuts**: быстрые действия с иконки (добавить долг, открыть цели)
- **Backup improvements**: автоматический бэкап каждые N дней через WorkManager

---

## Фаза 4 — Качество и масштабирование

- **Модуляризация по фичам**: разбить `app` модуль на `feature:debt`, `feature:finance`, `feature:goals`, `feature:settings`, `feature:auth`
- **Baseline Profiles**: для ускорения запуска приложения
- **App size**: R8 / ProGuard тонкая настройка, убрать лишние зависимости
- **Analytics**: Firebase Analytics или Amplitude для понимания поведения пользователей
- **Crashlytics**: Firebase Crashlytics для отслеживания крашей

---

## Порядок выполнения (рекомендуемый)

```
Фаза 0 (исправление багов) → Фаза 1.1 (Gradle/Kotlin) → Фаза 1.2 (зависимости) → 
Фаза 1.3 (Rx→Coroutines) → Фаза 2.1 (Hilt) → Фаза 2.2 (Compose, экран за экраном) → 
Фаза 2.2.1 (Orbit MVI) → Фаза 2.3 (Design System) → Фаза 3.1 (UX) → Фаза 3.2 (фичи) → Фаза 3.3 (монетизация)
```

Каждая следующая фаза начинается после стабилизации предыдущей и выпуска обновления в Google Play.