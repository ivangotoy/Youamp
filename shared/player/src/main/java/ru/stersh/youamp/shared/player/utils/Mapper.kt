package ru.stersh.youamp.shared.player.utils

import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.media3.common.HeartRating
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.StarRating
import ru.stresh.youamp.core.api.Song
import ru.stresh.youamp.core.api.provider.ApiProvider

internal suspend fun Song.toMediaItem(apiProvider: ApiProvider): MediaItem {
    val songUri = apiProvider
        .getApi()
        .downloadUrl(id)
        .toUri()

    val artworkUri = apiProvider
        .getApi()
        .getCoverArtUrl(coverArt)
        .toUri()

    val songRating = userRating
    val rating = if (songRating != null && songRating > 0) {
        StarRating(5, songRating.toFloat())
    } else {
        StarRating(5)
    }
    val starredRating = HeartRating(starred != null)

    val metadata = MediaMetadata
        .Builder()
        .setTitle(title)
        .setArtist(artist)
        .setExtras(
            bundleOf(
                MEDIA_ITEM_ALBUM_ID to albumId,
                MEDIA_ITEM_DURATION to duration * 1000L,
                MEDIA_SONG_ID to id,
            ),
        )
        .setOverallRating(rating)
        .setUserRating(starredRating)
        .setArtworkUri(artworkUri)
        .build()
    val requestMetadata = MediaItem
        .RequestMetadata
        .Builder()
        .setMediaUri(songUri)
        .build()
    return MediaItem
        .Builder()
        .setMediaId(id)
        .setMediaMetadata(metadata)
        .setRequestMetadata(requestMetadata)
        .setUri(songUri)
        .build()
}