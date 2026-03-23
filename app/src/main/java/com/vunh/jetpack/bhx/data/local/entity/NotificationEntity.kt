package com.vunh.jetpack.bhx.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val time: String,
    val isRead: Boolean
)
