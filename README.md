# Appyx Navigation

This is a Kotlin Multiplatform project targeting Android and iOS where we will showcase the Appyx as the app
navigation.

Assumptions:

- Application should allow us to navigate from one screen to another.
- Application should allow to pass some parameters from first to second screen.
- Application should handle the screen rotation without loosing data.
- Application should handle the Tab Navigation.
- Application should handle the async operations with coroutines.

In the next posts I will also cover
the [Voyager](https://github.com/mkonkel/VoyagerNavigation), [Decompose](https://github.com/mkonkel/DecomposeNavigation)
and
Composer navigation libraries.

### The project:

Base project setup as always is made with [Kotlin Multiplatform Wizard](https://kmp.jetbrains.com), we also need to add
[Appyx](https://github.com/bumble-tech/appyx) as it is the core thing that we would like to examine.
Appyx consist of three main libraries that complement itself ***navigation***, ***interactions*** and ***components*** this allows us to create an application that is fully customized.

*libs.versions.toml*

```toml
[versions]
appyx="2.0.1"

[libraries]
appyx-navigation = { module = "com.bumble.appyx:appyx-navigation", version.ref = "appyx" }
appyx-interactions = { module = "com.bumble.appyx:appyx-interactions", version.ref = "appyx" }
appyx-components-backstack = { module = "com.bumble.appyx:backstack", version.ref = "appyx" }

```

Freshly added dependencies needs to be synced with the project and added to the ***build.gradle.kts***

```kotlin
sourceSets {
    commonMain.dependencies {
        ...
        implementation(libs.appyx.navigation)
        implementation(libs.appyx.interactions)
        implementation(libs.appyx.components.backstack)
    }
}
```