package com.scannerapp.cdihandheldscannerviewactivity.EditItem.EditBarcode

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemData
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemInfo
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.util.Scanner

class EditBarcodeViewModelFactory(private val someParameter: ItemData? = null) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditBarcodeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditBarcodeViewModel(someParameter) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



class EditBarcodeViewModel(itemInfo: ItemData?): ViewModel() {
    private val _mainBarcode = MutableLiveData<String?>()
    val mainBarcode: LiveData<String?>
        get() = _mainBarcode

    private val _itemInfo = MutableLiveData<ItemData?>()
    val itemInfo: LiveData<ItemData?>
        get() = _itemInfo

    private val _companyID =  MutableLiveData<String>()
    val companyID: LiveData<String>
        get() = _companyID

    private val _warehouseNumber =  MutableLiveData<String>()
    val warehouseNO: LiveData<String>
        get() = _warehouseNumber

    private val _listOfBarcodesOfItem = MutableLiveData<List<String>?>()
    val listOfBarcodesOfItem: LiveData<List<String>?>
        get() = _listOfBarcodesOfItem

    private val _networkErrorMessage = MutableLiveData<String>()
    val networkErrorMessage: LiveData<String>
        get() = _networkErrorMessage

    private val _couldAddOrRemoveBarcode = MutableLiveData<Boolean>()
    val couldAddOrRemoveBarcode: LiveData<Boolean>
        get() = _couldAddOrRemoveBarcode

    // Error Message
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    // API Call Confirmation
    private val _wasLastAPICallSuccessful = MutableLiveData<Boolean>()
    val wasLastAPICallSuccessful : LiveData<Boolean>
        get() = _wasLastAPICallSuccessful


    fun setCompanyIDFromSharedPref(companyID: String){
        _companyID.value = companyID
    }

    fun setWarehouseNumberFromSharedPref(warehouseNO: Int){
        _warehouseNumber.value = warehouseNO.toString()
    }

    init {
        _itemInfo.value = itemInfo
    }

    fun getBarcodesFromBackend(){
            val exceptionHandler = CoroutineExceptionHandler { _, exception ->
                _networkErrorMessage.value = exception.message
                //_wasLastAPICallSuccessful.value = false
                Log.i("Get Barcodes" , "Error -> ${exception.message}")
            }

            viewModelScope.launch(exceptionHandler) {
                try {
                    // Assuming the Retrofit suspend function returns directly the response body
                    val response = ScannerAPI.getEditItemService().BarcodeService.getBarcodesForItem(_itemInfo.value!!.itemNumber, _companyID.value!!)
                    _mainBarcode.value = response.response.mainBarcode
                    _listOfBarcodesOfItem.value = response.response.listOfBarcodes
                    _wasLastAPICallSuccessful.value = true
                } catch (e: Exception) {
                    // Exception handling for network errors or serialization/deserialization issues
                    _networkErrorMessage.value = e.message
                    _wasLastAPICallSuccessful.value = false
                    Log.e("Get Barcodes", "Exception (e) -> ${e.localizedMessage}")
                }
            }
    }

    fun addBarcodeToBackend(barcodeToAdd: String, isMainBarcode: Boolean){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            //_wasLastAPICallSuccessful.value = false
            Log.i("Assign Barcode ", "Error -> ${exception.message}")
        }
        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getEditItemService().BarcodeService.addBarcodeToItem(
                    _itemInfo.value!!.itemNumber,
                    _companyID.value!!,
                    barcodeToAdd
                )
                _errorMessage.value = response.response.errorMessage
                _couldAddOrRemoveBarcode.value = response.response.couldAddBarcode
                _wasLastAPICallSuccessful.value = true

            }catch (e:Exception){
                _networkErrorMessage.value = e.message
                _wasLastAPICallSuccessful.value = false
                Log.e("Assign Barcode", "Exception (e) -> ${e.localizedMessage}")
            }
            }
    }

    fun updateBarcodeToBackend(oldBarcode:String ,newBarcode: String, isMainBarcode: Boolean){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            //_wasLastAPICallSuccessful.value = false
            Log.i("Update Barcode ", "Error -> ${exception.message}")
        }
        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getEditItemService().BarcodeService.updateBarcodeOfItem(
                    _itemInfo.value!!.itemNumber,
                    _companyID.value!!,
                    isMainBarcode,
                    oldBarcode,
                    newBarcode
                )
                _errorMessage.value = response.response.errorMessage
                _couldAddOrRemoveBarcode.value = response.response.couldUpdateBarcode
                _wasLastAPICallSuccessful.value = true

            }catch (e:Exception){
                _networkErrorMessage.value = e.message
                _wasLastAPICallSuccessful.value = false
                Log.e("Update Barcode", "Exception (e) -> ${e.message}")
            }
        }

    }

    fun removeBarcodeFromBackend(barcodeToRemove: String, isMainBarcode: Boolean){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            //_wasLastAPICallSuccessful.value = false
            Log.i("Remove Barcode ", "Error -> ${exception.message}")
        }
        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getEditItemService().BarcodeService.removeBarcodeFromItem(
                    _itemInfo.value!!.itemNumber,
                    _companyID.value!!,
                    barcodeToRemove
                )
                _errorMessage.value = response.response.errorMessage
                _couldAddOrRemoveBarcode.value = response.response.couldRemoveBarcode
                _wasLastAPICallSuccessful.value = true
            }catch (e:Exception){
                _networkErrorMessage.value = e.message
                _wasLastAPICallSuccessful.value = false
                Log.e("Remove Barcode", "Exception (e) -> ${e.localizedMessage}")
            }
        }
    }




}