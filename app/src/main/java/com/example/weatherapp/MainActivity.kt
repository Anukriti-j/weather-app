package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
private val binding: ActivityMainBinding by lazy {
    ActivityMainBinding.inflate(layoutInflater)
}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Tikamgarh")
        searchCity()
    }

    private fun searchCity() {
        val searchView= binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })

    }

    private fun fetchWeatherData(cityname: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build()
            .create(ApiInterface::class.java)

        val response = retrofit.getweatherdata(cityname, "c5eb0778c430844aafeb3614c1667180", units = "metric")

        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity= responseBody.main.humidity
                    val wind= responseBody.wind.speed
                    val sunrise= responseBody.sys.sunrise
                    val sunset= responseBody.sys.sunset
                    val sea= responseBody.main.pressure
                    val condition= responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxtemp= responseBody.main.temp_max
                    val mintemp= responseBody.main.temp_min

                    binding.temp.text= "$temperature °C"
                    binding.weather.text= condition
                    binding.maxTemp.text= "Max temp: $maxtemp °C "
                    binding.minTemp.text= "Min temp: $mintemp °C "
                    binding.wind.text= "$wind m/s"
                    binding.humidity.text= "$humidity %"
                    binding.sunrise.text= "${time(sunrise)}"
                    binding.sunset.text= "${time(sunset)}"
                    binding.sealevel.text="$sea"
                    binding.condition.text=condition
                    binding.day.text=dayname(System.currentTimeMillis())
                        binding.date.text=date()
                        binding.city.text="$cityname"
                    changeimageaccordingtoweather(condition)



                } else {
                    Log.e("MainActivity", "Response was not successful or body was null")
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Log.e("MainActivity", "Error fetching weather data", t)
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Failed to fetch weather data", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    private fun changeimageaccordingtoweather(conditions: String) {
        when(conditions){
            "Haze","Mist","Clouds","Partly clouds","Overcast","Mist"-> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.weatherimage.setImageResource(R.drawable.cloud_black)
            }
            "clear Sky","Sunny","Clear"-> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.weatherimage.setImageResource(R.drawable.sunny)
            }
            "Rain","Light Rain","Moderate rain","Showers","Drizzle","heavy Rain"-> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.weatherimage.setImageResource(R.drawable.rain)
            }
            "Light snow","Moderate snow","Snow","Heavy snow","Blizzard"-> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.weatherimage.setImageResource(R.drawable.snow)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.weatherimage.setImageResource(R.drawable.sunny)
            }
        }


    }

    private fun date(): String {
        val sdf= SimpleDateFormat("dd - MM - yyyy", Locale.getDefault())
        return sdf.format((Date()))

    }
    fun time(timestamp: Int): String{
        val sdf= SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(timestamp*1000)
    }


    fun dayname(timestamp: Long): String{
        val sdf= SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }

}
