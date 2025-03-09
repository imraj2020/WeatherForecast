package com.cse.weather.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cse.weather.DataClass.CurrentWeatherDataClass
import com.cse.weather.Repo.WeatherRepository
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherLiveData = MutableLiveData<CurrentWeatherDataClass>()
    val weatherLiveData: LiveData<CurrentWeatherDataClass> get() = _weatherLiveData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchWeather(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = repository.getWeather(lat, lon, apiKey)
                _weatherLiveData.postValue(response)
            } catch (e: Exception) {
                _error.postValue("Failed to load weather: ${e.message}")
            }
        }
    }
}