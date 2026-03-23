package com.vunh.jetpack.bhx.data.mapper

import com.vunh.jetpack.bhx.data.local.entity.NotificationEntity
import com.vunh.jetpack.bhx.presentation.profile.NotificationItem

fun NotificationEntity.toUiModel(): NotificationItem = NotificationItem(
    id = id,
    title = title,
    content = content,
    time = time,
    isRead = isRead
)

fun NotificationItem.toEntity(): NotificationEntity = NotificationEntity(
    id = id,
    title = title,
    content = content,
    time = time,
    isRead = isRead
)
