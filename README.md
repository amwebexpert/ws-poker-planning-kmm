# ws-poker-planning-kmm

Kotlin Multiplatform Mobile (KMM) PokerPlanning client app

## What's included in this proof of concept

This POC shows usage of the following kotlin multiplatform libraries:

- `ktor-client-core` for doing classic HTTP communications
- `ktor-client-websockets` for web socket full duplex communications support
- `kotlinx-coroutines-core` for asynchronous method and multi-threading
- `kotlinx-serialization-json` allows objects annotations to support JSON serialization
- `kotlinx-datetime` to handle ISO-8601 datetime format

### `shared` module includes

- classic KMM `ExampleService` to show `expect` / `actual` platform implementations (Android, iOS)
- the `ExampleService` also includes a classic HTTP communication through `ktor`
- poker planning business models (enum, interfaces, classes)
- `PokerPlanningService` business logic implementation
- `WebSocketService` as a `ktor` wrapper to handle web socket messaging exchange

### `androidApp` module includes

- `MainViewModel.kt` example showing two ways binding (between the UI and the `shared` module)
- `androidx.compose` usage example (for the two way binding mechanism)
- `MutableSharedFlow` is used to model web socket incoming message as they are received by the `WebSocketService`
- `Flow` is used to model HTTP call result received by `ExampleService`

### `iosApp` module includes

- `ContentView.swift` example showing two ways binding (between the UI and the `shared` module)
