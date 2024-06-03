package ru.stersh.youamp.feature.playlist.ui

import ru.stersh.youamp.feature.playlist.domain.PlaylistInfo
import ru.stersh.youamp.feature.playlist.domain.PlaylistSong

internal fun PlaylistInfo.toUi(): PlaylistInfoUi {
    return PlaylistInfoUi(
        artworkUrl = artworkUrl,
        title = title,
        songs = songs.toUi()
    )
}

internal fun List<PlaylistSong>.toUi(): List<PlaylistSongUi> = map { it.toUi() }

internal fun PlaylistSong.toUi(): PlaylistSongUi {
    return PlaylistSongUi(
        id = id,
        title = title,
        artist = artist,
        artworkUrl = artworkUrl,
        isCurrent = isCurrent,
        isPlaying = isPlaying
    )
}