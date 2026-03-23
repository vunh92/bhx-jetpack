package com.vunh.jetpack.bhx.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vunh.jetpack.bhx.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY time DESC")
    fun observeNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("SELECT COUNT(*) FROM notifications")
    suspend fun countNotifications(): Int

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
}
