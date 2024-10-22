package com.scannerapp.cdihandheldscannerviewactivity.EditItem

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemData
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemInfo
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class EditItemViewModel: ViewModel(){

    private val _networkErrorMessage = MutableLiveData<String>()
    val networkErrorMessage : LiveData<String>
        get() = _networkErrorMessage

    private val _opSuccess = MutableLiveData<Boolean>()
    val opSuccess : LiveData<Boolean>
        get() = _opSuccess

    private val _opMessage = MutableLiveData<String>()
    val opMessage : LiveData<String>
        get() = _opMessage

    private val _itemInfo = MutableLiveData<ItemInfo>()
    val itemInfo : LiveData<ItemInfo>
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

    private val _itemsInBinFromBarcode = MutableLiveData<List<ItemData>>()
    val itemsInBinFromBarcode: LiveData<List<ItemData>>
        get() = _itemsInBinFromBarcode

    init {
        _networkErrorMessage.value = ""
    }

    fun getItemsInBinFromBarcode(pItemNumber: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            Log.e("ViewModel", "Failed to fetch items in bin from barcode: ${exception.localizedMessage}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getAssignExpirationDateService()
                    .getItemsInBinFromBarcode(pItemNumber)

                // Convert MultiBarcodeItemData to ItemData
                val convertedItems = response.response.binItemInfo.binItemInfo.map {
                    ItemData(
                        itemNumber = it.itemNumber,
                        itemDescription = it.itemDescription,
                        binLocation = it.binLocation,
                        expireDate = it.expireDate,
                        lotNumber = it.lotNumber,
                        barCode = it.barCode,
                        vendorNumber = it.vendorNumber
                    )
                }

                _itemsInBinFromBarcode.value = convertedItems
                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                _networkErrorMessage.value = e.message
                _wasLastAPICallSuccessful.value = false
                Log.e("ViewModel", "Exception -> ${e.localizedMessage}")
            }
        }
    }

    fun assignExpirationDate(pItemNumber: String, pBinLocation: String, pExpireDate: String, pLotNumber: String, pWarehouseNo: Int) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            Log.i("Assign Expire Date", "Error -> ${exception.message}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getAssignExpirationDateService()
                    .assignExpireDate(pItemNumber, pBinLocation, pExpireDate, pLotNumber, pWarehouseNo)
                _wasLastAPICallSuccessful.value = response.response.opSuccess
                _opMessage.value = response.response.opMessage
                _opSuccess.value = response.response.opSuccess
            } catch (e: Exception) {
                _networkErrorMessage.value = e.message
                _wasLastAPICallSuccessful.value = false
                Log.i("Assign Expire Date", "Error -> ${e.message}")
            }
        }
    }

    fun setCompanyIDFromSharedPref(companyID: String){
        _companyID.value = companyID
    }

    fun setWarehouseNOFromSharedPref(warehouseNO: Int){
        _warehouseNO.value = warehouseNO.toString()
    }

    fun getItemInfo(pItemNumber: String, pBinLocation: String, pLotNumber: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            Log.i("Item Info", "Error -> ${exception.message}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getAssignExpirationDateService()
                    .getItemInformation(pItemNumber, pBinLocation, pLotNumber)
                _wasLastAPICallSuccessful.value = true

                val itemInfoList = response.response.binItemInfo.response
                if (!itemInfoList.isNullOrEmpty()) {
                    _itemInfo.value = itemInfoList[0]
                } else {
                    _networkErrorMessage.value = "No item information available."
                    _wasLastAPICallSuccessful.value = false
                    Log.i("Item Info", "Error -> No item information available.")
                }
            } catch (e: Exception) {
                _networkErrorMessage.value = e.message
                _wasLastAPICallSuccessful.value = false
                Log.i("Item Info", "Error -> ${e.message}")
            }
        }
    }

    fun setCurrentlyChosenItemForSearch(chosenItem: ItemData){
        _currentlyChosenItemForSearch.value = chosenItem
    }

    fun fetchItemSuggestions(filter: String = "") {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            Log.e("ViewModel", "Failed to fetch item suggestions: ${exception.localizedMessage}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getAssignExpirationDateService()
                    .getSuggestionsForItemOrBin()
                val filteredSuggestions = if (filter.isNotEmpty()) {
                    response.response.binItemInfo.binItemInfo.filter {
                        it.itemNumber.contains(filter, ignoreCase = true) ||
                                it.barCode?.contains(filter, ignoreCase = true) == true
                    }
                } else {
                    response.response.binItemInfo.binItemInfo
                }
                _suggestions.value = filteredSuggestions
            } catch (e: Exception) {
                _networkErrorMessage.value = e.message
                _wasLastAPICallSuccessful.value = false
                Log.i("Item Info", "Error -> ${e.message}")
            }
        }
    }

    fun resetSuccessFlag() {
        _opSuccess.value = false // Or null, if your logic allows
    }
}
