package com.example.weatherlab4.data.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Named
import javax.inject.Singleton
import com.example.weatherlab4.data.network.NominatimApi
import com.example.weatherlab4.data.network.NominatimReverseResponse

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE = "https://api.open-meteo.com/"
    private const val GEO  = "https://geocoding-api.open-meteo.com/"

    @Provides @Singleton
    fun json(): Json = Json { ignoreUnknownKeys = true; isLenient = true }

    @Provides @Singleton
    fun okHttp(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .build()

    @Provides @Singleton @Named("forecastApi")
    fun forecastApi(client: OkHttpClient, json: Json): OpenMeteoApi =
        Retrofit.Builder()
            .baseUrl(BASE)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
            .create(OpenMeteoApi::class.java)

    @Provides @Singleton @Named("geocodeApi")
    fun geocodeApi(client: OkHttpClient, json: Json): OpenMeteoApi =
        Retrofit.Builder()
            .baseUrl(GEO)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
            .create(OpenMeteoApi::class.java)

    @Provides @Singleton @Named("nominatimClient")
    fun nominatimClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            // У Nominatim обязателен осмысленный User-Agent
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("User-Agent", "WeatherApp/1.0 (contact: vromanenko2004@gmail.com)")
                        .build()
                )
            }
            .build()

    @Provides @Singleton @Named("nominatimApi")
    fun nominatimApi(
        @Named("nominatimClient") client: OkHttpClient,
        json: Json
    ): NominatimApi =
        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
            .create(NominatimApi::class.java)
}
