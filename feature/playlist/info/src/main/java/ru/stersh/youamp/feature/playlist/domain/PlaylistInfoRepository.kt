package ru.stersh.youamp.feature.playlist.domain

import kotlinx.coroutines.flow.Flow

internal interface PlaylistInfoRepository {
    fun getPlaylistInfo(playlistId: String): Flow<PlaylistInfo>
}