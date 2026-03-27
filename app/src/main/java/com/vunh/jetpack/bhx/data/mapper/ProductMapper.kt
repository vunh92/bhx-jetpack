package com.vunh.jetpack.bhx.data.mapper

import com.vunh.jetpack.bhx.data.remote.model.DummyCategoryModel
import com.vunh.jetpack.bhx.data.remote.model.DummyCategoryProductModel
import com.vunh.jetpack.bhx.data.remote.model.DummyProductModel
import com.vunh.jetpack.bhx.data.remote.model.ProductEscuelaModel
import com.vunh.jetpack.bhx.domain.model.Category
import com.vunh.jetpack.bhx.domain.model.Product

fun ProductEscuelaModel.toDomain(): Product {
    return Product(
        id = id,
        title = title,
        price = price,
        description = description,
        images = images,
        categoryName = category.name
    )
}

fun DummyCategoryModel.toDomain(): Category {
    return Category(
        name = name,
        slug = slug,
        url = url
    )
}

fun DummyCategoryProductModel.toDomain(): Product {
    return Product(
        id = id,
        title = title,
        price = price.toInt(),
        description = description,
        images = if (images.isNotEmpty()) images else listOf(thumbnail),
        categoryName = category
    )
}

fun DummyProductModel.toDomain(): Product {
    return Product(
        id = id,
        title = title,
        price = price.toInt(),
        description = description,
        images = if (images.isNotEmpty()) images else listOf(thumbnail),
        categoryName = category
    )
}
