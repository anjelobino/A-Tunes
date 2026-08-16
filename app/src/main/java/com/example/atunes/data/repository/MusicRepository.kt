package com.example.atunes.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.atunes.data.db.AppDatabase
import com.example.atunes.data.db.TrackDao
import com.example.atunes.data.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MusicRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    val trackDao: TrackDao = db.trackDao()
    val playlistDao = db.playlistDao()

    /**
     * Scan device via MediaStore and populate Room DB.
     * [folderFilter] if provided, only tracks in this relative path (and subfolders) are included.
     */
    suspend fun scanLibrary(
        folderFilter: String? = null,
        onProgress: (Int) -> Unit = {}
    ) = withContext(Dispatchers.IO) {
        val tracks = mutableListOf<Track>()

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.TRACK,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.RELATIVE_PATH
            } else {
                MediaStore.Audio.Media.DATA
            }
        )

        var selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} > 30000"
        val selectionArgs = mutableListOf<String>()

        if (!folderFilter.isNullOrBlank()) {
            val pathKey = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.RELATIVE_PATH
            } else {
                MediaStore.Audio.Media.DATA
            }
            // Match the folder and its subdirectories
            selection += " AND ($pathKey = ? OR $pathKey LIKE ?)"
            selectionArgs.add(folderFilter)
            selectionArgs.add("${folderFilter.trimEnd('/')}/%")
        }

        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            if (selectionArgs.isEmpty()) null else selectionArgs.toTypedArray(),
            sortOrder
        )?.use { cursor ->
            val idCol       = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol    = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol   = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol    = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdCol  = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dateCol     = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val trackNumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val pathCol     = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.RELATIVE_PATH)
            } else {
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            }

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
                ).toString()

                val rawPath = cursor.getString(pathCol) ?: "Unknown"
                val relativePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    rawPath.trim('/')
                } else {
                    // Extract folder from absolute path
                    rawPath.substringBeforeLast('/', "Unknown").substringAfterLast('/', "Unknown")
                }

                tracks += Track(
                    id          = id,
                    title       = cursor.getString(titleCol) ?: "Unknown Title",
                    artist      = cursor.getString(artistCol) ?: "Unknown Artist",
                    album       = cursor.getString(albumCol) ?: "Unknown Album",
                    albumId     = cursor.getLong(albumIdCol),
                    duration    = cursor.getLong(durationCol),
                    contentUri  = contentUri,
                    relativePath = relativePath,
                    dateAdded   = cursor.getLong(dateCol),
                    trackNumber = cursor.getInt(trackNumCol)
                )

                if (tracks.size % 50 == 0) {
                    onProgress(tracks.size)
                }
            }
        }

        // Clear stale data and insert fresh batch
        trackDao.clearAll()
        trackDao.insertAll(tracks)
        onProgress(tracks.size)
        tracks.size
    }

    /**
     * Returns the content URI for an album's artwork, usable by Coil.
     */
    fun getAlbumArtUri(albumId: Long): Uri =
        ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"),
            albumId
        )

    fun getAllTracks(): Flow<List<Track>> = trackDao.getAllTracks()
    fun getRecentTracks(limit: Int = 20) = trackDao.getRecentTracks(limit)
    fun getLikedTracks() = trackDao.getLikedTracks()
    fun searchTracks(query: String) = trackDao.searchTracks(query)
    fun getAlbums() = trackDao.getAlbums()
    fun getArtists() = trackDao.getArtists()
    fun getTracksByAlbum(album: String) = trackDao.getTracksByAlbum(album)
    fun getTracksByArtist(artist: String) = trackDao.getTracksByArtist(artist)

    suspend fun toggleLike(track: Track) {
        trackDao.setLiked(track.id, !track.isLiked)
    }

    /**
     * Deletes a track from the device storage and the database.
     * Returns a PendingIntent if user consent is required (Android 10+), or null if deleted.
     */
    suspend fun deleteTrack(track: Track): android.app.PendingIntent? = withContext(Dispatchers.IO) {
        try {
            val uri = Uri.parse(track.contentUri)
            context.contentResolver.delete(uri, null, null)
            trackDao.delete(track)
            null
        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && e is android.app.RecoverableSecurityException) {
                e.userAction.actionIntent
            } else {
                throw e
            }
        }
    }

    suspend fun getTrackCount() = trackDao.getTrackCount()
}
