package com.cse.weather.Repo

import androidx.lifecycle.LiveData
import com.cse.weather.DataClass.CurrentWeatherDataClass
import com.cse.weather.NetworkCall.ApiService
import com.cse.weather.roomdb.WeatherDAO
import com.cse.weather.roomdb.WeatherDatabase
import com.cse.weather.roomdb.weatherentity

class WeatherRepository(private val apiService: ApiService , private val weatherDao: WeatherDAO) {
    suspend fun getWeather(lat: Double?, lon: Double?, apiKey: String): CurrentWeatherDataClass {
        return apiService.getWeather(lat, lon, apiKey)
    }




    suspend fun fetchAndSaveWeather(lat: Double?,lon: Double?, apiKey: String) {
        try {

            val weatherResponse = apiService.getWeather(lat, lon,apiKey)
            val weatherEntity = weatherentity(
                id = weatherResponse.id,
                temperature = weatherResponse.main.temp,
                cityName = weatherResponse.name,
                humidity = weatherResponse.main.humidity,
                tempMax = weatherResponse.main.temp_max,
                tempMin = weatherResponse.main.temp_min,
                pressure = weatherResponse.main.pressure
            )
            weatherDao.insertWeather(weatherEntity)
        } catch (e: Exception) {

          //  Log.e("WeatherRepository", "Error fetching data: ${e.message}")
        }
    }


    fun getSavedWeather(): LiveData<weatherentity> {
        return weatherDao.getLatestWeather()
    }

    suspend fun clearWeather() {
        weatherDao.deleteAllWeather()
    }

}