package com.comdist.cdihandheldscannerviewactivity.ReceivingProductsIntoBin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DoorBinList
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.ItemInfoList
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.PreReceiving
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.PreReceivingInfo
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.ResponsePreReceiving
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
class ReceivingProductsViewModel: ViewModel() {

    private val _currentlyChosenAdapterPosition = MutableLiveData<Int>()
    val currentlyChosenAdapterPosition: LiveData<Int>
        get() = _currentlyChosenAdapterPosition

    //Info from shared preferences
    private val _warehouseNumber = MutableLiveData<Int>()
    val warehouseNumber: LiveData<Int>
        get() = _warehouseNumber

    private val _companyID = MutableLiveData<String>()
    val companyID: LiveData<String>
        get() = _companyID

    // Map to handle error messages
    private val _errorMessage = MutableLiveData<MutableMap<String, String>>()
    val errorMessage: LiveData<MutableMap<String, String>>
        get() = _errorMessage

    // Objects for temporary tables from backend
    private val _itemInfo = MutableLiveData<ItemInfoList>()
    val itemInfo: LiveData<ItemInfoList>
        get() = _itemInfo

    private val _doorBins = MutableLiveData<DoorBinList>()
    val doorBins: LiveData<DoorBinList>
        get() = _doorBins

    private val _preReceivingInfo = MutableLiveData<PreReceivingInfo>()
    val preReceivingInfo: LiveData<PreReceivingInfo>
        get() = _preReceivingInfo

    // Booleans for error handling
    private val _wasPreReceivingFound = MutableLiveData<Boolean>()
    val preReceiving: LiveData<Boolean>
        get() = _wasPreReceivingFound

    private val _wasItemFound = MutableLiveData<Boolean>()
    val wasItemFound: LiveData<Boolean>
        get() = _wasItemFound

    private val _wasBinFound = MutableLiveData<Boolean>()
    val wasBinFound: LiveData<Boolean>
        get() = _wasBinFound

    private val _wasItemMovedToDoor = MutableLiveData<Boolean>()
    val wasItemMovedFromDoor: LiveData<Boolean>
        get() = _wasItemMovedToDoor

    private val _wasItemMovedToBin = MutableLiveData<Boolean>()
    val wasItemMovedFromBin: LiveData<Boolean>
        get() = _wasItemMovedToBin
    
    // Other Values
    private val _preReceivingNumber = MutableLiveData<String>()
    val preReceivingNumber: LiveData<String>
        get() = _preReceivingNumber
    
    init {
        _wasPreReceivingFound.value = false
        _errorMessage.value = mutableMapOf(
            "wasItemFoundError" to "",
            "wasPreReceivingFoundError" to "",
            "wasBinFoundError" to "",
            "wasItemMovedToDoorError" to "",
            "wasItemMovedToBinError" to ""
        )
    }

    // Get company and warehouse from shared preferences
    fun setCompanyIDFromSharedPref(companyID: String){
        _companyID.value = companyID
    }
    fun setWarehouseNumberFromSharedPref(warehouseNumber: Int){
        _warehouseNumber.value = warehouseNumber
    }

    // API call functions
    fun getDoorBins(warehouseNumber: Int, companyID: String){
        val exceptionHandler = CoroutineExceptionHandler{ _, exception ->
            Log.i("set door bins for Receiving API Call Exception Handler", "Error -> ${exception.message}")
        }

        viewModelScope.launch(exceptionHandler){
            try{
                val response = ScannerAPI.getReceivingProductService().getDoorBins(warehouseNumber, companyID)
                _doorBins.value = response.ttBinList.tt_bin_list
            }catch(e: Exception){
                Log.i("get set the pre-receiving for Receiving API Call Exception Handler", "Error -> ${e.message}")
            }
        }
    }

    fun getPreReceiving(binNumber: String, warehouseNumber: Int, companyID: String) {
       val exceptionHandler = CoroutineExceptionHandler{ _, exception ->
           _wasPreReceivingFound.value = false
           Log.i("set pre-receiving for Receiving API Call Exception Handler", "Error -> ${exception.message}")
       }
        viewModelScope.launch(exceptionHandler){
            try{
                val response = ScannerAPI.getReceivingProductService().getPreReceiving(binNumber, warehouseNumber, companyID)
                _preReceivingNumber.value = response.response.preReceivingNumber
                _wasPreReceivingFound.value = response.response.wasPreReceivingFound
                _errorMessage.value!!["wasPreReceivingFoundError"] = response.response.errorMessage
                
            }catch(e: Exception){
                Log.i("get set the pre-receiving for Receiving API Call Exception Handler", "Error -> ${e.message}")
            }
        }
    }

