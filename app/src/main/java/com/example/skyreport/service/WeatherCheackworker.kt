package com.example.skyreport.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.skyreport.R
import com.example.skyreport.data.api.WeatherApi
import com.example.skyreport.utils.NatworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.Manifest
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Color
import com.example.skyreport.data.models.weather.WeatherResponce
import com.example.skyreport.widget.WeatherWidgetProvider

class WeatherCheackworker(
    private val context: Context,
    workerParams: WorkerParameters,
): CoroutineWorker(context, workerParams) {

    private val weatherApi = NatworkUtils.getRetrofitInstance().create(WeatherApi::class.java)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        if (!isNetworkAvailable(context)) {
            return@withContext Result.retry()
        }

        val sharedPref = context.getSharedPreferences("weather_pref", Context.MODE_PRIVATE)
        val cityName = sharedPref.getString("city_name", "")

        if (cityName.isNullOrEmpty()) return@withContext Result.success()

        val lastCondition = sharedPref.getString("last_condition", "")

        // Fetch the full weather data
        val weatherData = fetchFullWeatherData(cityName)
        if (weatherData == null) {
            return@withContext Result.retry()
        }

        val currentCondition = weatherData.weather.firstOrNull()?.main ?: "Unknown"

        if (lastCondition.isNullOrEmpty()) {
            // First run: establish baseline
            sharedPref.edit().putString("last_condition", currentCondition).apply()
        } else if (!lastCondition.equals(currentCondition, ignoreCase = true)) {
            // Weather changed: Show detailed notification
            val title = "${weatherData.main.temp.toInt()}° in $cityName"
            val message = "Feels like ${weatherData.main.feels_like.toInt()}° | $currentCondition | HIGH: ${weatherData.main.temp_max.toInt()}° LOW: ${weatherData.main.temp_min.toInt()}°"

            showNotification(title, message)

            // Save new condition
            sharedPref.edit().putString("last_condition", currentCondition).apply()
            sharedPref.edit().apply{
                putString("widget_city", weatherData.name)
                putString("widget_temp", "${weatherData.main.temp.toInt()}°")
                putString("widget_condition", weatherData.weather.firstOrNull()?.main)
                apply()
            }

            val intent = Intent(context, WeatherWidgetProvider ::class.java ).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            context.sendBroadcast(intent)
        }

        Result.success()
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private suspend fun fetchFullWeatherData(cityName: String): WeatherResponce? {
        return try {
            weatherApi.getWeather(cityName)
        } catch (e: Exception) {
            null
        }
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "weather_alerts"
        val manager = context.getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notification for weather changes"
                enableLights(true)
                lightColor = Color.BLUE
            }
            manager?.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notify_logo2)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(1001, notification)
        }
    }
}