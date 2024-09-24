package com.scannerapp.cdihandheldscannerviewactivity.ProductsInBin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ProductInBinInfo
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
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


    // Error Message
    private val _errorMessage = MutableLiveData<MutableMap<String,String>>()
    val errorMessage : LiveData<MutableMap<String,String>>
        get() = _errorMessage


    private val _numberOfItemsInBin = MutableLiveData<Int>()
    val numberOfItemsInBin : LiveData<Int>
        get() = _numberOfItemsInBin

    private val _currentlyChosenAdapterPosition = MutableLiveData<Int>()
    val currentlyChosenAdapterPosition: LiveData<Int>
        get() = _currentlyChosenAdapterPosition

    private val _companyIDOfUser = MutableLiveData<String>()
    val companyIDOfUser : LiveData<String>
        get() = _companyIDOfUser

    private val _warehouseNumberOfUser = MutableLiveData<Int>()
    val warehouseNumberOfUser: LiveData<Int>
        get() = _warehouseNumberOfUser


    // Function to set the currently chosen adapter position
    fun setChosenAdapterPosition(position : Int){
        if (position >= 0)
            _currentlyChosenAdapterPosition.value = position
        else
            _currentlyChosenAdapterPosition.value = 0
    }

    // Initialization block
    init {
        _numberOfItemsInBin.value = 0
        _currentlyChosenAdapterPosition.value = 0

        _errorMessage.value = mutableMapOf("getItemsInBin" to "")

    }


    // Function to clear the list of products
    fun clearListOfProducts(){
        _listOfProducts.value = listOf()
        _numberOfItemsInBin.value = 0
    }


    // Function to set the company ID from shared preferences
    fun setCompanyIDFromSharedPref(companyID: String){
        _companyIDOfUser.value = companyID
    }

    fun setWarehouseNumberFromSharedPref(warehouseNumber: Int){
        _warehouseNumberOfUser.value = warehouseNumber
    }


    fun getPaginatedItemsInBin(binNumber: String, pageSize:Int, pageNumber: Int){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Products In Bin View Model Product Info API Call" , "Error -> ${exception.message}")
        }
        try{
            viewModelScope.launch(exceptionHandler) {
                val response = ScannerAPI.getViewProductsInBinService().getPaginatedItemsInBin(_companyIDOfUser.value!!,
                                                                                                _warehouseNumberOfUser.value!!,
                                                                                                binNumber,
                                                                                                pageSize = pageSize,
                                                                                                pageNumber = pageNumber )
                _errorMessage.value!!["getItemsInBin"] = response.response.errorMessage
                _wasLastAPICallSuccessful.value = true
                _listOfProducts.value = response.response.itemsInBin.itemsInBin


                Log.i("Products In Bin View Model Product Info API Call", "Response -> ${response.toString()}" )
            }
        }catch (e : Exception) {
            _wasLastAPICallSuccessful.value = false
            Log.i("Products In Bin View Model Product Info", "Error (e) -> ${e.message}")
        }
    }

    // Function to fetch product information from the backend
    fun getProductInfoFromBackend(binNumber : String ){

        // Exception handler for API call
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Products In Bin View Model Product Info API Call" , "Error -> ${exception.message}")
        }

        // API call to get product information
        try{
            viewModelScope.launch(exceptionHandler) {
                val response = ScannerAPI.getViewProductsInBinService().getAllItemsInBin(_companyIDOfUser.value!!, _warehouseNumberOfUser.value!!, binNumber )
                _wasLastAPICallSuccessful.value = true
                _listOfProducts.value = response.response.itemsInBin.itemsInBin
                _errorMessage.value!!["getItemsInBin"] = response.response.errorMessage
                _wasBinFound.value = response.response.wasBinFound
                _numberOfItemsInBin.value = _listOfProducts.value!!.size
                Log.i("Products In Bin View Model Product Info API Call", "Response -> ${response.toString()}" )
            }
        }catch (e : Exception){
            _wasLastAPICallSuccessful.value = false
            Log.i("Products In Bin View Model Product Info API Call" , "Error -> ${e.message}")
        }
    }

}
