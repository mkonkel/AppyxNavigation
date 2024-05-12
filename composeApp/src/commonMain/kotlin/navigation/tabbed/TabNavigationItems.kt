package navigation.tabbed

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.sharp.AccountBox
import androidx.compose.material.icons.sharp.Home
import com.bumble.appyx.utils.material3.AppyxNavItem
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import navigation.tabbed.nodes.FourthNode
import navigation.tabbed.nodes.ThirdNode

@Parcelize
enum class TabNavigationItems : Parcelable {
    FIRST_DESTINATION, SECOND_DESTINATION;

    companion object {
        val resolver: (TabNavigationItems) -> AppyxNavItem = { navBarItem ->
            when (navBarItem) {
                FIRST_DESTINATION -> AppyxNavItem(
                    text = "Third",
                    unselectedIcon = Icons.Sharp.Home,
                    selectedIcon = Icons.Filled.Home,
                    node = { ThirdNode(it) }
                )

                SECOND_DESTINATION -> AppyxNavItem(
                    text = "Fourth",
                    unselectedIcon = Icons.Sharp.AccountBox,
                    selectedIcon = Icons.Filled.AccountBox,
                    node = { FourthNode(it) }
                )
            }
        }
    }
}

