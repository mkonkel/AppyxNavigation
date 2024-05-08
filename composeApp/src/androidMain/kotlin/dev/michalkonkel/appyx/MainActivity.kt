package dev.michalkonkel.appyx

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.bumble.appyx.navigation.integration.NodeActivity
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.platform.AndroidLifecycle
import navigation.RootNode

class MainActivity : NodeActivity() {
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
