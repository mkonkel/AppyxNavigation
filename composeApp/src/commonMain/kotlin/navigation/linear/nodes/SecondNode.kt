package navigation.linear.nodes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SecondNode(
    nodeContext: NodeContext,
    private val onButtonClick: () -> Unit
) : LeafNode(nodeContext = nodeContext) {
    private val countDownText = mutableStateOf<String>("0")

    init {
        lifecycle.coroutineScope.launch {
            for (i in 10 downTo 0) {
                countDownText.value = i.toString()
                delay(1000)
            }
        }
    }

    @Composable
    override fun Content(modifier: Modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Hello from the Second Node!")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onButtonClick) {
                Text("Go to First Node")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Countdown: ${countDownText.value}")
        }
    }
}