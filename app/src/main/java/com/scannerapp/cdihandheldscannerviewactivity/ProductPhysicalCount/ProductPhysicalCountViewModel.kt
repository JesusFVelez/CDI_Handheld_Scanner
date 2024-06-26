package com.comdist.cdihandheldscannerviewactivity.InventoryCount

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.BinsByClassCodeByVendorAndByItemNumber
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.GetItemDetailsForPopupResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.TtItemInf
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.TtItemInfo
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    private val _itemInfoPopUp = MutableLiveData<List<TtItemInf>>()
    val itemInfoPopUp: LiveData<List<TtItemInf>>
        get() = _itemInfoPopUp

    private val _binInfo = MutableLiveData<List<BinsByClassCodeByVendorAndByItemNumber>>()
    val binInfo: LiveData<List<BinsByClassCodeByVendorAndByItemNumber>>
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

    private val _currentlyChosenItemForSearch = MutableLiveData<BinsByClassCodeByVendorAndByItemNumber>()
    val setCurrentlySelectedBin: LiveData<BinsByClassCodeByVendorAndByItemNumber>
        get() = _currentlyChosenItemForSearch

    private val _countButtonPressed = MutableLiveData<Boolean>()
    val countButtonPressed: LiveData<Boolean>
        get() = _countButtonPressed

    // Added for saving filter states
    val enteredItemNumber = MutableLiveData<String>()
    val enteredVendor = MutableLiveData<String>()
    val enteredClassCode = MutableLiveData<String>()
    val selectedLane = MutableLiveData<String>("ALL")

    // Local variables to hold the filter states
    private var savedItemNumber: String = ""
    private var savedVendor: String = ""
    private var savedClassCode: String = ""
    private var savedLane: String = "ALL"

    // Function to set the company ID from shared preferences
    fun setCompanyIDFromSharedPref(companyID: String) {
        _companyIDOfUser.value = companyID
    }

    fun setWarehouseNumberFromSharedPref(warehouseNumber: Int) {
        _warehouseNO.value = warehouseNumber
    }

    fun setCurrentlySelectedBin(selectedBin: BinsByClassCodeByVendorAndByItemNumber) {
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
                    BinsByClassCodeByVendorAndByItemNumber(
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

    fun getBinsByClassCodeByVendorAndByItemNumber(pClassCode: String, pVendor: String, pItemNumber: String, pCompanyID: String, pWarehouseNo: Int) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.e("Get Bins By All", "Failed to fetch bins: ${exception.localizedMessage}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getInventoryCountService().getBinsByClassCodeByVendorAndByItemNumber(pClassCode, pVendor, pItemNumber, pCompanyID, pWarehouseNo)
                Log.d("GetBinsByAll", "Response: ${response.response.ttBinInfo.ttBinInfo}")

                val bins = response.response.ttBinInfo.ttBinInfo.map { bin ->
                    BinsByClassCodeByVendorAndByItemNumber(
                        binLocation = bin.binLocation,
                        classCode = bin.classCode ?: "N/A",
                        vendor = bin.vendor ?: "N/A",
                        itemNumber = bin.itemNumber ?: "N/A"
                    )
                }
                Log.d("GetBinsByAll", "Parsed Bins: $bins")

                _binInfo.value = bins
                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                _wasLastAPICallSuccessful.value = false
                Log.e("Get Bins By All", "Exception -> ${e.localizedMessage}")
            }
        }
    }

    fun getItemDetailsForPopup(pItemNumberOrBarCode: String, pWarehouse: Int, pCompanyID: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.e("ViewModel", "Exception in getItemDetailsForPopup: ${exception.localizedMessage}", exception)
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                Log.d("ViewModel", "Calling getItemDetailsForPopup with item: $pItemNumberOrBarCode, warehouse: $pWarehouse, company: $pCompanyID")
                val call = ScannerAPI.getInventoryCountService().getItemDetailsForPopup(pItemNumberOrBarCode, pWarehouse, pCompanyID)
                call.enqueue(object : Callback<GetItemDetailsForPopupResponseWrapper> {
                    override fun onResponse(
                        call: Call<GetItemDetailsForPopupResponseWrapper>,
                        response: Response<GetItemDetailsForPopupResponseWrapper>
                    ) {
                        if (response.isSuccessful) {
                            val itemInfoList = response.body()?.response?.ttItemInfo?.ttItemInfo
                            val items: List<TtItemInf> = itemInfoList?.map { item ->
                                item.copy(expireDate = item.expireDate ?: "N/A")
                            } ?: emptyList()

                            _itemInfoPopUp.postValue(items)
                            _wasLastAPICallSuccessful.postValue(true)
                            Log.d("ViewModel", "getItemDetailsForPopup successful: $items")
                        } else {
                            _wasLastAPICallSuccessful.postValue(false)
                            Log.e("ViewModel", "getItemDetailsForPopup response not successful, code: ${response.code()}, message: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<GetItemDetailsForPopupResponseWrapper>, t: Throwable) {
                        _wasLastAPICallSuccessful.postValue(false)
                        Log.e("ViewModel", "Exception in getItemDetailsForPopup: ${t.localizedMessage}", t)
                    }
                })
            } catch (e: Exception) {
                _wasLastAPICallSuccessful.value = false
                Log.e("ViewModel", "Exception in getItemDetailsForPopup: ${e.localizedMessage}", e)
            }
        }
    }

    fun saveFilterStates(context: Context) {
        savedClassCode = enteredClassCode.value ?: ""
        savedVendor = enteredVendor.value ?: ""
        savedItemNumber = enteredItemNumber.value ?: ""
        savedLane = selectedLane.value ?: "ALL"
    }

    fun loadFilterStates(context: Context) {
        enteredClassCode.value = savedClassCode
        enteredVendor.value = savedVendor
        enteredItemNumber.value = savedItemNumber
        selectedLane.value = savedLane

        // Notify observers to apply filters correctly
        enteredClassCode.postValue(enteredClassCode.value)
        enteredVendor.postValue(enteredVendor.value)
        enteredItemNumber.postValue(enteredItemNumber.value)
        selectedLane.postValue(selectedLane.value)
    }

    fun clearItemInfoPopUp() {
        _itemInfoPopUp.value = emptyList()
    }

    fun setCountButtonPressed(pressed: Boolean) {
        _countButtonPressed.value = pressed
    }
}
