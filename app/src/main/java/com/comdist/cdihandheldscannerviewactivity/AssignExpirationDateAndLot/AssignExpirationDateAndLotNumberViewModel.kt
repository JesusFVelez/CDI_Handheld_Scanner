package com.comdist.cdihandheldscannerviewactivity.AssignExpirationDateAndLot

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemData
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemInfo
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class AssignExpirationDateAndLotNumberViewModel: ViewModel(){

    private val _opSuccess = MutableLiveData<Boolean>()
    val opSuccess : LiveData<Boolean>
        get() = _opSuccess

    private val _opMessage = MutableLiveData<String>()
    val opMessage : LiveData<String>
        get() = _opMessage

    private val _itemInfo = MutableLiveData<List<ItemInfo>>()
    val itemInfo : LiveData<List<ItemInfo>>
        get() = _itemInfo

    private val _wasLastAPICallSuccessful = MutableLiveData<Boolean>()
    val wasLastAPICallSuccessful : LiveData<Boolean>
        get() = _wasLastAPICallSuccessful

    private val _suggestions = MutableLiveData<List<ItemData>>()
    val suggestions: LiveData<List<ItemData>>
        get() = _suggestions

    private val _companyID =  MutableLiveData<String>()
    val companyID: LiveData<String>
        get() = _companyID

    private val _warehouseNO =  MutableLiveData<String>()
    val warehouseNO: LiveData<String>
        get() = _warehouseNO

    private val _currentlyChosenItemForSearch = MutableLiveData<ItemData>()
    val currentlyChosenItemForSearch: LiveData<ItemData>
        get() = _currentlyChosenItemForSearch
    init{}

    fun assignExpirationDate(pItemNumber: String, pBinLocation: String, pExpireDate: String, pLotNumber: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Assign Expire Date " , "Error -> ${exception.message}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getAssignExpirationDateService().assignExpireDate(pItemNumber, pBinLocation, pExpireDate, pLotNumber)
                _wasLastAPICallSuccessful.value = true
                _opMessage.value = response.response.opMessage
                _opSuccess.value = response.response.opSuccess
            } catch (e: Exception) {
                _wasLastAPICallSuccessful.value = false
                Log.i("Assign Expire Date (e)", "Error -> ${e.message}")
            }
        }
    }

    fun setCompanyIDFromSharedPref(companyID: String){
        _companyID.value = companyID
    }
    fun setWarehouseNOFromSharedPref(warehouseNO: Int){
        _warehouseNO.value = warehouseNO.toString()
    }
    fun assignLotNumber(pItemNumber: String, pWarehouseNo:Int, pBinLocation: String, pLotNumber: String, pCompanyCode: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            _opMessage.value = "Exception occurred: ${exception.localizedMessage}"
            Log.e("AssignLotNumber", "Exception -> ${exception.localizedMessage}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                // Assuming the Retrofit suspend function returns directly the response body
                val response = ScannerAPI.getAssignLotNumberService().assignLotNumberToBinItem(pItemNumber,pCompanyCode, pBinLocation, pLotNumber ,pWarehouseNo)
                _opSuccess.value = response.response.opSuccess
                _opMessage.value = response.response.opMessage
                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                // Exception handling for network errors or serialization/deserialization issues
                _wasLastAPICallSuccessful.value = false
                Log.e("AssignLotNumber", "Exception -> ${e.localizedMessage}")
            }
        }
    }



    fun getItemInfo(pItemNumber: String, pBinLocation: String, pLotNumber: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Item Info", "Error -> ${exception.message}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getAssignExpirationDateService().getItemInformation(pItemNumber, pBinLocation, pLotNumber)
                _wasLastAPICallSuccessful.value = true
                _opMessage.value = response.response.opMessage
                _itemInfo.value = response.response.binItemInfo.response
                _opSuccess.value = response.response.opSuccess
            } catch (e: Exception) {
                _wasLastAPICallSuccessful.value = false
                Log.i("Item Info", "Error -> ${e.message}")
            }
        }
    }

    fun setCurrentlyChosenItemForSearch(chosenItem: ItemData){
        _currentlyChosenItemForSearch.value = chosenItem
    }

    fun fetchItemSuggestions(binLocation: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.e("ViewModel", "Failed to fetch item suggestions: ${exception.localizedMessage}")
        }

        try {
            viewModelScope.launch(exceptionHandler) {
                val response = ScannerAPI.getAssignExpirationDateService()
                    .getSuggestionsForItemOrBin(binLocation)
                _suggestions.value = response.response.binItemInfo.binItemInfo
                _wasLastAPICallSuccessful.value = true
            }
        }catch (e: Exception){
            _wasLastAPICallSuccessful.value = false
            Log.i("Get All item for suggestions - Assign Lot Number & Exp Date (e) " , "Error -> ${e.message}")
        }
    }
}