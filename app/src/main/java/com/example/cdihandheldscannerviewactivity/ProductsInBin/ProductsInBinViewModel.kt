package com.example.cdihandheldscannerviewactivity.ProductsInBin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdihandheldscannerviewactivity.Utils.Network.ProductInBinInfo
import com.example.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import com.example.cdihandheldscannerviewactivity.Utils.Network.WarehouseInfo
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.lang.Exception
// ViewModel class for managing Products in a Bin
class ProductsInBinViewModel: ViewModel() {

    // LiveData and MutableLiveData for various UI states and data
    private val _wasLastAPICallSuccessful = MutableLiveData<Boolean>()
    val wasLastAPICallSuccessful : LiveData<Boolean>
        get() = _wasLastAPICallSuccessful

    private val _wasBinFound = MutableLiveData<Boolean>()
    val wasBinFound : LiveData<Boolean>
        get() = _wasBinFound

    private val _listOfProducts = MutableLiveData<List<ProductInBinInfo>>()
    val listOfProducts : LiveData<List<ProductInBinInfo>>
        get() = _listOfProducts

    private val _isSpinnerArrowUp =  MutableLiveData<Boolean>()
    val isSpinnerArrowUp : LiveData<Boolean>
        get() = _isSpinnerArrowUp

    private val _listOfWarehouses = MutableLiveData<List<WarehouseInfo>>()
    val listOfWarehouses : LiveData<List<WarehouseInfo>>
        get() = _listOfWarehouses

    private val _currentWarehouseNumber = MutableLiveData<Int>()
    val currentWarehouseNumber: LiveData<Int>
        get() = _currentWarehouseNumber

    private val _numberOfItemsInBin = MutableLiveData<Int>()
    val numberOfItemsInBin : LiveData<Int>
        get() = _numberOfItemsInBin

    private val _currentlyChosenAdapterPosition = MutableLiveData<Int>()
    val currentlyChosenAdapterPosition: LiveData<Int>
        get() = _currentlyChosenAdapterPosition

    private val _companyIDOfUser = MutableLiveData<String>()
    val companyIDOfUser : LiveData<String>
        get() = _companyIDOfUser


    // Function to set the currently chosen adapter position
    fun setChosenAdapterPosition(position : Int){
        if (position >= 0)
            _currentlyChosenAdapterPosition.value = position
        else
            _currentlyChosenAdapterPosition.value = 0
    }

    // Initialization block
    init {
        getWarehousesFromBackendForSpinner()
        _isSpinnerArrowUp.value = false
        _currentWarehouseNumber.value = 0
        _numberOfItemsInBin.value = 0
        _currentlyChosenAdapterPosition.value = 0

    }



    // Function called when ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
    }

    // Function to clear the list of products
    fun clearListOfProducts(){
        _listOfProducts.value = listOf()
        _numberOfItemsInBin.value = 0
    }



    // Function to set the state of the spinner arrow
    fun setIsSpinnerArrowUp(isSpinnerUp: Boolean){
        _isSpinnerArrowUp.value = isSpinnerUp
    }

    // Function to set the current warehouse number based on the selected warehouse name
    private fun setWarehouseNumber(selectedWarehouseInSpinner : String){
        for(aWarehouse in listOfWarehouses.value!!){
            if (selectedWarehouseInSpinner == aWarehouse.warehouseName){
                _currentWarehouseNumber.value = aWarehouse.warehouseNumber.toInt()
            }
        }
    }

    // Function to set the company ID from shared preferences
    fun setCompanyIDFromSharedPref(companyID: String){
        _companyIDOfUser.value = companyID
    }

    // Function to fetch product information from the backend
    fun getProductInfoFromBackend(warehouseName : String, binNumber : String ){
        setWarehouseNumber(warehouseName)

        // Exception handler for API call
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Products In Bin View Model Product Info API Call" , "Error -> ${exception.message}")
        }

        // API call to get product information
        try{
            viewModelScope.launch(exceptionHandler) {
                val response = ScannerAPI.retrofitService.getAllItemsInBin(_companyIDOfUser.value!!, _currentWarehouseNumber.value!!, binNumber )
                _wasLastAPICallSuccessful.value = true
                _listOfProducts.value = response.response.itemsInBin.itemsInBin
                _wasBinFound.value = response.response.wasBinFound
                _numberOfItemsInBin.value = _listOfProducts.value!!.size

                Log.i("Products In Bin View Model Product Info API Call", "Response -> ${response.toString()}" )
            }
        }catch (e : Exception){
            _wasLastAPICallSuccessful.value = false
            Log.i("Products In Bin View Model Product Info API Call" , "Error -> ${e.message}")
        }
    }

    // Function to fetch the list of warehouses from the backend for the spinner
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
}
