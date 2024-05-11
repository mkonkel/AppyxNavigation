package navigation

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
    override fun buildChildNode(navTarget: NavTarget, nodeContext: NodeContext): Node<*> =
        when (navTarget) {
            NavTarget.FirstScreen -> FirstNode(nodeContext) {
                backstack.push(NavTarget.SecondScreen)
            }

            NavTarget.SecondScreen -> SecondNode(nodeContext) {
                backstack.push(NavTarget.FirstScreen)
            }
        }

    @Composable
    override fun Content(modifier: Modifier) {
        AppyxNavigationContainer(appyxComponent = backstack)
    }
}

private fun backstack(nodeContext: NodeContext): BackStack<NavTarget> = BackStack(
    model = BackStackModel(
        initialTarget = NavTarget.FirstScreen,
        savedStateMap = nodeContext.savedStateMap,
    ),
    visualisation = { BackStackFader(it) }
)