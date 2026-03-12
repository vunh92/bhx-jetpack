package com.vunh.jetpack.bhx.di

import com.vunh.jetpack.bhx.data.remote.ApiService
import com.vunh.jetpack.bhx.data.repository.HomeRepositoryImpl
import com.vunh.jetpack.bhx.domain.repository.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideHomeRepository(apiService: ApiService): HomeRepository {
        return HomeRepositoryImpl(apiService)
    }
}
