package navigation.tabbed

import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.utils.material3.AppyxMaterial3NavNode

class TabNode(
    nodeContext: NodeContext,
) : AppyxMaterial3NavNode<TabNavigationItems>(
    nodeContext = nodeContext,
    navTargets = TabNavigationItems.entries,
    navTargetResolver = TabNavigationItems.resolver,
    initialActiveElement = TabNavigationItems.FIRST_DESTINATION,
)