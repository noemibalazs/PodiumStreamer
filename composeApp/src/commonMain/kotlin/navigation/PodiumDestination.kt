package navigation

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import podiumstreamer.composeapp.generated.resources.Res
import podiumstreamer.composeapp.generated.resources.favorites
import podiumstreamer.composeapp.generated.resources.label_favorites
import podiumstreamer.composeapp.generated.resources.label_streams
import podiumstreamer.composeapp.generated.resources.streams

enum class PodiumDestination(val titleResId: StringResource, val iconResId: DrawableResource) {

    STREAMS(titleResId = Res.string.label_streams, iconResId = Res.drawable.streams),
    FAVORITES(titleResId = Res.string.label_favorites, iconResId = Res.drawable.favorites);

    companion object {

        fun getDestinations(): List<PodiumDestination> = listOf(STREAMS, FAVORITES)
    }
}