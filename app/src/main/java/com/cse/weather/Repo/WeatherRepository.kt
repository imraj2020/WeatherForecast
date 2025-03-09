package com.cse.weather.Repo

import com.cse.weather.DataClass.CurrentWeatherDataClass
import com.cse.weather.NetworkCall.ApiService

class WeatherRepository(private val apiService: ApiService) {
    suspend fun getWeather(lat: Double, lon: Double, apiKey: String): CurrentWeatherDataClass {
        return apiService.getWeather(lat, lon, apiKey)
    }
}