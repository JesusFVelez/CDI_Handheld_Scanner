package com.example.cdihandheldscannerviewactivity.MovingProductsBetweenBins

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import com.example.cdihandheldscannerviewactivity.Utils.Network.WarehouseInfo
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.lang.Exception

class MovingProductsBetweenBinsViewModel: ViewModel() {
    // LiveData and MutableLiveData for various UI states and data
    private val _wasLastAPICallSuccessful = MutableLiveData<Boolean>()
    val wasLastAPICallSuccessful : LiveData<Boolean>
        get() = _wasLastAPICallSuccessful

    private val _listOfWarehouses = MutableLiveData<List<WarehouseInfo>>()
    val listOfWarehouses : LiveData<List<WarehouseInfo>>
        get() = _listOfWarehouses

    // Function to fetch the list of warehouses from the backend for the spinner
    fun getWarehousesFromBackendForSpinner(){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("get Warehouses API Call " , "Error -> ${exception.message}")
        }

        // API call to get list of warehouses
        viewModelScope.launch (exceptionHandler) {
            try{
                val response = ScannerAPI.getGeneralService().getWarehousesAvailable()
                _listOfWarehouses.value = response.response.warehouses.warehouses
                _wasLastAPICallSuccessful.value = true
            }catch (e: Exception){
                _wasLastAPICallSuccessful.value = false
                Log.i("Get Warehouses API call (e)", "Error -> ${e.message}")
            }
        }
    }

    init{

    }



}