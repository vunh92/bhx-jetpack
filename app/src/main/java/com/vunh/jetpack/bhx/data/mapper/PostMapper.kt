package com.vunh.jetpack.bhx.data.mapper

import com.vunh.jetpack.bhx.data.local.entity.PostEntity
import com.vunh.jetpack.bhx.domain.model.Post

fun Post.toEntity(): PostEntity = PostEntity(
    userId = userId,
    id = id,
    title = title,
    body = body
)

fun PostEntity.toDomain(): Post = Post(
    userId = userId,
    id = id,
    title = title,
    body = body
)
