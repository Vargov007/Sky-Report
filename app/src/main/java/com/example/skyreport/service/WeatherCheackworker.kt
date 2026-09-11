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

class WeatherCheackworker(
    private val context: Context,
    workerParams: WorkerParameters,
    ): CoroutineWorker(context, workerParams) {

    private val weatherApi = NatworkUtils.getRetrofitInstance().create(WeatherApi::class.java)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        if (!isNetworkAvailable(context)){
            return@withContext Result.retry()
            
        }

        val sheardpref = context.getSharedPreferences("weather_pref", Context.MODE_PRIVATE)

        val cityName = sheardpref.getString("city_name","")
        val lastCondition = sheardpref.getString("last_condition","")

        val currentCondition = fetchCurrentCondition(cityName) ?: Result.retry()

        if (lastCondition?.isNotEmpty() == true && !lastCondition.equals(currentCondition as String?, ignoreCase = true)){
            showNotification("Weather Alert","The weather in $cityName is now $currentCondition")

            sheardpref.edit().putString("last_condition", currentCondition as String?).apply()
        }

        Result.success()
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }else{
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    private suspend fun fetchCurrentCondition(cityName: String?): String? {
        return try {
            val responce = weatherApi.getWeather(cityName)
            responce.weather.firstOrNull()?.main
        }catch (e: Exception){
            null
        }
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "weather_alerts"
        val manager = context.getSystemService(NotificationManager :: class.java) as NotificationManager

        if (Build.VERSION.SDK_INT  >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                channelId, "Weather Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context,channelId)
            .setSmallIcon(R.mipmap.ic_icon2_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
            NotificationManagerCompat.from(context).notify(1001, notification)
        }
    }




}