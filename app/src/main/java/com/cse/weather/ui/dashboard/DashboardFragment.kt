package com.cse.weather.ui.dashboard

import android.R
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.cse.weather.NetworkCall.RetrofitClient
import com.cse.weather.Repo.WeatherRepository
import com.cse.weather.databinding.FragmentDashboardBinding
import com.cse.weather.roomdb.WeatherDatabase
import com.cse.weather.ui.home.HomeViewModel
import com.cse.weather.ui.home.HomeViewModelFactory
import java.util.Locale


class DashboardFragment : Fragment() {

    private lateinit var viewModel: DashboardViewModel


    lateinit var binding: FragmentDashboardBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var latitude: Double? = null
        var longitude: Double? = null


        val apiService = RetrofitClient.instance
        val database = WeatherDatabase.getDatabase(requireContext())
        val repository = WeatherRepository(apiService, database.weatherDao())

        viewModel = ViewModelProvider(
            this,
            DashboardViewModelFactory(repository)
        )[DashboardViewModel::class.java]

        val appid = "b8595e52bed1e1657145b697a6e278ce"


        // Handle item click
        binding.locationSearchBox.setOnClickListener {

            val locationName = binding.locationSearchBox.text.toString()
            Toast.makeText(requireContext(), "Selected: $locationName", Toast.LENGTH_SHORT)
                .show()




            getCoordinates(locationName)


            val location = getCoordinates(locationName)
            val latitude = location.first
            val longitude = location.second


            viewModel.fetchWeather(latitude, longitude, appid)

            Log.d("BeforeApiCALL", "Details: " + latitude + " " + longitude + " " + appid)

            // Observe weather data
            viewModel.weatherLiveData.observe(viewLifecycleOwner, Observer { weather ->
                Log.d("TAG", "onCreateView: ${weather.main.temp}")
                binding.temperatureTextView.text = "Temp: ${weather.main.temp}°C"
                binding.locationTextView.text = weather.name
                binding.humidityTextView.text = "Humidity: ${weather.main.humidity}"
                binding.tempMaxTextView.text = "Maximum:\n${weather.main.temp_max}°C"
                binding.tempMinTextView.text = "Minimum:\n${weather.main.temp_min}°C"
                binding.pressureTextView.text = "Pressure: ${weather.main.pressure} Pa"

            })

            // Observe errors
            viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
                if (errorMessage != null) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()

                }
            }


        }

//        viewModel.fetchWeather(latitude, longitude, appid)
//        // Observe weather data
//        viewModel.weatherLiveData.observe(viewLifecycleOwner, Observer { weather ->
//            Log.d("TAG", "onCreateView: ${weather.main.temp}")
//            binding.temperatureTextView.text = "Temp: ${weather.main.temp}°C"
//            binding.locationTextView.text = weather.name
//            binding.humidityTextView.text = "Humidity: ${weather.main.humidity}"
//            binding.tempMaxTextView.text = "Maximum:\n${weather.main.temp_max}°C"
//            binding.tempMinTextView.text = "Minimum:\n${weather.main.temp_min}°C"
//            binding.pressureTextView.text = "Pressure: ${weather.main.pressure} Pa"
//
//        })

    }


    private fun getCoordinates(locationName: String): Pair<Double?, Double?> {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        val addresses = geocoder.getFromLocationName(locationName, 1)
        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            val latitude = address.latitude
            val longitude = address.longitude

            return Pair(latitude, longitude)


            Toast.makeText(
                requireContext(),
                "GeoLocation:" + latitude + " " + longitude,
                Toast.LENGTH_SHORT
            ).show()

        } else {
            Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show()



            return Pair(null, null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}