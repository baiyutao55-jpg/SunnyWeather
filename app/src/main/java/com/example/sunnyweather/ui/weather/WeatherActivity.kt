package com.example.sunnyweather.ui.weather

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.example.sunnyweather.R
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

    if(viewModel.locationLng.isEmpty()){
        viewModel.locationLng=intent.getStringExtra("location_lng")?:""
    }
        if(viewModel.locationLat.isEmpty()){
            viewModel.locationLat=intent.getStringExtra("location_lat")?:""
        }

        if(viewModel.placeName.isEmpty()){
            viewModel.placeName=intent.getStringExtra("place_name")?:""
        }

      //  Log.e("DEB","${ viewModel.locationLng}::${viewModel.locationLat}::${viewModel.placeName}")

     viewModel.weatherLiveData.observe(this, Observer{
         result ->
         val weather=result.getOrNull()
         if(weather!=null){
             showWeatherInfo(weather)
         }else
         {
             Toast.makeText(this,"无法成功获取天气信息", Toast.LENGTH_SHORT).show()
             result.exceptionOrNull()?.printStackTrace()
         }
     }
         )
viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
   }


    @SuppressLint("SuspiciousIndentation")
    private fun showWeatherInfo(weather: Weather){
val placeName=findViewById<TextView>(R.id.placeName)
    placeName.text=viewModel.placeName
    val realtime=weather.realtime
    val daily=weather.daily
    val currentTempText="${realtime.temperature.toInt()}℃"

    val currentTemp=findViewById<TextView>(R.id.currentTemp)
    currentTemp.text=currentTempText

    val currentSky=findViewById<TextView>(R.id.currentSky)
    currentSky.text= getSky(realtime.skycon).info

    val currentPM25Text="空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
    val currentAQI=findViewById<TextView>(R.id.currentAQI)
    currentAQI.text=currentPM25Text

    val nowLayout=findViewById<RelativeLayout>(R.id.nowLayout)
    nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

    val forecastLayout=findViewById<LinearLayout>(R.id.forecastLayout)
      forecastLayout.removeAllViews()

        val weatherLayout=findViewById<ScrollView>(R.id.weatherLayout)
        val days=daily.skycon.size

        for(i in 0 until days){

            val skycon=daily.skycon[i]
            val temperature=daily.temperature[i]
            val view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false)
            val dateInfo=view.findViewById(R.id.dateInfo) as TextView
            val skyIcon=view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo=view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo=view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat= SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())
            dateInfo.text=simpleDateFormat.format(skycon.date)
            val sky=getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text=sky.info

            val tempText="${temperature.min.toInt()}~${temperature.max.toInt()}℃"
            temperatureInfo.text=tempText
            forecastLayout.addView(view)
        }
val lifeIndex=daily.lifeIndex

        val coldRiskText=findViewById(R.id.coldRiskText) as TextView
        val dressingText=findViewById(R.id.dressingText) as TextView

        val ultravioletText=findViewById(R.id.ultravioletText) as TextView
        val carWashingText=findViewById(R.id.carWashingText) as TextView
      Log.e("DEB","desc:${lifeIndex.coldRisk[0].desc}")
        Log.e("DEB","desc:${lifeIndex.carWashing[0].desc}")
        coldRiskText.text=lifeIndex.coldRisk[0].desc
        dressingText.text=lifeIndex.dressing[0].desc
        ultravioletText.text=lifeIndex.ultraviolet[0].desc
        carWashingText.text=lifeIndex.carWashing[0].desc

        weatherLayout.visibility=View.VISIBLE

    }


    }
