package navigation.tabbed.nodes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode

class FourthNode(
    nodeContext: NodeContext,
) : LeafNode(nodeContext = nodeContext) {
    @Composable
    override fun Content(modifier: Modifier) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color.Cyan),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Hello from the Fourth Node!")
        }
    }
}