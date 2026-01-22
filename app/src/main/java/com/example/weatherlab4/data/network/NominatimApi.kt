package com.example.weatherlab4.data.network

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

@Serializable
data class NominatimReverseResponse(
    val display_name: String? = null,
    val address: Address? = null,
) {
    @Serializable
    data class Address(
        val city: String? = null,
        val town: String? = null,
        val village: String? = null,
        val state: String? = null,
        val country: String? = null,
    )
}

interface NominatimApi {
    @GET("reverse")
    suspend fun reverse(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("format") format: String = "jsonv2",
        @Query("accept-language") lang: String
    ): NominatimReverseResponse
}