package com.example.atunes.data.model

import androidx.room.Entity

@Entity(tableName = "playlist_tracks", primaryKeys = ["playlistId", "trackId"])
data class PlaylistTrack(
    val playlistId: Long,
    val trackId: Long,
    val position: Int = 0
)
