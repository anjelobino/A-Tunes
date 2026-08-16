package com.example.atunes.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val duration: Long,          // milliseconds
    val contentUri: String,      // content:// URI string
    val dateAdded: Long,         // epoch seconds
    val genre: String = "",
    val trackNumber: Int = 0,
    val isLiked: Boolean = false
)
