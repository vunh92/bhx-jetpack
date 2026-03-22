package com.vunh.jetpack.bhx.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vunh.jetpack.bhx.data.local.dao.PostDao
import com.vunh.jetpack.bhx.data.local.entity.PostEntity

@Database(
    entities = [PostEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
}
