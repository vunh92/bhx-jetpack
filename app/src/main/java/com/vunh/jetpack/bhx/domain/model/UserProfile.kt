package com.vunh.jetpack.bhx.domain.model

data class UserProfile(
    val id: Int,
    val name: String,
    val phoneNumber: String,
    val rank: String,
    val points: Int,
    val memberCode: String,
    val walletBalance: Long = 0,
    val notificationCount: Int = 0,
    val couponCount: Int = 0,
    val giftCount: Int = 0,
    val addressCount: Int = 0
)
