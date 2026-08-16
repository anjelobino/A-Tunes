package com.example.atunes.data.db

import androidx.room.*
import com.example.atunes.data.model.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tracks: List<Track>)

    @Query("SELECT * FROM tracks ORDER BY dateAdded DESC")
    fun getAllTracks(): Flow<List<Track>>

    @Query("SELECT * FROM tracks ORDER BY dateAdded DESC LIMIT :limit")
    fun getRecentTracks(limit: Int = 20): Flow<List<Track>>

    @Query("SELECT * FROM tracks WHERE isLiked = 1 ORDER BY title ASC")
    fun getLikedTracks(): Flow<List<Track>>

    @Query("""
        SELECT * FROM tracks 
        WHERE title LIKE '%' || :query || '%'
           OR artist LIKE '%' || :query || '%'
           OR album LIKE '%' || :query || '%'
        ORDER BY title ASC
    """)
    fun searchTracks(query: String): Flow<List<Track>>

    @Query("SELECT * FROM tracks WHERE album = :album ORDER BY trackNumber ASC")
    fun getTracksByAlbum(album: String): Flow<List<Track>>

    @Query("SELECT * FROM tracks WHERE artist = :artist ORDER BY album ASC, trackNumber ASC")
    fun getTracksByArtist(artist: String): Flow<List<Track>>

    @Query("SELECT DISTINCT album, albumId, artist FROM tracks ORDER BY album ASC")
    fun getAlbums(): Flow<List<AlbumSummary>>

    @Query("SELECT DISTINCT artist FROM tracks ORDER BY artist ASC")
    fun getArtists(): Flow<List<ArtistSummary>>

    @Query("UPDATE tracks SET isLiked = :liked WHERE id = :trackId")
    suspend fun setLiked(trackId: Long, liked: Boolean)

    @Query("SELECT COUNT(*) FROM tracks")
    suspend fun getTrackCount(): Int

    @Delete
    suspend fun delete(track: Track)

    @Query("DELETE FROM tracks")
    suspend fun clearAll()
}

data class AlbumSummary(val album: String, val albumId: Long, val artist: String)
data class ArtistSummary(val artist: String)
