import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.bumble.appyx.navigation.integration.IosNodeHost
import com.bumble.appyx.navigation.integration.MainIntegrationPoint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import navigation.RootNode

val backEvents: Channel<Unit> = Channel()
fun MainViewController() = ComposeUIViewController {
    IosNodeHost(
        modifier = Modifier,
        integrationPoint = MainIntegrationPoint(),
        onBackPressedEvents = backEvents.receiveAsFlow()
    ) { nodeContext ->
        RootNode(nodeContext)
    }
}