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

    private val _listOfBarcodesOfItem = MutableLiveData<List<String>>()
    val listOfBarcodesOfItem: LiveData<List<String>>
        get() = _listOfBarcodesOfItem

    fun setCompanyIDFromSharedPref(companyID: String){
        _companyID.value = companyID
    }

    fun setWarehouseNumberFromSharedPref(warehouseNO: Int){
        _warehouseNumber.value = warehouseNO.toString()
    }

    init {
//        _mainBarcode.value = null
        _itemInfo.value = itemInfo
    }

    fun getBarcodesFromBackend(){
            val exceptionHandler = CoroutineExceptionHandler { _, exception ->
                //_wasLastAPICallSuccessful.value = false
                Log.i("Assign Expire Date " , "Error -> ${exception.message}")
            }

            viewModelScope.launch(exceptionHandler) {
                try {
                    // Assuming the Retrofit suspend function returns directly the response body
                    val response = ScannerAPI.getEditItemService().BarcodeService.getBarcodesForItem(_itemInfo.value!!.itemNumber, _companyID.value!!)
                    _mainBarcode.value = response.response.mainBarcode
                    _listOfBarcodesOfItem.value = response.response.listOfBarcodes

                } catch (e: Exception) {
                    // Exception handling for network errors or serialization/deserialization issues
//                    _wasLastAPICallSuccessful.value = false
                    Log.e("AssignLotNumber", "Exception -> ${e.localizedMessage}")
                }
            }
    }


}