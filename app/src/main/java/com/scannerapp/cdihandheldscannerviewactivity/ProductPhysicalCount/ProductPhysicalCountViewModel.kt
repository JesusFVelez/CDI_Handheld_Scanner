package com.comdist.cdihandheldscannerviewactivity.InventoryCount

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.*
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class InventoryCountViewModel: ViewModel() {

    private val _opSuccess = MutableLiveData<Boolean>()
    val opSuccess: LiveData<Boolean>
        get() = _opSuccess

    private val _opMessage = MutableLiveData<String>()
    val opMessage: LiveData<String>
        get() = _opMessage

    private val _itemInfo = MutableLiveData<List<TtItemInfo>>()
    val itemInfo: LiveData<List<TtItemInfo>>
        get() = _itemInfo

    private val _binInfo = MutableLiveData<List<TtBinInfo>>()
    val binInfo: LiveData<List<TtBinInfo>>
        get() = _binInfo

    private val _wasLastAPICallSuccessful = MutableLiveData<Boolean>()
    val wasLastAPICallSuccessful: LiveData<Boolean>
        get() = _wasLastAPICallSuccessful

    fun updateCount(pItemNumber: String, pWarehouseNo: Int, pBinLocation: String, pQtyCounted: Double, pCompanyID: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            _opMessage.value = "Exception occurred: ${exception.localizedMessage}"
            Log.e("Update Count", "Exception -> ${exception.localizedMessage}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getInventoryCountService().updateCount(pItemNumber, pWarehouseNo, pBinLocation, pQtyCounted, pCompanyID)
                _opMessage.value = response.response.opMessage
                _opSuccess.value = response.response.opSuccess
                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                _wasLastAPICallSuccessful.value = false
                Log.e("Update Count", "Exception -> ${e.localizedMessage}")
            }
        }
    }

    fun getAllItemsInBinForSuggestion(pBinLocation: String, pWarehouse: Int, pCompanyID: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.e("Get Items In Bin", "Failed to fetch items: ${exception.localizedMessage}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getInventoryCountService().getAllItemsInBinForSuggestion(pBinLocation, pWarehouse, pCompanyID)
                _itemInfo.value = response.ttItemInfo
                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                _wasLastAPICallSuccessful.value = false
                Log.e("Get Items In Bin", "Exception -> ${e.localizedMessage}")
            }
        }
    }

    fun getAllBinNumbers(pCompanyID: String, pWarehouseNo: Int) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.e("Get Bin Numbers", "Failed to fetch bin numbers: ${exception.localizedMessage}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getInventoryCountService().getAllBinNumbers(pCompanyID, pWarehouseNo)
                _binInfo.value = response.response.ttBinInfoWrapper.ttBinInfo
                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                _wasLastAPICallSuccessful.value = false
                Log.e("Get Bin Numbers", "Exception -> ${e.localizedMessage}")
            }
        }
    }


}

