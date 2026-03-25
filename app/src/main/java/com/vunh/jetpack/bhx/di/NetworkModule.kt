package com.vunh.jetpack.bhx.di

import android.util.Log
import com.vunh.jetpack.bhx.data.remote.ApiService
import com.vunh.jetpack.bhx.data.remote.DummyJsonApiService
import com.vunh.jetpack.bhx.data.remote.EscuelaApiService
import com.vunh.jetpack.bhx.data.remote.JsonPlaceholderApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            Log.d("RetrofitLog", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @MainRetrofit
    fun provideMainRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @EscuelaRetrofit
    fun provideEscuelaRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.escuelajs.co/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @JsonPlaceholderRetrofit
    fun provideJsonPlaceholderRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(@JsonPlaceholderRetrofit retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideEscuelaApiService(@EscuelaRetrofit retrofit: Retrofit): EscuelaApiService {
        return retrofit.create(EscuelaApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDummyJsonApiService(@MainRetrofit retrofit: Retrofit): DummyJsonApiService {
        return retrofit.create(DummyJsonApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideJsonPlaceholderApiService(@JsonPlaceholderRetrofit retrofit: Retrofit): JsonPlaceholderApiService {
        return retrofit.create(JsonPlaceholderApiService::class.java)
    }
}
