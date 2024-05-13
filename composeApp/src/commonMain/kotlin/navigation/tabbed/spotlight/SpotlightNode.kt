package navigation.tabbed.spotlight

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.operation.first
import com.bumble.appyx.components.spotlight.operation.last
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import navigation.tabbed.nodes.FourthNode
import navigation.tabbed.nodes.ThirdNode

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
) {

    override fun buildChildNode(navTarget: SpotlightNavTarget, nodeContext: NodeContext): Node<*> {
        return when (navTarget) {
            SpotlightNavTarget.ThirdScreen -> ThirdNode(nodeContext = nodeContext)
            SpotlightNavTarget.FourthScreen -> FourthNode(nodeContext = nodeContext)
        }
    }

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
}

private fun spotlightModel(nodeContext: NodeContext) = SpotlightModel(
    items = listOf(SpotlightNavTarget.ThirdScreen, SpotlightNavTarget.FourthScreen),
    initialActiveIndex = 0f,
    savedStateMap = nodeContext.savedStateMap
)
