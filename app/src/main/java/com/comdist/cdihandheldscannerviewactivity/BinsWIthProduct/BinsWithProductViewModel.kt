package com.comdist.cdihandheldscannerviewactivity.BinsWIthProduct

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.BinInfo
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.ItemDetailsForBinSearch
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
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



    private val _companyIDOfUser = MutableLiveData<String>()
    val companyIDOfUser : LiveData<String>
        get() = _companyIDOfUser

    private val _warehouseNumberOfUser = MutableLiveData<Int>()
    val warehouseNumberOfUser : LiveData<Int>
        get() = _warehouseNumberOfUser


    init {
    }


    fun setCompanyIDFromSharedPref(companyID: String){
        _companyIDOfUser.value = companyID
    }

    // Function to set the current warehouse number based on the selected warehouse name
    fun setWarehouseNumberFromSharedPref(warehouseNumber: Int){
        _warehouseNumberOfUser.value = warehouseNumber
    }

    fun getItemDetailsForBinSearchFromBackend(scannedBarCode: String){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("~get item details for Bin search API Call Exception Handler" , "Error -> ${exception.message}")
        }
        viewModelScope.launch (exceptionHandler){
            try{
                val response = ScannerAPI.getViewBinsThatHaveItemService().getItemDetailsForBinSearch(_companyIDOfUser.value!!, _warehouseNumberOfUser.value!!, scannedBarCode)
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


    fun getBinsThatHaveProductFromBackend(itemNumber: String){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("get Bins that have item API Call" , "Error -> ${exception.message}")
        }
        viewModelScope.launch (exceptionHandler) {
            try{
                val response = ScannerAPI.getViewBinsThatHaveItemService().getAllBinsThatHaveProduct(_companyIDOfUser.value!!, _warehouseNumberOfUser.value!!, itemNumber)
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
