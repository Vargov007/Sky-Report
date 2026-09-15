package com.example.skyreport.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.AppWidgetTarget
import com.example.skyreport.R
import com.example.skyreport.activities.WeatherPage

class WeatherWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        context?.let { ctx ->
            appWidgetManager?.let { manager ->
                appWidgetIds?.forEach { appWidgetId ->
                    updateAppWidget(ctx, manager, appWidgetId)
                }
            }
        }
    }

    companion object {

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_weather)
            val sharedPref = context.getSharedPreferences("weather_pref", Context.MODE_PRIVATE)

            val city = sharedPref.getString("widget_city", "City")
            val temp = sharedPref.getString("widget_temp", "--°")
            val condition = sharedPref.getString("widget_condition", "Loading...")
            val isDaytime = sharedPref.getBoolean("widget_is_daytime", true)

            // Update Text Views
            views.setTextViewText(R.id.tvWidgetCity, city)
            views.setTextViewText(R.id.tvWidgettemp, temp)
            views.setTextViewText(R.id.tvWidgetCondition, condition)

            // Get the appropriate raw/drawable icon resource
            val imgRes = getImgIcon(condition, isDaytime)

            // Correct Glide implementation for App Widgets
            val appWidgetTarget = AppWidgetTarget(context, R.id.widgetimg, views, appWidgetId)

            Glide.with(context.applicationContext)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .load(imgRes)
                .placeholder(R.drawable.no_connection)
                .into(appWidgetTarget)

            // Intent to open WeatherPage on widget click
            val intent = Intent(context, WeatherPage::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            views.setOnClickPendingIntent(R.id.tvWidgetCity, pendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getImgIcon(condition: String?, isDaytime: Boolean): Int {
            val formattedCondition = condition?.lowercase() ?: ""

            return if (isDaytime) {
                when {
                    formattedCondition.contains("clear") || formattedCondition.contains("sunny") -> R.drawable.icon_sunny
                    formattedCondition.contains("cloud") -> R.drawable.icon_cloudy_sky_day
                    formattedCondition.contains("rain") || formattedCondition.contains("drizzle") -> R.drawable.icon_rain
                    formattedCondition.contains("storm") -> R.drawable.icon_havy_rain
                    formattedCondition.contains("thunder") -> R.drawable.icon_lightning_bolt
                    formattedCondition.contains("snow") -> R.drawable.icon_snow
                    else -> R.drawable.icon_sunny
                }
            } else {
                when {
                    formattedCondition.contains("clear") || formattedCondition.contains("sunny") -> R.drawable.icon_clear_night
                    formattedCondition.contains("cloud") -> R.drawable.icon_cloudy_night2
                    formattedCondition.contains("rain") || formattedCondition.contains("drizzle") -> R.drawable.icon_rain
                    formattedCondition.contains("storm") -> R.drawable.icon_havy_rain
                    formattedCondition.contains("thunder") -> R.drawable.icon_lightning_bolt
                    formattedCondition.contains("snow") -> R.drawable.icon_snow
                    else -> R.drawable.icon_clear_night
                }
            }
        }
    }
}