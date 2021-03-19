package info.espeleta.flutter_tvprovider

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.util.Rational
import androidx.annotation.RequiresApi
import androidx.tvprovider.media.tv.*
import info.espeleta.flutter_tvprovider.model.TvChannel
import info.espeleta.flutter_tvprovider.model.TvProgram
import io.flutter.FlutterInjector

@RequiresApi(26)
@SuppressLint("RestrictedApi")
class FlutterTvProviderUtils private constructor() {
    
    companion object {
        private val TAG = FlutterTvProviderUtils::class.java.simpleName

        /**
         * Parse an aspect ratio constant into the equivalent rational number. For example,
         * [TvContractCompat.PreviewPrograms.ASPECT_RATIO_16_9] becomes `Rational(16, 9)`. The
         * constant must be one of ASPECT_RATIO_* in [TvContractCompat.PreviewPrograms].
         */
        fun parseAspectRatio(ratioConstant: Int): Rational = when(ratioConstant) {
            TvContractCompat.PreviewPrograms.ASPECT_RATIO_16_9 -> Rational(16, 9)
            TvContractCompat.PreviewPrograms.ASPECT_RATIO_1_1 -> Rational(1, 1)
            TvContractCompat.PreviewPrograms.ASPECT_RATIO_2_3 -> Rational(2, 3)
            TvContractCompat.PreviewPrograms.ASPECT_RATIO_3_2 -> Rational(3, 2)
            TvContractCompat.PreviewPrograms.ASPECT_RATIO_4_3 -> Rational(4, 3)
            TvContractCompat.PreviewPrograms.ASPECT_RATIO_MOVIE_POSTER -> Rational(1000, 1441)
            else -> throw IllegalArgumentException(
                "Constant must be one of ASPECT_RATIO_* in TvContractCompat.PreviewPrograms")
        }

        /**
         * Retrieve the preview programs associated with the given channel ID or, if ID is null,
         * return all programs associated with any channel.
         */
        fun getTvPrograms(context: Context, channelId: String? = null): List<TvProgram> {
            // Now find the channel with the matching content ID for our collection
            val allChannels = PreviewChannelHelper(context).allChannels
            val foundChannel = allChannels.find { it.internalProviderId == channelId }
            if (foundChannel == null) {
                Log.e(TAG, "No channel with ID ${channelId}")
                return listOf()
            }
            return getPreviewPrograms(context, foundChannel.id).map { TvProgram.fromPreviewProgram(it) }.toList()
        }

        /**
         * Retrieve all programs in watch next row that are ours
         */
        fun getWatchNextTvPrograms(context: Context): List<TvProgram> {
            return getWatchNextPrograms(context).map { TvProgram.fromWatchNextProgram(it) }.toList()
        }

        /**
         * Retrieve all channels (without logos)
         */
        fun getTvChannels(context: Context): List<TvChannel> {
            // Retrieve a list of all previously created channels
            val allChannels: List<PreviewChannel> = try {
                PreviewChannelHelper(context).allChannels
            } catch (exc: IllegalArgumentException) {
                listOf()
            }
            return allChannels.map { TvChannel.fromPreviewChannel(it) }.toList()
        }


        /**
         * Remove a program given a program identifier
         */
        @Synchronized
        fun removeTvProgram(context: Context, programId: String?): Boolean {
            Log.d(TAG, "Removing content from watch next: $programId")

            // First, get all the programs from all of our channels
            val allPrograms = getPreviewPrograms(context)

            // Now find the program with the matching content ID for our metadata
            // Note that we are only getting the first match, this is because in our data models
            // we only allow for a program to be in one "collection" (aka channel) but nothing
            // prevents more than one program added to the content provider sharing content ID by
            // adding the same program to multiple channels
            val foundProgram = allPrograms.find { it.contentId == programId }
            if (foundProgram == null) Log.e(TAG, "No program found with content ID ${programId}")

            // Use the found program's URI to delete it from the content resolver
            return foundProgram?.id?.let {
                PreviewChannelHelper(context).deletePreviewProgram(it)
                Log.d(TAG, "Program successfully removed from home screen")
                true
            } ?: false
        }

        /**
         * Insert or update a channel given a [TvChannel] object. Setting the argument
         * [clearPrograms] to true makes sure that the channel end up with only the items in
         * the [programs] argument.
         */
        @Synchronized
        fun upsertChannel(
                context: Context,
                channel: TvChannel,
                programs: List<TvProgram>,
                clearPrograms: Boolean = false
        ): Boolean {
            // Retrieve a list of all previously created channels
            val allChannels: List<PreviewChannel> = try {
                PreviewChannelHelper(context).allChannels
            } catch (exc: IllegalArgumentException) {
                listOf()
            }

            // If we already have a channel with this ID, use it as a base for updated channel
            // This is the only way to set the internal ID field when using [PreviewChannel.Builder]
            // NOTE: This means that any fields not set in the updated channel that were set in the
            //  existing channel will be preserved
            val existingChannel = allChannels.find { it.internalProviderId == channel.internalProviderId }
            val channelBuilder = if (existingChannel == null) {
                PreviewChannel.Builder()
            } else {
                PreviewChannel.Builder(existingChannel)
            }
            val updatedChannel = createChannel(context, channelBuilder, channel)

            // Check if a channel with a matching ID already exists
            val channelId = if (existingChannel != null) {

                // Delete all the programs from existing channel if requested
                // NOTE: This is in general a bad practice, since there could be metadata loss such
                //  as playback position, interaction count, etc.
                if (clearPrograms) {
                    // To delete all programs from a channel, we can build a special URI that
                    // references all programs associated with a channel and delete just that one
                    val channelPrograms = TvContractCompat.buildPreviewProgramsUriForChannel(existingChannel.id)
                    context.contentResolver.delete(channelPrograms, null, null)
                }

                // Update channel in the system's content provider
                PreviewChannelHelper(context).updatePreviewChannel(existingChannel.id, updatedChannel)
                Log.d(TAG, "Updated channel ${existingChannel.id}")

                // Return the existing channel's ID
                existingChannel.id

            } else {

                // Insert channel, return null if URI in content provider is null
                try {
                    val channelId = PreviewChannelHelper(context).publishChannel(updatedChannel)
                    Log.d(TAG, "Published channel $channelId")
                    channelId

                } catch (exc: Throwable) {
                    // Early exit: return null if we couldn't insert the channel
                    Log.e(TAG, "Unable to publish channel", exc)
                    return false
                }
            }

            // If it's the first channel being shown, make it the app's default channel
            if (allChannels.none { it.isBrowsable }) {
                TvContractCompat.requestChannelBrowsable(context, channelId)
            }

            // Retrieve programs already added to this channel, if any
            val existingProgramList = getPreviewPrograms(context, channelId)

            // Create a list of programs from the content URIs, adding as much metadata as
            // possible for each piece of content. The more metadata added, the better the
            // content will be presented in the user's home screen.
            programs.forEach { tvProgram ->

                // If we already have a program with this ID, use it as a base for updated program
                // This is the only way to set the internal ID field when using the builder
                // NOTE: This means that any fields not set in the updated program that were set in
                //  the existing program will be preserved
                val existingProgram = existingProgramList.find { it.contentId == tvProgram.contentId }
                val programBuilder = if (existingProgram == null) {
                    PreviewProgram.Builder()
                } else {
                    PreviewProgram.Builder(existingProgram)
                }

                // Copy all metadata into our program builder
                val updatedProgram = programBuilder.also { tvProgram.toBuilder(it) }
                    // Set the same channel ID in all programs
                    .setChannelId(channelId)
                    // This must match the desired intent filter in the manifest for VIEW action
                    .setIntentUri(Uri.parse(tvProgram.intentUri))
                    // Build the program at once
                    .build()

                // Insert new program into the channel or update if another one with same ID exists
                try {
                    if (existingProgram == null) {
                        PreviewChannelHelper(context).publishPreviewProgram(updatedProgram)
                        Log.d(TAG, "Inserted program into channel: $updatedProgram")
                    } else {
                        PreviewChannelHelper(context).updatePreviewProgram(existingProgram.id, updatedProgram)
                        Log.d(TAG, "Updated program in channel: $updatedProgram")
                    }
                } catch (exc: IllegalArgumentException) {
                    Log.e(TAG, "Unable to add program: $updatedProgram", exc)
                }
            }
            // Return OK
            return true
        }

        /**
         * Remove a [TvChannel] object from the channel list
         */
        @Synchronized
        fun removeTvChannel(context: Context, channelId: String?): Boolean {
            Log.d(TAG, "Removing channel from home screen: $channelId")
            
            // First, get all the channels added to the home screen
            val allChannels = PreviewChannelHelper(context).allChannels

            // Now find the channel with the matching content ID for our collection
            val foundChannel = allChannels.find { it.internalProviderId == channelId }
            if (foundChannel == null) Log.e(TAG, "No channel with ID ${channelId}")

            // Use the found channel's ID to delete it from the content resolver
            return foundChannel?.let {
                PreviewChannelHelper(context).deletePreviewChannel(it.id)
                Log.d(TAG, "Channel successfully removed from home screen")

                // Remove all of the channel programs as well
                val channelPrograms = TvContractCompat.buildPreviewProgramsUriForChannel(it.id)
                context.contentResolver.delete(channelPrograms, null, null)

                // Return the ID of the channel removed
                true
            } ?: false
        }

        /**
         * Insert or update a [TvProgram] into the watch next row
         */
        @Synchronized
        fun upsertWatchNext(context: Context, program: TvProgram): Boolean {
            Log.d(TAG, "Adding program to watch next row: $program")

            // If we already have a program with this ID, use it as a base for updated program
            val existingProgram = getWatchNextPrograms(context).find { it.contentId == program.internalProviderId }
            val programBuilder = if (existingProgram == null) {
                WatchNextProgram.Builder()
            } else {
                WatchNextProgram.Builder(existingProgram)
            }.also { program.toBuilder(it) }

            // Required for CONTINUE program type: set last engagement time
            programBuilder.setLastEngagementTimeUtcMillis(System.currentTimeMillis())

            // Set the specific intent for this program in the watch next row
            // Watch next type is inferred from media playback using the following rules:
            // 1. NEXT      - If position is NULL
            // 2. CONTINUE  - If position not NULL AND duration is not NULL
            // 3. UNKNOWN   - If position < 0 OR (position is not NULL AND duration is NULL)
            programBuilder.setWatchNextType(program.lastPlaybackPositionMillis?.let { position ->
                if (position > 0 && program.lastPlaybackPositionMillis?.let { it > 0 } == true) {
                    Log.d(TAG, "Inferred watch next type: CONTINUE")
                    TvContractCompat.WatchNextPrograms.WATCH_NEXT_TYPE_CONTINUE
                } else {
                    Log.d(TAG, "Inferred watch next type: UNKNOWN")
                    WatchNextProgram.WATCH_NEXT_TYPE_UNKNOWN
                }
            } ?: TvContractCompat.WatchNextPrograms.WATCH_NEXT_TYPE_NEXT)

            // This must match the desired intent filter in the manifest for VIEW intent action
            programBuilder.setIntentUri(Uri.parse(program.intentUri))

            // Build the program with all the metadata
            val updatedProgram = programBuilder.build()

            // Check if a program matching this one already exists in the watch next row
            return if (existingProgram != null) {
                // If the program is already in the watch next row, update it
                PreviewChannelHelper(context).updateWatchNextProgram(updatedProgram, existingProgram.id)
                Log.d(TAG, "Updated program in watch next row: $updatedProgram")
                true
            } else {
                // Otherwise build the program and insert it into the channel
                try {
                    val programId = PreviewChannelHelper(context).publishWatchNextProgram(updatedProgram)
                    Log.d(TAG, "Added program to watch next row: $updatedProgram")
                    true
                } catch (exc: IllegalArgumentException) {
                    Log.e(TAG, "Unable to add program to watch next row")
                    false
                }
            }
        }

        /**
         * Remove a [TvProgram] object from the watch next row
         */
        @Synchronized
        fun removeFromWatchNext(context: Context, programId: String?): Boolean {
            Log.d(TAG, "Removing program from watch next: $programId")

            // First, get all the programs in the watch next row
            val allPrograms = getWatchNextPrograms(context)

            // Now find the program with the matching content ID for our metadata
            val foundProgram = allPrograms.find { it.contentId == programId }
            if (foundProgram == null)
                Log.e(TAG, "No program found in Watch Next with content ID ${programId}")

            // Use the found program's URI to delete it from the content resolver
            return foundProgram?.let {
                val programUri = TvContractCompat.buildWatchNextProgramUri(it.id)
                val deleteCount = context.contentResolver.delete(programUri, null, null)
                if (deleteCount == 1) {
                    Log.d(TAG, "Content successfully removed from watch next")
                    true

                } else {
                    Log.e(TAG, "Content failed to be removed from watch next " +
                        "(delete count $deleteCount)")
                    false
                }
            } ?: false
        }

        // PRIVATE
        private fun createChannel(context: Context, channelBuilder: PreviewChannel.Builder, channel: TvChannel): PreviewChannel {
            // Build an updated channel object and add our metadata
            val uriString = channel.logoUri ?: channel.defaultLogoUri
            val logoUri = Uri.parse(uriString)
            if (logoUri.scheme != null && logoUri.scheme!!.startsWith("http")) {
                return channelBuilder
                        .setInternalProviderId(channel.internalProviderId)
                        .setLogo(logoUri)
                        .setAppLinkIntentUri(if (channel.appLinkIntentUri != null) Uri.parse(channel.appLinkIntentUri) else null)
                        .setDisplayName(channel.displayName ?: "")
                        .setDescription(channel.description ?: "")
                        .build()
            } else {
                return channelBuilder
                        .setInternalProviderId(channel.internalProviderId)
                        .setLogo(loadLogo(context, uriString))
                        .setAppLinkIntentUri(if (channel.appLinkIntentUri != null) Uri.parse(channel.appLinkIntentUri) else null)
                        .setDisplayName(channel.displayName ?: "")
                        .setDescription(channel.description ?: "")
                        .build()
            }
        }

        private fun loadLogo(context: Context, logoPath: String): Bitmap {
            val loader = FlutterInjector.instance().flutterLoader()
            val key = loader.getLookupKeyForAsset(logoPath)
            val fd = context.assets.openFd(key)
            return BitmapFactory.decodeStream(fd.createInputStream())
        }

        private fun getPreviewPrograms(context: Context, channelId: Long? = null): List<PreviewProgram> {
            val programs: MutableList<PreviewProgram> = mutableListOf()
            try {
                val cursor = context.contentResolver.query(
                        TvContractCompat.PreviewPrograms.CONTENT_URI,
                        PreviewProgram.PROJECTION,
                        null,
                        null,
                        null)
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        val program = PreviewProgram.fromCursor(cursor)
                        if (channelId == null || channelId == program.channelId) {
                            programs.add(program)
                        }
                    } while (cursor.moveToNext())
                }
                cursor?.close()

            } catch (exc: IllegalArgumentException) {
                Log.e(TAG, "Error retrieving preview programs", exc)
            }
            return programs
        }

        private fun getWatchNextPrograms(context: Context): List<WatchNextProgram> {
            val programs: MutableList<WatchNextProgram> = mutableListOf()

            try {
                val cursor = context.contentResolver.query(
                        TvContractCompat.WatchNextPrograms.CONTENT_URI,
                        WatchNextProgram.PROJECTION,
                        null,
                        null,
                        null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        val watchNext = WatchNextProgram.fromCursor(cursor)
                        programs.add(watchNext)
                    } while (cursor.moveToNext())
                }
                cursor?.close()

            } catch (exc: IllegalArgumentException) {
                Log.e(TAG, "Error retrieving Watch Next programs", exc)
            }
            return programs
        }


    }
}