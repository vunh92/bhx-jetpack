package com.vunh.jetpack.bhx.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EscuelaRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DummyJsonRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class JsonPlaceholderRetrofit
