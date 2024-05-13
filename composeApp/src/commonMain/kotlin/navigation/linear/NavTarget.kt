package navigation.linear

import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

sealed class NavTarget : Parcelable {
    @Parcelize
    data object FirstScreen : NavTarget()

    @Parcelize
    data object SecondScreen : NavTarget()

    @Parcelize
    data object SpotlightTabScreen : NavTarget()

    @Parcelize
    data object MaterialTabScreen : NavTarget()
}