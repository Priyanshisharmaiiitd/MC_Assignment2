package com.example.mc_assign2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mc_2.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.annotations.SerializedName
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeParseException

data class TempData(
    val userdate: String,
    val maxTemp: Double?,
    val minTemp: Double?
)

class MainActivity : ComponentActivity() {
    private var userdate by mutableStateOf("")
    private var maxTemp by mutableStateOf<Double?>(null)
    private var minTemp by mutableStateOf<Double?>(null)
    private var errorMes by mutableStateOf<String?>(null)
    private val listofweather = mutableStateListOf<TempData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(color = Color.White) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Cyan),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Text(
                            modifier = Modifier.padding(7.dp),
                            text = "Weather Prediction", fontSize = 50.sp, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.padding(5.dp)) {
                        Column {
                            TextField(
                                modifier = Modifier
                                    .border(
                                        BorderStroke(2.dp, Color.Black)
                                    ),
                                value = userdate,
                                onValueChange = { userdate = it },
                                placeholder = { Text(text = "YYYY-MM-DD") }
                            )

                            Spacer(modifier = Modifier.height(8.dp)) // Adding some space between TextField and Button

                            Button(onClick = { fetchWeatherData() },
                                modifier = Modifier.wrapContentWidth(),
                                colors= ButtonDefaults.buttonColors(containerColor = Color.Blue)
                            )
                            {
                                Text(
                                    "Fetch Weather Data",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row (modifier = Modifier.padding(5.dp)){
                        Column(modifier = Modifier.padding(5.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween // Aligns items at the start and end of the row
                            ) {
                                // Maximum Temperature
                                Row {
                                    Box(
                                        modifier = Modifier
                                            .background(Color.Blue) // Set the background color of the box to blue
                                    ) {
                                        Text(
                                            text = "Max Temp:-",
                                            fontSize = 15.sp,
                                            fontFamily = FontFamily.Monospace,
                                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Yellow),
                                            modifier = Modifier.padding(8.dp) // Add padding to the text inside the box
                                        )
                                    }

                                }

                                // Spacer to push the next item to the right
                                Spacer(modifier = Modifier.weight(1f))

                                // Minimum Temperature
                                Row {
                                    Box(
                                        modifier = Modifier
                                            .background(Color.Blue)
                                    ) {
                                        Text(
                                            text = "Min Temp:-",
                                            fontSize = 15.sp,
                                            fontFamily = FontFamily.Monospace,
                                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Yellow),
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween // Aligns items at the start and end of the row
                            ) {
                                // Maximum Temperature value
                                Row {
                                    maxTemp?.let {
                                        Text(
                                            color = Color.Red,
                                            fontSize = 20.sp,
                                            fontFamily = FontFamily.Monospace,
                                            text = "$it °C",
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }
                                }

                                // Spacer to push the next item to the right
                                Spacer(modifier = Modifier.weight(1f))

                                // Minimum Temperature value
                                Row {
                                    minTemp?.let {
                                        Text(
                                            color = Color.Red,
                                            fontSize = 20.sp,
                                            fontFamily = FontFamily.Monospace,
                                            text = "$it °C",
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }
                                }
                            }
                        }

                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.15f)
                            .padding(3.dp)
                    ) {
                        errorMes?.let {
                            Text(
                                text = it,
                                color = Color.Black,

                                fontSize = 25.sp,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .wrapContentSize(Alignment.Center),
                                textAlign = TextAlign.Center
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(4.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(onClick = { getAllWeatherData() },
                                colors= ButtonDefaults.buttonColors(containerColor = Color.Blue)
                            ) {
                                Text("Show All Rows")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Display fetched rows
                            LazyColumn {
                                items(listofweather) { weather ->
                                    Text(
                                        color = Color.Black,
                                        text = "Date : ${weather.userdate}, Max Temp : ${weather.maxTemp}, Min Temp : ${weather.minTemp}",
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    private fun fetchWeatherData() {
        val dateString = userdate
        if (dateString.isBlank()) {
            return
        }

        if (!isDateValid(dateString)) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val weatherDataSource = WeatherDataRepository(this@MainActivity)

                // Check if weather data for the given date is available in the database
                val databaseWeather = weatherDataSource.getWeather(dateString)
                if (databaseWeather != null) {
                    withContext(Dispatchers.Main) {
                        maxTemp = databaseWeather.maxTemp
                        minTemp = databaseWeather.minTemp
                        errorMes = "Values are retrieved from Database" // if data is fetched successfully
                    }
                } else {
                    val response = RetrofitClient.weatherApiService.getWeather(
                        ltd = 12.971599,
                        lgd = 77.594566,
                        stdate = dateString,
                        fdate = dateString
                    )
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val weather = response.body()?.daily
                            weather?.let {
                                var maT = it.temperature2mMax.firstOrNull()
                                var miT = it.temperature2mMin.firstOrNull()
                                var flag = 1
                                if (maT == null && miT == null) {
                                    val averageTemperature = getYears(dateString)
                                    maT = averageTemperature.averageMaxTemp
                                    miT = averageTemperature.averageMinTemp
                                    flag=0
                                }
                                // Insert the fetched weather data into the database
                                val weatherEntry = TempData(dateString, maT, miT)
                                weatherDataSource.insertWeather(weatherEntry)
                                // Update UI with fetched weather data
                                this@MainActivity.maxTemp = maT
                                this@MainActivity.minTemp = miT
                                errorMes = if(flag==1){
                                    "API Values" // if data is fetched successfully
                                } else{
                                    "Averaged Valued"
                                }

                            }
                        } else {
                            val averageTemperature = getYears(dateString)
                            errorMes = "Averaged Valued"
                            val avgMaTem = averageTemperature.averageMaxTemp
                            val avgMiTem = averageTemperature.averageMinTemp
                            // Insert the fetched weather data into the database
                            val weatherEntry = TempData(dateString, avgMaTem, avgMiTem)
                            weatherDataSource.insertWeather(weatherEntry)
                            // Update UI with average temperatures
                            this@MainActivity.maxTemp = avgMaTem
                            this@MainActivity.minTemp = avgMiTem
                        }
                    }
                }
            } catch (e: Exception) {
                errorMes = "An error occurred: ${e.message}"
            }
        }
    }
    private fun deleteAllWeatherData() {
        val weatherDataSource = WeatherDataRepository(this)
        weatherDataSource.deleteAllWeatherData()
        errorMes="Cleared Database"
        // Clear the UI by resetting maxTemp and minTemp
        maxTemp = null
        minTemp = null
        listofweather.clear()

    }

    private fun getAllWeatherData() {
        val weatherDataSource =
            WeatherDataRepository(this)
        listofweather.clear()
        listofweather.addAll(weatherDataSource.getAllWeatherData())
        maxTemp = null
        minTemp = null
        errorMes = "ShowCasing rows"
    }

    private suspend fun getYears(dateString: String): AverageTemperature {
        // Fetch historical weather data for the previous 10 years
        val datefut = LocalDate.parse(dateString)
        val monfut = datefut.monthValue
        val dayfut = datefut.dayOfMonth

        val todayDate = LocalDate.now()
        val todayYear = todayDate.year
        val previousyearslist = mutableListOf<WeatherResponse>()

        // Iterate over the previous 10 years
        for (i in 1..10) {
            // Subtract 'i' years from the current year
            val year = todayYear - i
            errorMes = if(year==2013){
                "Fetched"
            } else{
                "Working: $year"
            }
            // Construct the date string for the same day and month but different year
            val newDate = String.format("%d-%02d-%02d", year, monfut, dayfut)

            // Make API request for the constructed date
            val response = RetrofitClient.weatherApiService.getWeather(
                ltd = 12.971599,
                lgd = 77.594566,
                stdate = newDate,
                fdate = newDate
            )

            if (response.isSuccessful) {
                val weatherResponse = response.body()
                weatherResponse?.let {
                    previousyearslist.add(it)
                }
            } else {
                errorMes="Failed to fetch weather data from API"
            }
        }
        val totalMaxTemp = previousyearslist.sumOf {
            it.daily.temperature2mMax.firstOrNull() ?: 0.0
        }
        val totalMinTemp = previousyearslist.sumOf {
            it.daily.temperature2mMin.firstOrNull() ?: 0.0
        }
        val avgMaTem = (totalMaxTemp / previousyearslist.size).toBigDecimal()
            .setScale(1, RoundingMode.HALF_EVEN).toDouble()
        val avgMiTem = (totalMinTemp / previousyearslist.size).toBigDecimal()
            .setScale(1, RoundingMode.HALF_EVEN).toDouble()

        return AverageTemperature(avgMaTem, avgMiTem)
    }

    private fun isDateValid(dateString: String): Boolean {
        val regex = """^\d{4}-\d{2}-\d{2}$""".toRegex()

        if (!dateString.matches(regex)) {
            errorMes = "Invalid date format. Please use YYYY-MM-DD."
            return false
        }
        val (year, month, day) = dateString.split("-").map { it.toInt() }
        if (month < 1 || month > 12) {
            errorMes = "Invalid month"
            return false
        }
        val validDays = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
            else -> return false
        }
        if (day < 1 || day > validDays) {
            errorMes = "Invalid day"
            return false
        }
        return true
    }
}


data class WeatherResponse(
    @SerializedName("daily") val daily: DailyWeather
)

data class DailyWeather(
    @SerializedName("time") val time: List<String>,
    @SerializedName("temperature_2m_max") val temperature2mMax: List<Double>,
    @SerializedName("temperature_2m_min") val temperature2mMin: List<Double>
)

data class AverageTemperature(val averageMaxTemp: Double, val averageMinTemp: Double)



