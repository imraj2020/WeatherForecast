package com.cse.weather.ui.dashboard

import android.R
import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.cse.weather.NetworkCall.RetrofitClient
import com.cse.weather.Repo.WeatherRepository
import com.cse.weather.databinding.FragmentDashboardBinding
import com.cse.weather.databinding.FragmentHomeBinding
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
        val repository = WeatherRepository(apiService)

        viewModel = ViewModelProvider(
            this,
            DashboardViewModelFactory(repository)
        )[DashboardViewModel::class.java]

        val appid = "b8595e52bed1e1657145b697a6e278ce"

        fun getCoordinates(context: Context, locationName: String) {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addressList = geocoder.getFromLocationName(locationName, 1)

            addressList?.firstOrNull()?.let { address ->
                latitude = address.latitude
                longitude = address.longitude
            } ?: run {
                latitude = 0.0 // or some valid default value
                longitude = 0.0
            }
        }





      //  Log.d("Geocoding", "Lat: $latitude, Lng: $longitude")

      //  Toast.makeText(context, "Lat: $latitude, Lng: $longitude", Toast.LENGTH_SHORT).show()


// Example location list (this could come from an API)
        val locations = listOf(
            "New York", "Los Angeles", "Chicago", "San Francisco", "Miami",
            "Toronto", "Vancouver", "Montreal", "London", "Paris",
            "Berlin", "Sydney", "Tokyo", "Seoul", "Shanghai",
            "Dubai", "Istanbul", "Moscow", "Rio de Janeiro", "Buenos Aires",
            "Cape Town", "Rome", "Madrid", "Singapore", "Bangkok",
            "Mexico City", "Cairo", "Los Angeles", "San Diego", "Seattle",
            "Dubai", "Beijing", "Stockholm", "Oslo", "Copenhagen"
        )


        context?.let { ctx ->
            locations.distinct().forEach { location ->
                getCoordinates(ctx, location)
            }
        }

        val adapter = ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, locations)
        adapter.notifyDataSetChanged()
        binding.locationSearchBox.setAdapter(adapter)

        // Handle item click
        binding.locationSearchBox.setOnItemClickListener { _, _, position, _ ->
            val selectedLocation = adapter.getItem(position)
            Toast.makeText(requireContext(), "Selected: $selectedLocation", Toast.LENGTH_SHORT)
                .show()


                latitude?.let { lat ->
                    longitude?.let { lon ->
                        viewModel.fetchWeather(lat, lon,appid)
                        adapter.notifyDataSetChanged()
                        binding.cardContainer.invalidate()
                        binding.cardContainer.requestLayout()
                        binding.cardContainer.visibility = View.VISIBLE
                        Toast.makeText(context, "Lat: $latitude, Lng: $longitude", Toast.LENGTH_SHORT).show()


                    }
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

            })

            // Observe errors
            viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
                if (errorMessage != null) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()

                }
            }



        }







    }




    override fun onDestroyView() {
        super.onDestroyView()

    }
}