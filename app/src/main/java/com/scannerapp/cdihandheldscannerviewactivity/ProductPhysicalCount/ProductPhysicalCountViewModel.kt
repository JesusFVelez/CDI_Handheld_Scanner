package com.comdist.cdihandheldscannerviewactivity.InventoryCount

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.BinInfo
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.TtItemInfo
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class InventoryCountViewModel : ViewModel() {

    private val _opSuccess = MutableLiveData<Boolean>()
    val opSuccess: LiveData<Boolean>
        get() = _opSuccess

    private val _opMessage = MutableLiveData<String>()
    val opMessage: LiveData<String>
        get() = _opMessage

    private val _itemInfo = MutableLiveData<List<TtItemInfo>>()
    val itemInfo: LiveData<List<TtItemInfo>>
        get() = _itemInfo

    private val _binInfo = MutableLiveData<List<BinInfo>>()
    val binInfo: LiveData<List<BinInfo>>
        get() = _binInfo

    private val _wasLastAPICallSuccessful = MutableLiveData<Boolean>()
    val wasLastAPICallSuccessful: LiveData<Boolean>
        get() = _wasLastAPICallSuccessful

    private val _companyIDOfUser = MutableLiveData<String>()
    val companyIDOfUser: LiveData<String>
        get() = _companyIDOfUser

    private val _warehouseNO = MutableLiveData<Int>()
    val warehouseNumberOfUser: LiveData<Int>
        get() = _warehouseNO

    private val _currentlyChosenItemForSearch = MutableLiveData<BinInfo>()
    val setCurrentlySelectedBin: LiveData<BinInfo>
        get() = _currentlyChosenItemForSearch

    // Added for saving filter states
    val enteredItemNumber = MutableLiveData<String>()
    val enteredVendor = MutableLiveData<String>()
    val enteredClassCode = MutableLiveData<String>()
    val selectedLane = MutableLiveData<String>("ALL")

    // Function to set the company ID from shared preferences
    fun setCompanyIDFromSharedPref(companyID: String) {
        _companyIDOfUser.value = companyID
    }

    fun setWarehouseNumberFromSharedPref(warehouseNumber: Int) {
        _warehouseNO.value = warehouseNumber
    }

    fun setCurrentlySelectedBin(selectedBin: BinInfo) {
        _currentlyChosenItemForSearch.value = selectedBin
    }

    fun isBinFullyCounted(binLocation: String): Boolean {
        val itemsInBin = itemInfo.value?.filter { it.binLocation == binLocation }
        return itemsInBin?.all { it.inCount } == true
    }

    fun getAllBinNumbers(pCompanyID: String, pWarehouse: Int) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.e("Get Bin Numbers", "Failed to fetch bin numbers: ${exception.localizedMessage}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getInventoryCountService().getAllBinNumbers(pCompanyID, pWarehouse)
                Log.d("GetAllBinNumbers", "Response: ${response.response.ttBinInfo.ttBinInfo}")
                _binInfo.value = response.response.ttBinInfo.ttBinInfo.map { bin ->
                    BinInfo(
                        binLocation = bin.binLocation,
                        classCode = "",  // Set default values or get them from another source if available
                        vendor = "",
                        itemNumber = ""
                    )
                }
                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                _wasLastAPICallSuccessful.value = false
                Log.e("Get Bin Numbers", "Exception -> ${e.localizedMessage}")
            }
        }
    }

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
                val items = response.response.ttItemInfo.ttItemInfo.map { item ->
                    item.copy(expireDate = item.expireDate ?: "N/A")
                }
                _itemInfo.value = items
                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                _wasLastAPICallSuccessful.value = false
                Log.e("Get Items In Bin", "Exception -> ${e.localizedMessage}")
            }
        }
    }

    fun getBinsByClassCode(pClassCode: String, pCompanyID: String, pWarehouseNo: Int) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.e("Get Bins By Class Code", "Failed to fetch bins: ${exception.localizedMessage}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getInventoryCountService().getBinsByClassCode(pClassCode, pCompanyID, pWarehouseNo)
                Log.d("GetBinsByClassCode", "Response: ${response.response.ttBinInfo.ttBinInfo}")

                val bins = response.response.ttBinInfo.ttBinInfo.map { bin ->
                    BinInfo(
                        binLocation = bin.binLocation,
                        classCode = bin.classCode ?: "N/A",
                        vendor = bin.vendor ?: "N/A",
                        itemNumber = bin.itemNumber
                    )
                }
                Log.d("GetBinsByClassCode", "Parsed Bins: $bins")

                _binInfo.value = bins
                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                _wasLastAPICallSuccessful.value = false
                Log.e("Get Bins By Class Code", "Exception -> ${e.localizedMessage}")
            }
        }
    }

    fun getBinsByVendor(pVendor: String, pCompanyID: String, pWarehouseNo: Int) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.e("Get Bins By Vendor", "Failed to fetch bins: ${exception.localizedMessage}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getInventoryCountService().getBinsByVendor(pVendor, pCompanyID, pWarehouseNo)
                Log.d("GetBinsByVendor", "Response: ${response.response.ttBinInfo.ttBinInfo}")

                val bins = response.response.ttBinInfo.ttBinInfo.map { bin ->
                    BinInfo(
                        binLocation = bin.binLocation,
                        classCode = bin.classCode ?: "N/A",
                        vendor = bin.vendor ?: "N/A",
                        itemNumber = bin.itemNumber
                    )
                }
                Log.d("GetBinsByVendor", "Parsed Bins: $bins")

                _binInfo.value = bins
                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                _wasLastAPICallSuccessful.value = false
                Log.e("Get Bins By Vendor", "Exception -> ${e.localizedMessage}")
            }
        }
    }

    fun getBinsByItemNumber(pItemNumber: String, pCompanyID: String, pWarehouseNo: Int) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.e("Get Bins By Item Number", "Failed to fetch bins: ${exception.localizedMessage}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getInventoryCountService().getBinsByItemNumber(pItemNumber, pCompanyID, pWarehouseNo)
                Log.d("GetBinsByItemNumber", "Response: ${response.response.ttBinInfo.ttBinInfo}")

                val bins = response.response.ttBinInfo.ttBinInfo.map { bin ->
                    BinInfo(
                        binLocation = bin.binLocation,
                        classCode = bin.classCode ?: "N/A",
                        vendor = bin.vendor ?: "N/A",
                        itemNumber = bin.itemNumber
                    )
                }
                Log.d("GetBinsByItemNumber", "Parsed Bins: $bins")

                _binInfo.value = bins
                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                _wasLastAPICallSuccessful.value = false
                Log.e("Get Bins By Item Number", "Exception -> ${e.localizedMessage}")
            }
        }
    }
}
