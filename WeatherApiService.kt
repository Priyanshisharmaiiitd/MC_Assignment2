package com.example.mc_2

import com.example.mc_assign2.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("archive")
    suspend fun getWeather(
        @Query("latitude") ltd: Double,
        @Query("longitude") lgd: Double,
        @Query("starting_date") stdate: String,
        @Query("finish_date") fdate: String,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min"
    ): Response<WeatherResponse>
}