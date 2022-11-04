package com.yb.part6_chapter01.data.repository.gallery

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.yb.part6_chapter01.data.entity.GalleryPhotoEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultGalleryRepository(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
) : GalleryRepository {

    override suspend fun getAllPhotos(): MutableList<GalleryPhotoEntity> = withContext(ioDispatcher) {
        val galleryPhotoList = mutableListOf<GalleryPhotoEntity>()
        val uriExternal: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val query: Cursor?
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.DISPLAY_NAME,
            MediaStore.Images.ImageColumns.SIZE,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.DATE_ADDED,
            MediaStore.Images.ImageColumns._ID,
        )

        val resolver = context.contentResolver
        query = resolver?.query(uriExternal,
            projection,
            null,
            null,
            "${MediaStore.Images.ImageColumns.DATE_ADDED} DESC")

        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.SIZE)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val size = cursor.getInt(sizeColumn)
                val date = cursor.getString(dateColumn)

                val contentUri = ContentUris.withAppendedId(uriExternal, id)

                galleryPhotoList.add(
                    GalleryPhotoEntity(id, contentUri, name, date ?: "", size)
                )
            }
        }

        return@withContext galleryPhotoList
    }

}