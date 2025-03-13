package com.cse.weather.roomdb
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "weather_table")
data class weatherentity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cityName: String,
    val temperature: Double,
    val humidity: Int,
    val tempMax: Double,
    val tempMin: Double,
    val pressure: Int
)