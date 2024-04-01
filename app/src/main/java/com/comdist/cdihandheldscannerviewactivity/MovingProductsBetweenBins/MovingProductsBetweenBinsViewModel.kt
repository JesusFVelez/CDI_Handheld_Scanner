package com.comdist.cdihandheldscannerviewactivity.MovingProductsBetweenBins

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.WarehouseInfo
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.binInfo
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.lang.Exception

class MovingProductsBetweenBinsViewModel: ViewModel() {
    // LiveData and MutableLiveData for various UI states and data
    private val _wasLastAPICallSuccessful = MutableLiveData<Boolean>()
    val wasLastAPICallSuccessful: LiveData<Boolean>
        get() = _wasLastAPICallSuccessful

    // Error Message
    private val _errorMessage = MutableLiveData<MutableMap<String, String>>()
    val errorMessage: LiveData<MutableMap<String, String>>
        get() = _errorMessage

    private val _listOfBinsInWarehouse = MutableLiveData<List<binInfo>>()
    val listOfBinsInWarehouse: LiveData<List<binInfo>>
        get() = _listOfBinsInWarehouse


    init {
        _errorMessage.value = mutableMapOf(
            "confirmBin" to "",
            "confirmItem" to "",
            "isQuantityValid" to "",
            "moveItemBetweenBins" to ""
        )

        fun getAllBinsFromBackend(warehouseNumber: Int) {
            // Exception handler for API call
            val exceptionHandler = CoroutineExceptionHandler { _, exception ->
                _wasLastAPICallSuccessful.value = false
                Log.i(
                    "Get All Bins - Bin Movement (exceptionHandler) ",
                    "Error -> ${exception.message}"
                )
            }

            // API call to get the products in order
            try {
                viewModelScope.launch(exceptionHandler) {
                    val response =
                        ScannerAPI.getMovingItemsBetweenBinsService().getAllBins(warehouseNumber)
                    _wasLastAPICallSuccessful.value = true
                    _listOfBinsInWarehouse.value = response.response.listOfBinsWrapper.listOfBins
                }

            } catch (e: Exception) {
                _wasLastAPICallSuccessful.value = false
                Log.i("Get All Bins - Bin Movement (e) ", "Error -> ${e.message}")
            }
        }


    }
}