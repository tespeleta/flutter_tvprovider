package info.espeleta.flutter_tvprovider.model

import android.graphics.Bitmap
import androidx.tvprovider.media.tv.BasePreviewProgram
import androidx.tvprovider.media.tv.PreviewChannel
import androidx.tvprovider.media.tv.PreviewProgram
import kotlinx.serialization.Serializable

/**
 * Data class representing a channel of playable programs
 */
@Serializable
data class TvChannel(
    val id: Long? = null,
    val internalProviderId: String,
    val defaultLogoUri: String,
    val logoUri: String? = null,
    val type: String? = null,
    val displayName: String? = null,
    val description: String? = null,
    val appLinkIntentUri: String? = null,
) {
    companion object {
        fun fromPreviewChannel(channel: PreviewChannel?): TvChannel {
            val providerId = if (channel?.internalProviderId == null) "" else channel.internalProviderId
            return TvChannel(
                id = channel?.id,
                internalProviderId = providerId,
                defaultLogoUri = "",
                type = channel?.type,
                displayName = channel?.displayName?.toString(),
                description = channel?.description?.toString(),
                appLinkIntentUri = channel?.appLinkIntentUri?.toString()
            )
        }
    }
}
