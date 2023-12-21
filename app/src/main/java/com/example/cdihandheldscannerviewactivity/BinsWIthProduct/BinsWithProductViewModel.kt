package com.example.cdihandheldscannerviewactivity.BinsWIthProduct

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdihandheldscannerviewactivity.Utils.Network.BinInfo
import com.example.cdihandheldscannerviewactivity.Utils.Network.ItemDetailsForBinSearch
import com.example.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import com.example.cdihandheldscannerviewactivity.Utils.Network.WarehouseInfo
import com.example.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.util.Scanner
import kotlin.Exception

class BinsWithProductViewModel :ViewModel(){


    // LiveData for getItemDetailsForBinSearch API Call
    private val _itemDetails = MutableLiveData<List<ItemDetailsForBinSearch>>()
    val itemDetails: LiveData<List<ItemDetailsForBinSearch>>
        get() = _itemDetails

    private val _isBarCodeValid = MutableLiveData<Boolean>()
    val isBarCodeValid: LiveData<Boolean>
        get() = _isBarCodeValid

    private val _barcodeErrorMessage = MutableLiveData<String>()
    val barcodeErrorMessage: LiveData<String>
        get() = _barcodeErrorMessage


    // LiveData for getBinsThatHaveItem API Call

    private val _listOfBinsThatHaveProduct = MutableLiveData<List<BinInfo>>()
    val listOfBinsThatHaveProduct: LiveData<List<BinInfo>>
        get() = _listOfBinsThatHaveProduct

    private val _hasBinBeenFoundWithItem = MutableLiveData<Boolean>()
    val hasBinBeenFoundWithItem: LiveData<Boolean>
        get() = _hasBinBeenFoundWithItem

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
        _currentWarehouseNumber.value = 0
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
    fun getWarehousesFromBackendForSpinner(){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("get Warehouses API Call" , "Error -> ${exception.message}")
        }

        // API call to get list of warehouses
        viewModelScope.launch (exceptionHandler) {
            try{
                val response = ScannerAPI.getGeneralService().getWarehousesAvailable()
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

    fun getItemDetailsForBinSearchFromBackend(warehouseName: String, scannedBarCode: String){
        setWarehouseNumber(warehouseName)
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("~get item details for Bin search API Call Exception Handler" , "Error -> ${exception.message}")
        }
        viewModelScope.launch (exceptionHandler){
            try{
                val response = ScannerAPI.getViewBinsThatHaveItemService().getItemDetailsForBinSearch(_companyIDOfUser.value!!, _currentWarehouseNumber.value!!, scannedBarCode)
                _barcodeErrorMessage.value = response.response.barcodeErrorMessage
                _isBarCodeValid.value = response.response.isBarCodeValid
                _wasLastAPICallSuccessful.value = true
                _itemDetails.value = response.response.itemDetailsForBinSearch.itemDetailsForBinSearch
                Log.i("get item details for Bin search API Call", "Item Details -> ${_itemDetails.value.toString()}")
            }catch (e: Exception){
                _wasLastAPICallSuccessful.value = false
                Log.i("get item details for Bin search API Call", "Error -> ${e.message}")
            }
        }
    }


    fun getBinsThatHaveProductFromBackend( warehouseName: String, itemNumber: String){
        setWarehouseNumber(warehouseName)
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("get Bins that have item API Call" , "Error -> ${exception.message}")
        }
        viewModelScope.launch (exceptionHandler) {
            try{
                val response = ScannerAPI.getViewBinsThatHaveItemService().getAllBinsThatHaveProduct(_companyIDOfUser.value!!, _currentWarehouseNumber.value!!, itemNumber)
                _listOfBinsThatHaveProduct.value = response.response.binsThatHaveItem.binsThatHaveItem
                _hasBinBeenFoundWithItem.value = response.response.hasBinBeenFoundWithItem
                _wasLastAPICallSuccessful.value = true
                Log.i("get Bins that have item API Call" , "Bins that have item -> ${_listOfBinsThatHaveProduct.value.toString()}")
            }catch (e: Exception){
                _wasLastAPICallSuccessful.value = false
                Log.i("get Bins that have item API Call" , "Error -> ${e.message}")
            }
        }

    }
}
