package com.cse.weather.NetworkCall

import com.cse.weather.DataClass.CurrentWeatherDataClass
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {


    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("appid") apiKey: String
    ): CurrentWeatherDataClass

}