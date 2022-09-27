package com.yb.part6_chapter01.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yb.part6_chapter01.data.db.dao.LocationDao
import com.yb.part6_chapter01.data.entity.LocationLatLngEntity

@Database(
    entities = [LocationLatLngEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ApplicationDatabase: RoomDatabase() {

    companion object {
        const val DB_NAME = "ApplicationDatabase.db"
    }

    abstract fun LocationDao(): LocationDao
}