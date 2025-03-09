package com.cse.weather.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cse.weather.NetworkCall.RetrofitClient
import com.cse.weather.R
import com.cse.weather.Repo.WeatherRepository
import com.cse.weather.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class HomeFragment : Fragment() {


    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var viewModel: HomeViewModel

    
    lateinit var binding: FragmentHomeBinding



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val swipeRefreshLayout: SwipeRefreshLayout = binding.swipeRefreshLayout

        val apiService = RetrofitClient.instance
        val repository = WeatherRepository(apiService)

        viewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(repository)
        )[HomeViewModel::class.java]

        val appid = "b8595e52bed1e1657145b697a6e278ce"

        // Handle location permissions
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Fetch location & call API
        fun fetchWeatherWithLocation() {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        viewModel.fetchWeather(latitude, longitude, appid)
                    } else {
                        Toast.makeText(context, "Failed to get location", Toast.LENGTH_SHORT).show()
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to get location: ${it.message}", Toast.LENGTH_SHORT).show()
                    swipeRefreshLayout.isRefreshing = false
                }
        }

        // Initial weather fetch
        fetchWeatherWithLocation()

        // Swipe-to-refresh setup
        swipeRefreshLayout.setOnRefreshListener {
            fetchWeatherWithLocation()
        }

        // Observe weather data
        viewModel.weatherLiveData.observe(viewLifecycleOwner, Observer { weather ->
            Log.d("TAG", "onCreateView: ${weather.main.temp}")
            binding.temperatureTextView.text = "Temp: ${weather.main.temp}°C"
            binding.locationTextView.text = weather.name
            binding.humidityTextView.text = "Humidity: ${weather.main.humidity}"
            binding.tempMaxTextView.text = "Maximum:\n${weather.main.temp_max}°C"
            binding.tempMinTextView.text = "Minimum:\n${weather.main.temp_min}°C"
            binding.pressureTextView.text = "Pressure: ${weather.main.pressure} Pa"
            swipeRefreshLayout.isRefreshing = false
        })

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()

    }


}