package navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.node

class RootNode(
    nodeContext: NodeContext,
    private val backstack: BackStack<NavTarget> = backstack(nodeContext),
) : Node<NavTarget>(
    appyxComponent = backstack,
    nodeContext = nodeContext,
) {
    override fun buildChildNode(navTarget: NavTarget, nodeContext: NodeContext): Node<*> = when (navTarget) {
        NavTarget.FirstScreen -> node(nodeContext) { Text("First Screen") }
        NavTarget.SecondScreen -> node(nodeContext) { Text("Second Screen") }
    }

    @Composable
    override fun Content(modifier: Modifier) {
        Column {
            AppyxNavigationContainer(
                modifier = Modifier.weight(1f),
                appyxComponent = backstack
            )
            Row {
                Button(onClick = { backstack.push(NavTarget.FirstScreen) }) { Text("First Screen") }
                Button(onClick = { backstack.push(NavTarget.SecondScreen) }) { Text("Second Screen") }
            }
        }
    }
}

private fun backstack(nodeContext: NodeContext): BackStack<NavTarget> = BackStack(
    model = BackStackModel(
        initialTarget = NavTarget.FirstScreen,
        savedStateMap = nodeContext.savedStateMap,
    ),
    visualisation = { BackStackFader(it) }
)