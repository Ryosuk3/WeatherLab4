package com.example.weatherlab4.di

import android.content.Context
import com.example.weatherlab4.location.LocationClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
    @Provides @Singleton fun fused(@ApplicationContext c: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(c)
    @Provides @Singleton fun client(@ApplicationContext c: Context, fused: FusedLocationProviderClient) =
        LocationClient(c, fused)
}