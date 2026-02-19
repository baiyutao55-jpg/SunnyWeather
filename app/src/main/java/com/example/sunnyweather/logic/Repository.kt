package com.example.sunnyweather.logic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.Dispatcher

object Repository {
    fun serachPlaces(query: String)= liveData(Dispatchers.IO) {
        val result=try {
            val placeResponse= SunnyWeatherNetwork.searchPlaces(query)
            if(placeResponse.status=="ok"){
                val places=placeResponse.places
                Result.success(places)
            }
            else{
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        }
        catch (e: Exception){
            Result.failure<List<Place>>(e)
        }
        emit(result)
    }


    fun refreshWeather(lng: String,lat: String)= liveData(Dispatchers.IO){


        val result=try{

            coroutineScope {
                val deferredRealtime = async {
                    SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
                }
                val deferredDaily = async {
                    SunnyWeatherNetwork.getDailyWeather(lng, lat)
                }



                val realtimeResponse=deferredRealtime.await()
              //  Log.e("DEB","realtimeResponse:::${lng},${lat},${realtimeResponse.result.realtime.airQuality.aqi.chn.toInt()}")

                val dailyResponse=deferredDaily.await()
              //  Log.e("DEB","dailyResponse="+dailyResponse.status)



                if(realtimeResponse.status=="ok" && dailyResponse.status=="ok"){
                   // Log.e("DEB","realtimeResponse="+realtimeResponse.result.realtime.toString())
                    val weather= Weather(realtimeResponse.result.realtime,dailyResponse.result.daily)
                    Result.success(weather)
                } else{
                    Result.failure(
                        RuntimeException(
                            "realtime response status is ${realtimeResponse.status}"+"daily response status is ${dailyResponse.status}"
                        )
                    )

                }
            }

        } catch (e: Exception){
           // Log.e("DEB","Result.failure="+e.toString())
            Result.failure<Weather>(e)

        }
        emit(result)
    }
}