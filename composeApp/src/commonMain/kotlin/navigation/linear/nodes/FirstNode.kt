package navigation.linear.nodes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode

class FirstNode(
    nodeContext: NodeContext,
    private val onButtonClick: () -> Unit,
    private val onTabbedNavClick: () -> Unit,
) : LeafNode(nodeContext = nodeContext) {
    @Composable
    override fun Content(modifier: Modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Hello from the First Node!")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onButtonClick) {
                Text("Go to Second Node")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onTabbedNavClick) {
                Text("Go to Tabbed Node")
            }
        }
    }
}