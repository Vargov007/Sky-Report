package com.example.skyreport.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skyreport.R
import com.example.skyreport.data.models.hourlyweather.ForecastHour


class HourlyWeatherAdapter : RecyclerView.Adapter<HourlyWeatherAdapter.HourlyViewHolder>()  {

    private var hourlyList : List<ForecastHour> = emptyList()

    fun submitlist(list: List<ForecastHour>?){
        if (list != null) {
            hourlyList = list
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ):HourlyViewHolder {
        val binding = LayoutInflater.from( viewGroup.context)
            .inflate(R.layout.itms_recycle, viewGroup, false)
        return HourlyViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: HourlyWeatherAdapter.HourlyViewHolder,
        position: Int
    ) {
        val forecastHour = hourlyList[position]
        // Formats time as HH:mm and temperature with degree symbol
        val timeStr = "${forecastHour.displayDateTime.hours}:${String.format("%02d", forecastHour.displayDateTime.minutes)}"
        val tempStr = "${forecastHour.temperature.degrees.toInt()}°"

        holder.time.text = timeStr
        holder.temp.text = tempStr
    }

    override fun getItemCount(): Int {
        return hourlyList.size
    }

    class HourlyViewHolder( views : View) : RecyclerView.ViewHolder(views) {
        val time : TextView = views.findViewById(R.id.hourlytime)
        val temp : TextView = views.findViewById(R.id.hourlytemp)
    }

}