    fun getPreReceivingInfo(preReceivingNumber: String, warehouseNumber: Int, companyID: String){
        val exceptionHandler = CoroutineExceptionHandler{ _, exception ->
            Log.i("set pre-receiving info for Receiving API Call Exception Handler", "Error -> ${exception.message}")
        }
        viewModelScope.launch(exceptionHandler){
            try{
                val response = ScannerAPI.getReceivingProductService().getPreReceivingInfo(preReceivingNumber, warehouseNumber, companyID)
                _preReceivingInfo.value = response.response.ttPreReceiving.tt_pre_receiving
            }catch(e: Exception){
                Log.i("get set the pre-receiving info for Receiving API Call Exception Handler", "Error -> ${e.message}")
            }
        }
    }

    fun getItemInfo(scannedCode: String, warehouseNumber: Int, companyID: String){
        val exceptionHandler = CoroutineExceptionHandler{ _, exception ->
            _wasItemFound.value = false
            Log.i("set item info for Receiving API Call Exception Handler", "Error -> ${exception.message}")
        }
        viewModelScope.launch(exceptionHandler){
            try{
                val response = ScannerAPI.getReceivingProductService().getItemInfo(scannedCode, warehouseNumber, companyID)
                _itemInfo.value = response.itemInfo.item_info
                _wasItemFound.value = response.wasItemFound
                _errorMessage.value!!["wasItemFoundError"] = response.errorMessage
            }catch(e: Exception){
                Log.i("get set the item info for Receiving API Call Exception Handler", "Error -> ${e.message}")
            }
        }
    }

    fun wasBinFound(binNumber: String, warehouseNumber: Int, companyID: String){
        val exceptionHandler = CoroutineExceptionHandler{ _, exception ->
            _wasBinFound.value = false
            Log.i("set was bin found for Receiving API Call Exception Handler", "Error -> ${exception.message}")
        }
        viewModelScope.launch(exceptionHandler){
            try{
                val response = ScannerAPI.getReceivingProductService().wasBinFound(binNumber, warehouseNumber, companyID)
                _wasBinFound.value = response.wasPreReceivingFound
                _errorMessage.value!!["wasBinFoundError"] = response.errorMessage
            }catch(e: Exception){
                Log.i("get set the was bin found for Receiving API Call Exception Handler", "Error -> ${e.message}")
            }
        }
    }

    fun moveItemToDoor(scannedCode: String, doorBin: String, quantity: Int, lotNumber: String, expireDate: String, warehouseNumber: Int, companyID: String){
        val exceptionHandler = CoroutineExceptionHandler{ _, exception ->
            _wasItemMovedToDoor.value = false
            Log.i("set move item to door for Receiving API Call Exception Handler", "Error -> ${exception.message}")
        }
        viewModelScope.launch(exceptionHandler){
            try{
                val response = ScannerAPI.getReceivingProductService().addItemToDoorBin(scannedCode, doorBin, quantity, lotNumber, expireDate, warehouseNumber, companyID)
                _wasItemMovedToDoor.value = response.wasItemMovedToDoor
                _errorMessage.value!!["wasItemMovedToDoorError"] = response.errorMessage
            }catch(e: Exception){
                Log.i("get set the move item to door for Receiving API Call Exception Handler", "Error -> ${e.message}")
            }
        }
    }

    fun moveItemToBin(designatedBin: String, itemNumber: String, lotNumber: String, expirationDate: String, quantity: Int, warehouseNumber: Int, companyID: String){
        val exceptionHandler = CoroutineExceptionHandler{ _, exception ->
            _wasItemMovedToBin.value = false
            Log.i("set move item to bin for Receiving API Call Exception Handler", "Error -> ${exception.message}")
        }
        viewModelScope.launch(exceptionHandler){
            try{
                val response = ScannerAPI.getReceivingProductService().moveItemFromDoorBin(designatedBin, itemNumber, lotNumber, expirationDate, quantity, warehouseNumber, companyID)
                _wasItemMovedToBin.value = response.response.wasItemMoved
                _errorMessage.value!!["wasItemMovedToBinError"] = response.response.errorMessage
            }catch(e: Exception){
                Log.i("get set the move item to bin for Receiving API Call Exception Handler", "Error -> ${e.message}")
            }
        }
    }
}
