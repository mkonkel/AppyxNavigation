package dev.michalkonkel.appyx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.bumble.appyx.navigation.integration.ActivityIntegrationPoint
import com.bumble.appyx.navigation.integration.IntegrationPointProvider
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.platform.AndroidLifecycle
import navigation.RootNode

class MainActivity : ComponentActivity(), IntegrationPointProvider {

    override lateinit var appyxIntegrationPoint: ActivityIntegrationPoint

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appyxIntegrationPoint = createIntegrationPoint(savedInstanceState)

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

    private fun createIntegrationPoint(savedInstanceState: Bundle?) =
        ActivityIntegrationPoint(
            activity = this,
            savedInstanceState = savedInstanceState
        )


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        appyxIntegrationPoint.onSaveInstanceState(outState)
    }
}
