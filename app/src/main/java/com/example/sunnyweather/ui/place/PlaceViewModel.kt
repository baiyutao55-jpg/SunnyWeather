package com.example.sunnyweather.ui.place

import com.example.sunnyweather.logic.Repository
import com.example.sunnyweather.logic.model.Place
import androidx.lifecycle.*

class PlaceViewModel: ViewModel() {
    private val searchLiveData= MutableLiveData<String>()
    val placeList= ArrayList<Place>()
    val placeLiveData = searchLiveData.switchMap { query->
        Repository.serachPlaces(query)
    }


    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }


}