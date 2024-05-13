package navigation.tabbed.spotlight

import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

sealed class SpotlightNavTarget : Parcelable {
    @Parcelize
    data object ThirdScreen : SpotlightNavTarget()

    @Parcelize
    data object FourthScreen : SpotlightNavTarget()
}