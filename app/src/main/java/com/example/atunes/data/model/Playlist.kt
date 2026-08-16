package com.example.atunes.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val coverUri: String = "",   // custom cover or empty → auto gradient
    val createdAt: Long = System.currentTimeMillis()
)
