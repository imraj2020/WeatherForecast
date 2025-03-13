package com.cse.weather.roomdb
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WeatherDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)

    suspend fun insertWeather(weather: weatherentity)

    @Query("SELECT * FROM weather_table ORDER BY id DESC LIMIT 1")
    fun getLatestWeather(): LiveData<weatherentity>

    @Query("DELETE FROM weather_table")
    suspend fun deleteAllWeather()
//    @Delete
//    suspend fun deleteAllWeather()

}