package com.vunh.jetpack.bhx.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.vunh.jetpack.bhx.domain.model.UserProfile

class ProfileManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveProfile(profile: UserProfile) {
        val json = gson.toJson(profile)
        sharedPreferences.edit().putString("user_profile", json).apply()
    }

    fun getProfile(): UserProfile? {
        val json = sharedPreferences.getString("user_profile", null)
        return if (json != null) {
            gson.fromJson(json, UserProfile::class.java)
        } else {
            null
        }
    }

    fun clearProfile() {
        sharedPreferences.edit().remove("user_profile").apply()
    }

    fun isLoggedIn(): Boolean {
        return getProfile() != null
    }
}
