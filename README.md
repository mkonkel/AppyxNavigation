# Appyx Navigation

This is a Kotlin Multiplatform project targeting Android and iOS where we will showcase the Appyx as
the app
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

Base project setup as always is made with [Kotlin Multiplatform Wizard](https://kmp.jetbrains.com),
we also need to add
[Appyx](https://github.com/bumble-tech/appyx) as it is the core thing that we would like to examine.
Appyx consist of three main libraries that complement itself ***navigation***, ***interactions***
and ***components***
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

Freshly added dependencies needs to be synced with the project and added to the
***build.gradle.kts***

```kotlin
sourceSets {
    commonMain.dependencies {
        ...
        implementation(libs.appyx.navigation)
        implementation(libs.appyx.interactions)
        api(libs.appyx.components.backstack)
    }
}
```

With dependencies added we can start to create the navigation. Following the
Appyx [documentation](https://bumble-tech.github.io/appyx/navigation/) we can notice one major
thing, the Appyx gives us
a flexibility in the interpreting term "navigation". Most modern libraries/solutions ficus on gow to
get from one screen
to another, but Appyx gives us a possibility to create a navigation that is not only about screens
but "viewport" it can
be what you can imagine, for example spinning the carousel.
Nevertheless, we will focus on traditional `Stack` navigation. There are some basic blocks that we
need to use. First
one is `Node` which is a representation of the structure (in our case the screen). Each node can
hold other notes, and
they are called ***children***. The ***node*** is standalone unit with own:

- [AppyxComponent](https://bumble-tech.github.io/appyx/components/) - in our case it will be
  ***back stack***, with
  simple linear navigation. Element at front is consider active, other as stashed. It can never be
  empty. It has some
  basic functions helper functions as ***push***, ***pop***, ***replace*** and default back handler.
- [Lifecycle](https://bumble-tech.github.io/appyx/navigation/features/lifecycle/) - it's a
  multiplatform interfaces that
  notifies the component about the state changes on the platform. For example on he Android platform
  it is implemented
  with ***AndroidLifecycle***.
- State Restoration after orientation changes
- The View, that is created with `@composable`
- Business logic
- Plugins - since ***Nodes*** should be kept lean, the plugins are used to add additional
  functionality, for
  example `NodeLifecycleAware` that allows to listen to the lifecycle events.

The ***nodes*** can be as small as you want to keep the complexity of your logic low and
encapsulated end extracted to
the **children*** to compose the process. With the nodes your navigation can work as a tree with
multiple branches
responsible for different processes. Some parts of the tree are active - visible on the screen,
other are stashed. To
change what's currently active we will use the component, the change will look like navigation. By
the adding or
removing nodes of node. Such approach creates
a [Scoped DI](https://bumble-tech.github.io/appyx/navigation/features/scoped-di/) the situation
where if the parent node is destroyed all of its children nodes and related objects are released.
There is
also [ChildAwareAPI](https://bumble-tech.github.io/appyx/navigation/features/childaware/) that helps
with
communication between parent and dynamically added child.

After that short introduction it's time to code. First thing that we need to create is
***RootNode***.

```kotlin
class RootNode(
    nodeContext: NodeContext,
) : LeafNode(nodeContext) {

    @Composable
    override fun Content(modifier: Modifier) {
        Column(
            modifier = Modifier.fillMaxSize().then(modifier),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Hello Appyx!")
        }
    }
}
```

Similarly, as in the [Decompose lib](https://github.com/mkonkel/DecomposeNavigation) we need to
provide some kind of
context. The `NodeContext` is created on the host platform (ex. Androids MainActivity) and it is
passed down to all the
descendants.
It's ensures the support of lifecycle and keeps the structured hierarchy od children nodes.
The `LeafNode` uses the
context and handle all the lifecycle events, manages plugins, provides the coroutines scope and
manage the children
creation to keeps the structured nodes hierarchy mentioned in ***scoped DI***. It also forces us to
implement a
***@Composable*** function `Content` that will be used to create the view.

Let's connect the ***RootNode*** with the hosts. For Android we need to use ***MainActivity*** and
inherit from
the `NodeComponentActivity()` which under the hood integrates the android with Appyx (if you don't
want to inherit from ready solutions you can implement ActivityIntegrationPoint by yourself) . Then
we need to create the `NodeHost` (that
is responsible for providing ***nodeContext***) and provide it with ***lifecycle***.

```kotlin
class MainActivity : NodeComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                NodeHost(
                    lifecycle = AndroidLifecycle(LocalLifecycleOwner.current.lifecycle),
                    integrationPoint = appyxIntegrationPoint
                ) { nodeContext ->
                    RootNode(nodeContext)
                }
            }
        }
    }
}
```

Now the iOS. As it is a compose function we just need to create proper host the `IosNodeHost` with
default `IntegrationPoint`.

```kotlin
fun MainViewController() = ComposeUIViewController {
    IosNodeHost(
        modifier = Modifier,
        integrationPoint = MainIntegrationPoint(),
        onBackPressedEvents = backEvents.receiveAsFlow()
    ) { nodeContext ->
        RootNode(nodeContext)
    }
}
```

The basic setup was done, we were able to display initial screen with the text. Now we can add children screens. The
screen definition need to be defined as `Parcelable` it will tell the node where we ant to go. The `Parcelable` is a
part of Appyx library and it's expect/actual class so it has a common definition that is implemented differently on
platforms. For android platform we need to add the support
od [kotlin-parcelize](https://developer.android.com/kotlin/parcelize) plugin to use `@Parcelize` annotation.

```kotlin
plugins {
    ...
    id("kotlin-parcelize")
}
```

```kotlin
sealed class NavTarget : Parcelable {
    @Parcelize
    data object FirstScreen : NavTarget()

    @Parcelize
    data object SecondScreen : NavTarget()
}
```

Targets are defined, the ***RootNode*** needs to be modified. It should handle the `BackStack` component, and know how
to navigate from one screen to another.
The first change that we need to do is to change the ***RootNode*** from being just a `LeafNode` to be a `Node<>`. First
one was a simple node that can't have children and was used to display the content. The second one is a node that can
have children and can be used to navigate between them.

The ***Node*** requires the `NavTarget` to be defined
and `buildChildNode` function to be implemented - those two things will be responsible for handling creation of the
children nodes.

Second important thing is the `appyxComponent = ` parameter that is used to define the way how the nodes will be
handled.

```kotlin
private fun backstack(nodeContext: NodeContext): BackStack<NavTarget> = BackStack(
    model = BackStackModel(
        initialTarget = NavTarget.FirstScreen,
        savedStateMap = nodeContext.savedStateMap,
    ),
    visualisation = { BackStackFader(it) }
)
```

Now we need to add some destination nodes, they would be same with different texts only.

```kotlin
class FirstNode(
    nodeContext: NodeContext
) : LeafNode(nodeContext = nodeContext) {
    @Composable
    override fun Content(modifier: Modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Hello from the First Node!")
        }
    }
}
```

To be able to navigate between the screens we will ad simple lambdas, that will be called in child nodes, but handled in
root node.

```kotlin
class FirstNode(
    private val onButtonClick: () -> Unit
) : LeafNode(nodeContext = nodeContext) {
    @Composable
    override fun Content(modifier: Modifier) {
        ...
        TextButton(onClick = onButtonClick) {
            Text("Go to Second Node")
        }
    }
}
```

Going back to the ***RoodNode***

```kotlin
class RootNode(
    nodeContext: NodeContext,
    private val backstack: BackStack<NavTarget> = backstack(nodeContext),
) : Node<NavTarget>(
    appyxComponent = backstack,
    nodeContext = nodeContext,
) {
    override fun buildChildNode(navTarget: NavTarget, nodeContext: NodeContext): Node<*> =
        when (navTarget) {
            NavTarget.FirstScreen -> FirstNode(nodeContext) {
                backstack.push(NavTarget.SecondScreen)
            }
            NavTarget.SecondScreen -> SecondNode(nodeContext) {
                backstack.push(NavTarget.FirstScreen)
            }
        }
}
```

We need to
add [AppyxNavigationContainer](https://bumble-tech.github.io/appyx/interactions/usage/?h=appyxnavigationcontainer#in-the-scope-of-appyx-navigation)
that will handle and the navigation and render the added nodes content.

```kotlin
@Composable
override fun Content(modifier: Modifier) {
    AppyxNavigationContainer(appyxComponent = backstack)
}
```

```kotlin
        commonMain.dependencies {
    ...
    api(libs.appyx.components.spotlingh)
}
```

![AppyxNavigation](/blog/images/1_navigation.gif "Basic Navogation with Appyx")

You can experiment with different visualisations, for example `BackStackFader`, `BackStackSlider`, `BackStackParallax`or
other mentioned in the [documentation](https://bumble-tech.github.io/appyx/components/backstack/)

### Tab Navigation

To handle bottom navigation feature we need to use
a [Spotlight](https://bumble-tech.github.io/appyx/components/spotlight/) component, which behaves similar to the view
pager.
It can hold multiple nodes at the same, and keeps one of them active (visible). The rule is same as with linear
navigation, we just need to switch form ***backstack*** to ***spotlight***.

```toml
appyx-components-spotlingh = { module = "com.bumble.appyx:spotlight", version.ref = "appyx" }
```

Lets create new navigation targets for tabbed navigation.

```kotlin
sealed class SpotlightNavTarget : Parcelable {
    @Parcelize
    data object ThirdScreen : SpotlightNavTarget()

    @Parcelize
    data object FourthScreen : SpotlightNavTarget()
}
```

Now we need to create a parent node that will hold the spotlight component.

```kotlin
class SpotlightNode(
    nodeContext: NodeContext,
    private val model: SpotlightModel<SpotlightNavTarget> = spotlightModel(nodeContext),
    private val spotlight: Spotlight<SpotlightNavTarget> = Spotlight(
        model = model,
        visualisation = { SpotlightSlider(it, model.currentState) }
    ),
) : Node<SpotlightNavTarget>(
    appyxComponent = spotlight,
    nodeContext = nodeContext,
)
```

We have to provide `SpotlightModel` and `SpotlightVisualisation` one will handle navigation, other the animation.
The model takes list of elements available to be displayed in the carousel, and initial index of default the active tab.

```kotlin
private fun spotlightModel(nodeContext: NodeContext) = SpotlightModel(
    items = listOf(SpotlightNavTarget.ThirdScreen, SpotlightNavTarget.FourthScreen),
    initialActiveIndex = 0f,
    savedStateMap = nodeContext.savedStateMap
)
```

Last thing to do is to create a UI representation of the screen, we will use `Scaffold` and default buttons

```kotlin
@Composable
override fun Content(modifier: Modifier) {
    Scaffold(
        bottomBar = {
            Row(Modifier.background(Color.White)) {
                TextButton(modifier = Modifier.weight(1f), onClick = { spotlight.first() }) {
                    Text(text = "Third")
                }
                TextButton(modifier = Modifier.weight(1f), onClick = { spotlight.last() }) {
                    Text(text = "Fourth")
                }
            }
        }
    ) { paddings ->
        AppyxNavigationContainer(modifier = Modifier.padding(paddings), appyxComponent = spotlight)
    }
}
```

The nodes will be exactly the same but with a different text and a background.

```kotlin
class ThirdNode(
    nodeContext: NodeContext,
) : LeafNode(nodeContext = nodeContext) {
    @Composable
    override fun Content(modifier: Modifier) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color.Magenta),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Hello from the Third Node!")
        }
    }
}
```

The solution is ready to use, as it was designed to work jus out of the box we need to add only the entrypoint to our
existing navigation.
We need to add `TabScreen` as a ***NavTarget*** in the linear navigation, and a button in `FirstNode` that will run the
***tabbed navigation*** feature.

![Tab Navigation with Spotlight](/blog/images/2_spotlight_tab_navigation.gif "Tab Navigation with Spotlight")

Appyx also provides a [Material3](https://m3.material.io/) support with out of the box solution for bottom navigation,
so we can easily create it using built in components.
The material support library uses the (spotlight)[https://bumble-tech.github.io/appyx/components/spotlight/] component
under the hood, so the dependencies we need are:

```toml
appyx-utils-material = { module = "com.bumble.appyx:utils-material3", version.ref = "appyx" }
```

```kotlin
        commonMain.dependencies {
    ...
    api(libs.appyx.utils.material)
}
```

After syncing the project we will reach the `AppyxNavItem` which will be used in a bottom navigation and
the `AppyxMaterial3NavNode` responsible for navigation.
Creation of the ***TabNavigationItems*** is pretty straightforward and similar to the previously used linear navigation.
We need to create **destinations** in our case these will be the enums that will represent the screens/nodes.

```kotlin
@Parcelize
enum class TabNavigationItems : Parcelable {
    FIRST_DESTINATION, SECOND_DESTINATION;
}
```

Now we can create the ***resolver*** that will be responsible for creating the bottom bar. It takes the defined
***destination*** and creates the proper navigation items, with ***text***, ***icons*** and lambda that will create
desired ***nodes***.

```kotlin
companion object {
    val resolver: (TabNavigationItems) -> AppyxNavItem = { navBarItem ->
        when (navBarItem) {
            FIRST_DESTINATION -> AppyxNavItem(
                text = "Third",
                unselectedIcon = Icons.Sharp.Home,
                selectedIcon = Icons.Filled.Home,
                node = { ThirdNode(it) }
            )

            SECOND_DESTINATION -> AppyxNavItem(
                text = "Fourth",
                unselectedIcon = Icons.Sharp.AccountBox,
                selectedIcon = Icons.Filled.AccountBox,
                node = { FourthNode(it) }
            )
        }
    }
}
```

The last thing to do is to create proper node.

```kotlin
class TabNode(
    nodeContext: NodeContext,
) : AppyxMaterial3NavNode<TabNavigationItems>(
    nodeContext = nodeContext,
    navTargets = TabNavigationItems.entries,
    navTargetResolver = TabNavigationItems.resolver,
    initialActiveElement = TabNavigationItems.FIRST_DESTINATION,
)
```

At the end we need to add another entrypoint in the ***FirstNode*** to be able to react freshly created screen.

![Tab Navigation with Material](/blog/images/3_material_tab_navigation.gif "Tab Navigation with Material")

### Coroutines

There is no official approach how to handle coroutines inside the ***Nodes***, but we can use for example the
***NodeLifecycle*** which provides the `lifecycle` and `lifecycleScope` and use it (as in the example).
We can also use the `lifecycle` and add an `observer`with `PlatformLifecycleEventObserver` and create and manage the
coroutineScope. We can also use the scope ot the @Composable view `rememberCoroutineScope()` or we can mix approaches to
fit all your needs.

Moving further I will extend the ***SecondNode*** with a countdown timer that will be started on the screen creation and
update the text value.

```kotlin
    private val countDownText = mutableStateOf<String>("0")

init {
    lifecycle.coroutineScope.launch {
        for (i in 10 downTo 0) {
            countDownText.value = i.toString()
            delay(1000)
        }
    }
}
```

```kotlin
    @Composable
override fun Content(modifier: Modifier) {
    Column(...) {
        ...
        Spacer(modifier = Modifier.height(16.dp))
        Text("Countdown: ${countDownText.value}")
    }
}
```

You should think about a better place to hold your business logic than `Node` a place that can handle the configuration
changes and recreating of the view, a place that can retain the state. The `ViewModel` is a perfect place for that, but
it's not a part of the Appyx library, so you need to implement it by yourself.
Appyx is currently in the development phase of ***viewmodel*** support, and you can vote for
ith [here](https://bumble-tech.github.io/appyx/navigation/integrations/viewmodel/?h=retain#alternative-retainedinstancestore).
If you would like to survive configuration changes there is an
official [guide](https://bumble-tech.github.io/appyx/navigation/features/surviving-configuration-changes/) for that.

![Coroutines](/blog/images/4_coroutines_support.gif "Coroutines")

### Summary

The `Appyx` library is a powerful tool that allows you to create a fully customized navigation in your Compose
Multiplatform application.
It's a great solution for creating complex navigation structures, and it's easy to use. The library is tightly coupled
with te Jetpack Compose but doesn't provide dedicated component to hold your business logic, you are free to use your
own solutions.
The library is still in the development phase waiting for example for the `ViewModel` support as mentioned in the post.
Therefore, it doesn't provide an out-of-the-box support for coroutines. You need to handle it by yourself, but it's not
a big deal.

Comparing to [Decompose](https://github.com/mkonkel/DecomposeNavigation) it was quicker to set up the basic navigation,
but it needs to use third-party for holding logic and develop whole app.
From the other side comparing to [Voyager](https://github.com/mkonkel/VoyagerNavigation) I can find a lot of
similarities on how it was designed to use, and how handles the navigation. Nevertheless, the ***Voyager*** was a bit
more intuitive for me, and I liked the way how it was designed to use.
All in all every library has its pros and cons, and it's up to you to choose the best one for your project. I think you
should try them all and decide which one fits your needs the best.

I hope this post has given you a good overview of the `Appyx` library and how you can use it to create a navigation in
your Compose Multiplatform application. If you have any questions or comments, please feel free to leave them below. I'd
love to hear from you!
