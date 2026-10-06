# Task App

Учебное Android-приложение: список задач на Kotlin + Jetpack Compose + Room + Hilt.

**Стек:** Compose (Material 3), Navigation Compose, Room, Hilt, Kotlin Flow/StateFlow.
**Запуск:** открыть в Android Studio → Sync → Run.

**Содержание:** [Карта](#карта-проекта) · [Поток данных](#поток-данных) ·
[Рецепты](#рецепты) · [Грабли](#грабли-и-решения) · [Миграции](#миграции-room) ·
[Нюансы](#известные-нюансы) · [Сделано](#сделано) · [TODO](#todo)

---

## Карта проекта

| Слой | Файл | За что отвечает |
|---|---|---|
| UI | `TaskListScreen`, `AddTaskScreen`, `EditTaskScreen` | рисуют экран, о действиях сообщают колбэками |
| UI | `TaskItem`, `TaskList` | карточка задачи и список |
| UI | `AppNavigation`, `Routes` | навигация, защита от двойных нажатий |
| UI | `ui.theme` | цвета, шрифты, тема |
| State | `TaskViewModel`, `TaskUiState` | состояние экрана, действия пользователя |
| Data | `TaskRepository` | посредник между ViewModel и DAO |
| Data | `TaskMapper` | `TaskEntity` ↔ `Task` (extension-функции) |
| Data | `TaskDao`, `TaskEntity`, `TaskDatabase` | таблица, запросы, миграции |
| DI | `DatabaseModule` | как Hilt создаёт БД и DAO |
| Корень | `MainActivity`, `TaskApplication` | точка входа и `@HiltAndroidApp` |

<details>
<summary>Дерево пакетов</summary>

```text
com.example.taskapp
├── data
│   ├── local        TaskDao, TaskDatabase, TaskEntity
│   ├── mapper       TaskMapper
│   └── repository   TaskRepository
├── di               DatabaseModule
├── domain.model     Task
├── presentation
│   ├── components   TaskItem, TaskList
│   ├── navigation   Routes
│   ├── screens      AddTaskScreen, EditTaskScreen, TaskListScreen
│   ├── task         TaskUiState, TaskViewModel
│   └── AppNavigation
├── ui.theme         Color, Theme, Type
├── MainActivity
└── TaskApplication
```
</details>

## Поток данных

```text
Действие:  Compose → ViewModel → Repository → DAO → Room
Данные:    Room → DAO (Flow<Entity>) → Repository (map → Flow<Task>)
           → ViewModel (StateFlow<UiState>) → Compose
```

Правила:
- UI и ViewModel знают только `Task`, никогда `TaskEntity`.
- ViewModel не обращается к Room напрямую.
- Экраны не знают про `navController`, наружу идут колбэки (`onTaskSave`, `onTaskBack`).

<details>
<summary>🗄️ Room — путь данных</summary>

### Чтение

```text
Room Database
    ↓
TaskDao
    ↓
Flow<List<TaskEntity>>
    ↓
TaskRepository
    ↓
TaskMapper
    ↓
Flow<List<Task>>
    ↓
TaskViewModel
    ↓
StateFlow<TaskUiState>
    ↓
Compose
```

### Запись

```text
Compose
    ↓
TaskViewModel
    ↓
TaskRepository
    ↓
TaskMapper
    ↓
TaskEntity
    ↓
TaskDao
    ↓
Room
```

### Кто за что отвечает

| Класс | Роль |
|---|---|
| `TaskEntity` | представление данных для БД |
| `TaskDao` | SQL-запросы и операции Room |
| `TaskDatabase` | сама БД + доступ к DAO |
| `TaskMapper` | `TaskEntity` ↔ `Task` |
| `TaskRepository` | посредник между ViewModel и DAO |

</details>

## Рецепты

### Добавить поле в задачу (например, `dueDate`)
1. `TaskEntity`: добавить поле с дефолтом.
2. `TaskDatabase`: `version + 1`, написать `MIGRATION_N_N+1`.
3. `DatabaseModule`: добавить миграцию в `.addMigrations(...)`.
4. `Task` и `TaskMapper`: добавить поле **в обе стороны** (`toTask` и `toEntity`).
5. UI: показать/редактировать.

Шаблон миграции:
```kotlin
val MIGRATION_3_4 = object : Migration(3, 4) {
  override fun migrate(database: SupportSQLiteDatabase) {
    database.execSQL("ALTER TABLE TaskEntity ADD COLUMN dueDate INTEGER NOT NULL DEFAULT 0")
  }
}
```

### Добавить новое действие (например, «архивировать»)
`DAO` → `Repository` → `ViewModel` → колбэк в экране → вызов в `AppNavigation`.

### Добавить новый экран
1. Константа в `Routes`.
2. `composable(Routes.X) { ... }` в `AppNavigation`.
3. Переход через `navigate(...) { launchSingleTop = true }` + проверка `isResumed()`.

### Аргумент навигации
```kotlin
Routes.edit(taskId)            // "edit/5"

backStackEntry.arguments
    ?.getString("taskId")
    ?.toLongOrNull()           // id типа Long
```

### Обработка состояния
```kotlin
when (val state = uiState) {
  TaskUiState.Loading -> ...
  is TaskUiState.Success -> state.tasks
  is TaskUiState.Error -> state.message
}
```

## Грабли и решения

| Проблема | Решение |
|---|---|
| Быстрые нажатия открывают несколько экранов | `launchSingleTop = true` + `backStackEntry.isResumed()` |
| Дубли задач при быстром «Сохранить» | флаг `isSaving` в экране |
| Закрылся лишний экран при двойном «Назад» | проверка `isResumed()` перед `popBackStack()` |
| Текст в поле пропадает при повороте | `rememberSaveable` вместо `remember` |
| Задачи в случайном порядке | `ORDER BY isCompleted ASC, id DESC` |
| Нет анимации перемещения | `key = { it.id }` + `Modifier.animateItem()` |
| Иконки не импортируются | зависимость `material-icons-core` |
| Фризы при запуске из Studio | это debug-сборка; проверять на release |
| Новое поле сбрасывается при `@Update` | добавить поле в `Task` **и** в `TaskMapper` (в обе стороны) |

## Миграции Room

- Меняется `TaskEntity` → `version + 1` + новая `Migration`.
- Новую миграцию добавить в `DatabaseModule` → `.addMigrations(...)`.
- Старые миграции не трогаем, цепочка `1 → 2 → 3 → ...` должна быть целой.
- Сейчас: **версия 3** (`description`, `priority`).

## Известные нюансы

- `exportSchema = false`: для учебного проекта нормально, для реального лучше включить экспорт схем.

## Сделано

- [x] Room + миграции 1→2→3
- [x] Hilt, ViewModel, `StateFlow<TaskUiState>`
- [x] Сортировка (невыполненные внизу, новые сверху)
- [x] Анимация перемещения и удаления (`animateItem`)
- [x] Защита от двойных нажатий (навигация и сохранение)
- [x] `Scaffold`, `TopAppBar`, FAB на всех экранах
- [x] Разделение на пакеты и файлы
- [x] Отдельный `EditTaskViewModel` (задача по `id` из `SavedStateHandle`)

## TODO

- [ ] Поля `description` и `priority` в UI
- [ ] Snackbar «Отменить» при удалении
- [ ] Свайп для удаления
- [ ] Тесты ViewModel
