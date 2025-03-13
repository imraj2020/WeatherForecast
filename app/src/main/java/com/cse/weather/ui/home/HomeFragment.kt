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
import com.cse.weather.roomdb.WeatherDatabase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class HomeFragment : Fragment() {


    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var viewModel: HomeViewModel

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

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




        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val swipeRefreshLayout: SwipeRefreshLayout = binding.swipeRefreshLayout

        val apiService = RetrofitClient.instance
        val database = WeatherDatabase.getDatabase(requireContext())
        val repository = WeatherRepository(apiService, database.weatherDao())



        viewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(repository)
        )[HomeViewModel::class.java]

        val appid = "b8595e52bed1e1657145b697a6e278ce"


        // Fetch location & call API
        fun fetchWeatherWithLocation() {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        viewModel.fetchAndSaveWeather(latitude, longitude, appid)
                        swipeRefreshLayout.isRefreshing = false
                    } else {
                        Toast.makeText(context, "Failed to get location", Toast.LENGTH_SHORT).show()
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        context,
                        "Failed to get location: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
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
//        viewModel.weatherLiveData.observe(viewLifecycleOwner, Observer { weather ->
//            Log.d("TAG", "onCreateView: ${weather.main.temp}")
//            binding.temperatureTextView.text = "Temp: ${weather.main.temp}°C"
//            binding.locationTextView.text = weather.name
//            binding.humidityTextView.text = "Humidity: ${weather.main.humidity}"
//            binding.tempMaxTextView.text = "Maximum:\n${weather.main.temp_max}°C"
//            binding.tempMinTextView.text = "Minimum:\n${weather.main.temp_min}°C"
//            binding.pressureTextView.text = "Pressure: ${weather.main.pressure} Pa"
//            swipeRefreshLayout.isRefreshing = false
//        })

        viewModel.getSavedWeather().observe(viewLifecycleOwner, Observer { weather ->
            if (weather != null) {

                binding.temperatureTextView.text =
                    "Temp: ${weather.temperature}°C"
                binding.locationTextView.text = weather.cityName // And here
                binding.humidityTextView.text = "Humidity: ${weather.humidity}"
                binding.tempMaxTextView.text = "Maximum:\n${weather.tempMax}°C"
                binding.tempMinTextView.text = "Minimum:\n${weather.tempMin}°C"
                binding.pressureTextView.text = "Pressure: ${weather.pressure} Pa"
                swipeRefreshLayout.isRefreshing = false
            } else {
                // weather IS null, handle this case
                Log.e("HomeFragment", "Weather data is null!")
                // Option 1: Set default values (if you have them)
                binding.temperatureTextView.text = "Temp: N/A"
                binding.locationTextView.text = "N/A"
                binding.humidityTextView.text = "Humidity: N/A"
                binding.tempMaxTextView.text = "Maximum:\nN/A"
                binding.tempMinTextView.text = "Minimum:\nN/A"
                binding.pressureTextView.text = "Pressure: N/A"
            }
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