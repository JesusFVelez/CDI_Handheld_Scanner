package com.example.cdihandheldscannerviewactivity.BinsWIthProduct

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdihandheldscannerviewactivity.Network.ScannerAPI
import com.example.cdihandheldscannerviewactivity.Network.WarehouseInfo
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.lang.Exception

class BinsWithProductViewModel :ViewModel(){

    private val _wasLastAPICallSuccessful = MutableLiveData<Boolean>()
    val wasLastAPICallSuccessful : LiveData<Boolean>
        get() = _wasLastAPICallSuccessful

    private val _isSpinnerArrowUp =  MutableLiveData<Boolean>()
    val isSpinnerArrowUp : LiveData<Boolean>
        get() = _isSpinnerArrowUp

    private val _listOfWarehouses = MutableLiveData<List<WarehouseInfo>>()
    val listOfWarehouses : LiveData<List<WarehouseInfo>>
        get() = _listOfWarehouses

    private val _currentWarehouseNumber = MutableLiveData<Int>()
    val currentWarehouseNumber: LiveData<Int>
        get() = _currentWarehouseNumber

    private val _companyIDOfUser = MutableLiveData<String>()
    val companyIDOfUser : LiveData<String>
        get() = _companyIDOfUser


    init {
        getWarehousesFromBackendForSpinner()
        _isSpinnerArrowUp.value = false
    }

    // Function to set the state of the spinner arrow
    fun setIsSpinnerArrowUp(isSpinnerUp: Boolean){
        _isSpinnerArrowUp.value = isSpinnerUp
    }

    fun getWarehousesFromBackendForSpinner(){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("get Warehouses API Call" , "Error -> ${exception.message}")
        }

        // API call to get list of warehouses
        viewModelScope.launch (exceptionHandler) {
            try{
                val response = ScannerAPI.retrofitService.getWarehousesAvailable()
                _listOfWarehouses.value = response.response.warehouses.warehouses
                _wasLastAPICallSuccessful.value = true
            }catch (e: Exception){
                _wasLastAPICallSuccessful.value = false
                Log.i("Products In Bin View Model WH API Call", "Error -> ${e.message}")
            }
        }
    }

    fun setCompanyIDFromSharedPref(companyID: String){
        _companyIDOfUser.value = companyID
    }
}
