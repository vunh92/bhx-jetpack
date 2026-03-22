package com.vunh.jetpack.bhx.di

import android.content.Context
import androidx.room.Room
import com.vunh.jetpack.bhx.data.local.AppDatabase
import com.vunh.jetpack.bhx.data.local.dao.PostDao
import com.vunh.jetpack.bhx.data.local.ProfileManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "bhx_database"
        ).build()
    }

    @Provides
    fun providePostDao(appDatabase: AppDatabase): PostDao {
        return appDatabase.postDao()
    }

    @Provides
    @Singleton
    fun provideProfileManager(@ApplicationContext context: Context): ProfileManager {
        return ProfileManager(context)
    }
}
