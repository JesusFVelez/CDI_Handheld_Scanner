package com.comdist.cdihandheldscannerviewactivity.ProductsInBin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.ProductInBinInfo
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
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
