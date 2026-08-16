package com.example.atunes.data.db

import androidx.room.*
import com.example.atunes.data.model.Playlist
import com.example.atunes.data.model.PlaylistTrack
import com.example.atunes.data.model.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist): Long

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackToPlaylist(playlistTrack: PlaylistTrack)

    @Delete
    suspend fun removeTrackFromPlaylist(playlistTrack: PlaylistTrack)

    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Query("""
        SELECT t.* FROM tracks t
        INNER JOIN playlist_tracks pt ON t.id = pt.trackId
        WHERE pt.playlistId = :playlistId
        ORDER BY pt.position ASC
    """)
    fun getPlaylistTracks(playlistId: Long): Flow<List<Track>>

    @Query("SELECT COUNT(*) FROM playlist_tracks WHERE playlistId = :playlistId")
    fun getPlaylistTrackCount(playlistId: Long): Flow<Int>
}
