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
Appyx consist of three main libraries that complement itself ***navigation***, ***interactions*** and ***components***
this allows us to create an application that is fully customized.

*libs.versions.toml*

```toml
[versions]
appyx = "2.0.1"

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

With dependencies added we can start to create the navigation. Following the
Appyx [documentation](https://bumble-tech.github.io/appyx/navigation/) we can notice one major thing, the Appyx gives us
a flexibility in the interpreting term "navigation". Most modern libraries/solutions ficus on gow to get from one screen
to another, but Appyx gives us a possibility to create a navigation that is not only about screens but "viewport" it can
be what you can imagine, for example spinning the carousel.
Nevertheless, we will focus on traditional `Stack` navigation. There are some basic blocks that we need to use. First
one is `Node` which is a representation of the structure (in our case the screen). Each node can hold other notes, and
they are called ***children***. The ***node*** is standalone unit with own:

- [AppyxComponent](https://bumble-tech.github.io/appyx/components/) - in our case it will be ***back stack***, with
  simple linear navigation. Element at front is consider active, other as stashed. It can never be empty. It has some
  basic functions helper functions as ***push***, ***pop***, ***replace*** and default back handler.
- [Lifecycle](https://bumble-tech.github.io/appyx/navigation/features/lifecycle/) - it's a multiplatform interfaces that
  notifies the component about the state changes on the platform. For example on he Android platform it is implemented
  with ***AndroidLifecycle***.
- State Restoration after orientation changes
- The View, that is created with `@composable`
- Business logic
- Plugins - since ***Nodes*** should be kept lean, the plugins are used to add additional functionality, for
  example `NodeLifecycleAware` that allows to listen to the lifecycle events.

The ***nodes*** can be as small as you want to keep the complexity of your logic low and encapsulated end extracted to
the **children*** to compose the process. With the nodes your navigation can work as a tree with multiple branches
responsible for different processes. Some parts of the tree are active - visible on the screen, other are stashed. To
change what's currently active we will use the component, the change will look like navigation. By the adding or removing nodes of node.

There is also [ChildAwareAPI](https://bumble-tech.github.io/appyx/navigation/features/childaware/) that helps with
communication between parent and dynamically added child.








---

### Summary

Cool that it supports WEB also!