package info.espeleta.flutter_tvprovider.model

import android.annotation.SuppressLint
import android.media.tv.TvContentRating
import android.net.Uri
import androidx.tvprovider.media.tv.BasePreviewProgram
import androidx.tvprovider.media.tv.PreviewProgram
import androidx.tvprovider.media.tv.WatchNextProgram
import kotlinx.serialization.Serializable

/**
 * Data class representing a piece of content metadata (title, content URI, state-related fields
 * [playback position], etc.)
 */
@SuppressLint("RestrictedApi")
@Serializable
data class TvProgram(
    var id: Long? = null,
    var channelId: Long? = null,
    var audioLanguages: List<String>? = null,
    var author: String? = null,
    var availability: Int? = null,
    var canonicalGenres: List<String>? = null,
    var contentId: String? = null,
    var description: String? = null,
    var durationMillis: Int? = null,
    var endTimeUtcMillis: Long? = null,
    var episodeNumber: Int? = null,
    var episodeTitle: String? = null,
    var genre: String? = null,
    var intentUri: String? = null,
    var interactionCount: Long? = null,
    var interactionType: Int? = null,
    var internalProviderId: String? = null,
    var internalProviderFlag1: Long? = null,
    var internalProviderFlag2: Long? = null,
    var internalProviderFlag3: Long? = null,
    var internalProviderFlag4: Long? = null,
    var itemCount: Int? = null,
    var lastEngagementTimeUtcMillis: Long? = null,
    var lastPlaybackPositionMillis: Int? = null,
    var live: Boolean? = null,
    var logoContentDescription: String? = null,
    var logoUri: String? = null,
    var longDescription: String? = null,
    var offerPrice: String? = null,
    var packageName: String? = null,
    var posterArtAspectRatio: Int? = null,
    var posterArtUri: String? = null,
    var previewAudioUri: String? = null,
    var previewVideoUri: String? = null,
    var projection: Boolean? = null,
    var releaseDate: String? = null,
    var reviewRating: String? = null,
    var reviewRatingStyle: Int? = null,
    var searchable: Boolean? = null,
    var seasonNumber: String? = null,
    var seasonTitle: String? = null,
    var startingPrice: String? = null,
    var startTimeUtcMillis: Long? = null,
    var thumbnailAspectRatio: Int? = null,
    var thumbnailUri: String? = null,
    var title: String? = null,
    var transient: Boolean? = null,
    var tvSeriesItemType: Int? = null,
    var type: Int? = null,
    var videoHeight: Int? = null,
    var videoWidth: Int? = null,
    var watchNextType: Int? = null,
    var weight: Int? = null
) {
    fun <T : BasePreviewProgram.Builder<*>>toBuilder(builder: T) {
        // Basic metadata
        builder.setContentId(contentId).setTitle(title)

        // Type
        type?.let { builder.setType(it) }

        // Author (director for movies, performer for songs)
        author?.let { builder.setAuthor(it) }

        // Blurb shown under the media title in the program card
        description?.let { builder.setDescription(it) }

        // Release date, possible formats are  "yyyy", "yyyy-MM-dd", and "yyyy-MM-ddTHH:mm:ssZ"
        releaseDate?.let { builder.setReleaseDate(it)}

        // Track / episode number
        episodeNumber?.let { builder.setEpisodeNumber(it) }

        // Duration of content, set in milliseconds
        durationMillis?.let { builder.setDurationMillis(it) }

        // Position of playback, set in milliseconds
        lastPlaybackPositionMillis?.let { builder.setLastPlaybackPositionMillis(it) }

        // Album / poster art, which will have a specific aspect ratio
        posterArtUri?.let {
            builder.setPosterArtUri(Uri.parse(it))
            posterArtAspectRatio?.let { ratio ->
                builder.setPosterArtAspectRatio(ratio)
            }
        }
    }

    companion object {
        fun fromPreviewProgram(program: PreviewProgram?): TvProgram {
            val tvprogram = fromBasePreviewProgram(program)
            if (program?.channelId != null) {
                tvprogram.channelId = program.channelId
            }
            if (program?.weight != null) {
                tvprogram.weight = program.weight
            }
            return tvprogram
        }

        fun fromWatchNextProgram(program: WatchNextProgram?): TvProgram {
            val tvprogram = fromBasePreviewProgram(program)
            if (program?.watchNextType != null) {
                tvprogram.watchNextType = program.watchNextType
            }
            if (program?.lastEngagementTimeUtcMillis != null) {
                tvprogram.lastEngagementTimeUtcMillis = program.lastEngagementTimeUtcMillis
            }
            return tvprogram
        }

        private fun fromBasePreviewProgram(program: BasePreviewProgram?): TvProgram {
            return TvProgram(
                id = program?.id,
                audioLanguages = program?.audioLanguages?.toList(),
                author = program?.author,
                availability = program?.availability,
                canonicalGenres = program?.canonicalGenres?.toList(),
                contentId = program?.contentId,
                description = program?.description,
                durationMillis = program?.durationMillis,
                endTimeUtcMillis = program?.endTimeUtcMillis,
                episodeNumber = program?.episodeNumber?.toIntOrNull(),
                episodeTitle = program?.episodeTitle,
                genre = program?.genre,
                intentUri = program?.intentUri?.toString(),
                interactionCount = program?.interactionCount,
                interactionType = program?.interactionType,
                internalProviderId = program?.internalProviderId,
                internalProviderFlag1 = program?.internalProviderFlag1,
                internalProviderFlag2 = program?.internalProviderFlag2,
                internalProviderFlag3 = program?.internalProviderFlag3,
                internalProviderFlag4 = program?.internalProviderFlag4,
                itemCount = program?.itemCount,
                lastPlaybackPositionMillis = program?.lastPlaybackPositionMillis,
                live = program?.isLive,
                logoContentDescription = program?.logoContentDescription,
                logoUri = program?.logoUri?.toString(),
                longDescription = program?.longDescription,
                offerPrice = program?.offerPrice,
                packageName = program?.packageName,
                posterArtAspectRatio = program?.posterArtAspectRatio,
                posterArtUri = program?.posterArtUri?.toString(),
                previewAudioUri = program?.previewAudioUri?.toString(),
                previewVideoUri = program?.previewVideoUri?.toString(),
                projection = null,
                releaseDate = program?.releaseDate,
                reviewRating = program?.reviewRating,
                reviewRatingStyle = program?.reviewRatingStyle,
                searchable = program?.isSearchable,
                seasonNumber = program?.seasonNumber,
                seasonTitle = program?.seasonTitle,
                startingPrice = program?.startingPrice,
                startTimeUtcMillis = program?.startTimeUtcMillis,
                thumbnailAspectRatio = program?.thumbnailAspectRatio,
                thumbnailUri = program?.thumbnailUri?.toString(),
                title = program?.title,
                transient = program?.isTransient,
                //tvSeriesItemType = program?.tvSeriesItemType,
                type = program?.type,
                videoHeight = program?.videoHeight,
                videoWidth = program?.videoWidth
            )
        }
    }
}
