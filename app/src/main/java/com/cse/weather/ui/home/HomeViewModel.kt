package com.cse.weather.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import com.cse.weather.DataClass.CurrentWeatherDataClass
import com.cse.weather.Repo.WeatherRepository
import com.cse.weather.roomdb.weatherentity
import kotlinx.coroutines.launch


class HomeViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherLiveData = MutableLiveData<CurrentWeatherDataClass>()
    val weatherLiveData: LiveData<CurrentWeatherDataClass> get() = _weatherLiveData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error



    fun fetchAndSaveWeather(lat: Double?, lon: Double?, apiKey: String) {
        viewModelScope.launch {
            repository.fetchAndSaveWeather(lat, lon, apiKey)
        }
    }



    fun getSavedWeather(): LiveData<weatherentity> {
        return repository.getSavedWeather()
    }
}