package com.example.skyreport.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.skyreport.R
import com.example.skyreport.activities.WeatherPage
import com.example.skyreport.data.api.WeatherApi
import com.example.skyreport.data.models.weather.WeatherResponce
import com.example.skyreport.utils.NatworkUtils

class WeatherWidgetProvider : AppWidgetProvider() {



    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        for (appWidgetId in appWidgetIds!!) {
            updateAppWidget(context,appWidgetManager,appWidgetId)
        }
    }

    companion object{
//        private val weatherApi = NatworkUtils.getRetrofitInstance().create(WeatherApi::class.java)


         fun updateAppWidget(
            context: Context?,
            appWidgetManager: AppWidgetManager?,
            appWidgetId: Int
        ){

            val views = RemoteViews(context?.packageName,R.layout.widget_weather)
            val sharedpref = context?.getSharedPreferences("weather_pref", Context.MODE_PRIVATE)
            val city = sharedpref?.getString("widget_city", "City")
             val temp = sharedpref?.getString("widget_temp", "--°")
             val condition = sharedpref?.getString("widget_condition", "Loading...")


             views.setTextViewText(R.id.tvWidgetCity,city)
             views.setTextViewText(R.id.tvWidgettemp,temp)
             views.setTextViewText(R.id.tvWidgetCondition,condition)

             val intent = Intent(context, WeatherPage ::class.java )
             val pandingIntent = PendingIntent.getActivity(
                 context,0,intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
             )

             views.setOnClickPendingIntent(R.id.tvWidgetCity,pandingIntent)
             appWidgetManager?.updateAppWidget(appWidgetId,views)

//            val weather = fetchWeatherdata(city)

        }

//        private suspend fun fetchWeatherdata(cityname: String?) : WeatherResponce? {
//            return try {
//                weatherApi.getWeather(cityname)
//            }catch (e: Exception){
//                null
//            }
//        }
    }
}