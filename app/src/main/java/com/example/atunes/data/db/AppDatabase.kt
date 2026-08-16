package com.example.atunes.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.atunes.data.model.Playlist
import com.example.atunes.data.model.PlaylistTrack
import com.example.atunes.data.model.Track

@Database(
    entities = [Track::class, Playlist::class, PlaylistTrack::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vinyl_red.db"
                ).build().also { INSTANCE = it }
            }
    }
}
