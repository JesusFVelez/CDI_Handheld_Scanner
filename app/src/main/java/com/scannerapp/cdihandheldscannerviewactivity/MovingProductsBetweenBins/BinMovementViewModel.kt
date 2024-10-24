package com.scannerapp.cdihandheldscannerviewactivity.MovingProductsBetweenBins

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.binInfo
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.itemsInBin
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.lang.Exception

class BinMovementViewModel: ViewModel() {
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

    private val _listOfAllItemsInAllBins = MutableLiveData<List<itemsInBin>>()
    val listOfAllItemsInAllBins: LiveData<List<itemsInBin>>
        get() = _listOfAllItemsInAllBins

    // Company ID
    private val _companyID =  MutableLiveData<String>()
    val companyID: LiveData<String>
        get() = _companyID

    // Company ID
    private val _warehouseNumber =  MutableLiveData<Int>()
    val warehouseNumber: LiveData<Int>
        get() = _warehouseNumber

    // Picker User name
    private val _pickerUserName =  MutableLiveData<String>()
    val pickerUserName: LiveData<String>
        get() = _pickerUserName

    private val _currentlyChosenItemToMoveFromSpinner = MutableLiveData<itemsInBin>()
    val currentlyChosenItemToMove: LiveData<itemsInBin>
        get() = _currentlyChosenItemToMoveFromSpinner

    private val _positionOfItemToMove = MutableLiveData<Int>()
    val positionOfItemToMove: MutableLiveData<Int>
        get() = _positionOfItemToMove

    private val _wasBinConfirmed = MutableLiveData<Boolean>()
    val wasBinConfirmed: LiveData<Boolean>
        get() = _wasBinConfirmed

    private val _wasItemMovedSuccessfully = MutableLiveData<Boolean>()
    val wasItemMovedSuccessfully: LiveData<Boolean>
        get() = _wasItemMovedSuccessfully

    private val _allItemsMoved = MutableLiveData<Boolean>()
    val allItemsMoved: LiveData<Boolean>
        get() = _allItemsMoved

    private val _newItemToBeMoved = MutableLiveData<BinMovementDataClass?>()
    val newItemToBeMoved: LiveData<BinMovementDataClass?>
        get() = _newItemToBeMoved

    private val _networkErrorMessage = MutableLiveData<String>()
    val networkErrorMessage: LiveData<String>
        get() = _networkErrorMessage

    private val _willUpdateItemToMove = MutableLiveData<Boolean>()
    val willUpdateItemToMove: LiveData<Boolean>
        get() = _willUpdateItemToMove

    data class itemBeingMoved (
        val itemNumber: String,
        val wasItemMoved: Boolean,
        val errorMessage: String,
        val fromBinLocation: String,
        val toBinLocation:String
    )
    private val _itemsMoved = MutableLiveData<MutableList<itemBeingMoved>>()
    val itemsMoved: LiveData<MutableList<itemBeingMoved>>
        get() = _itemsMoved

    fun setWillUpdateItemToMove(willUpdate: Boolean){
        _willUpdateItemToMove.value = willUpdate
    }

    fun resetWillUpdateItemToMove(){
        _willUpdateItemToMove.value = false
    }


    fun setPositionOfItemToMove(position: Int){
        _positionOfItemToMove.value = position
    }

    fun resetPositionOfItemToMove(){
        _positionOfItemToMove.value = -1
    }

    init {
        _willUpdateItemToMove.value = false
        _positionOfItemToMove.value = -1
        _errorMessage.value = mutableMapOf(
            "confirmBin" to "",
            "confirmItem" to "",
            "isQuantityValid" to "",
            "moveItemBetweenBins" to ""
        )
        _networkErrorMessage.value = ""
        _itemsMoved.value = mutableListOf()
    }

    fun setNewItemToBeMoved(newItemToMove: BinMovementDataClass){
        _newItemToBeMoved.value = newItemToMove
    }

    fun resetNewItemToBeMoved(){
        _newItemToBeMoved.value = null
    }

    fun setPickerUserName(pickerUserName: String){
        _pickerUserName.value = pickerUserName
    }

    private val _hasAPIBeenCalled = MutableLiveData<Boolean>()
    val hasAPIBeenCalled:LiveData<Boolean>
        get() = _hasAPIBeenCalled

    fun setCurrentlyChosenItemToMoveFromSpinner(itemToMove: itemsInBin){
        _currentlyChosenItemToMoveFromSpinner.value = itemToMove
    }

    fun setCompanyIDFromSharedPref(companyID: String){
        _companyID.value = companyID
    }

    fun setWarehouseFromSharedPref(warehouse: Int){
        _warehouseNumber.value = warehouse
    }

    fun resetHasAPIBeenCalled(){
        _hasAPIBeenCalled.value = false
    }

        fun getAllBinsFromBackend() {
            // Exception handler for API call
            val exceptionHandler = CoroutineExceptionHandler { _, exception ->
                _networkErrorMessage.value = exception.message
                _wasLastAPICallSuccessful.value = false
                Log.i(
                    "Get All Bins - Bin Movement (exceptionHandler) ",
                    "Error -> ${exception.message}"
                )
            }

            // API call to get the products in order
            try {

                viewModelScope.launch(exceptionHandler) {
                    _hasAPIBeenCalled.value = true
                    val response =
                        ScannerAPI.getMovingItemsBetweenBinsService().getAllBins(_companyID.value!!,_warehouseNumber.value!!)
                    _wasLastAPICallSuccessful.value = true
                    _listOfBinsInWarehouse.value = response.response.listOfBinsWrapper.listOfBins
                }

            } catch (e: Exception) {
                _networkErrorMessage.value = e.message
                _wasLastAPICallSuccessful.value = false
                Log.i("Get All Bins - Bin Movement (e) ", "Error -> ${e.message}")
            }
        }

        fun getAllItemsInAllBinsFromBackend() {
            // Exception handler for API call
            val exceptionHandler = CoroutineExceptionHandler { _, exception ->
                _networkErrorMessage.value = exception.message
                _wasLastAPICallSuccessful.value = false
                Log.i(
                    "Get All Items In Bin - Bin Movement (exceptionHandler) ",
                    "Error -> ${exception.message}"
                )
            }

            // API call to get the products in order
            try {

                viewModelScope.launch(exceptionHandler) {
                    _hasAPIBeenCalled.value = true
                    val response =
                        ScannerAPI.getMovingItemsBetweenBinsService().getAllItemsInAllBin(_companyID.value!!, _warehouseNumber.value!!)
                    _wasLastAPICallSuccessful.value = true
                    _listOfAllItemsInAllBins.value = response.response.listOfItemsInBinWrapper.listOfItemsInBin
                }

            } catch (e: Exception) {
                _networkErrorMessage.value = e.message
                _wasLastAPICallSuccessful.value = false
                Log.i("Get All Items In Bin - Bin Movement (e) ", "Error -> ${e.message}")
            }
        }


    fun resetAllItemsMovedFlag() {
        _allItemsMoved.value = false
    }




    fun moveItemsBetweenBins(itemsToMove: List<BinMovementDataClass>){

        // Exception handler for API call
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            Log.i(
                "Move Item Between Bins - Bin Movement (exceptionHandler) ",
                "Error -> ${exception.message}"
            )
        }
        viewModelScope.launch {
            _hasAPIBeenCalled.value = true  // Moved outside the loop to avoid setting it multiple times
            for (itemToMove in itemsToMove) {
                try {
                    val response = ScannerAPI.getMovingItemsBetweenBinsService()
                        .moveItemBetweenBins(
                            rowID = itemToMove.rowIDOfItemInFromBin,
                            pickerUserName = _pickerUserName.value!!,
                            newBin = itemToMove.toBinNumber,
                            quantityToMove = itemToMove.qtyToMoveFromBinToBin.toFloat()
                        )
                    _itemsMoved.value!!.add(itemBeingMoved(
                        itemToMove.itemNumber,
                        response.response.wasItemMoved,
                        response.response.errorMessage,
                        itemToMove.fromBinNumber,
                        itemToMove.toBinNumber
                    ))
                    _wasLastAPICallSuccessful.value = true
                } catch (e: Exception) {
                    _networkErrorMessage.value = e.message
                    _wasLastAPICallSuccessful.value = false
                    Log.i("API Error", "Error -> ${e.message}")
                }
            }
            // After all API calls are complete, notify the UI
            _allItemsMoved.value = true
        }



//        viewModelScope.launch {
//            val jobs = itemsToMove.map { itemToMove ->
//                launch {
//                    try {
//                        _hasAPIBeenCalled.value = true
//                        val response = ScannerAPI.getMovingItemsBetweenBinsService()
//                            .moveItemBetweenBins(rowID = itemToMove.rowIDOfItemInFromBin,
//                                                pickerUserName = _pickerUserName.value!!,
//                                                newBin = itemToMove.toBinNumber,
//                                                quantityToMove = itemToMove.qtyToMoveFromBinToBin.toFloat())
//                        _itemsMoved.value!!.add(itemBeingMoved(
//                            itemToMove.itemNumber,
//                            response.response.wasItemMoved,
//                            response.response.errorMessage,
//                            itemToMove.fromBinNumber,
//                            itemToMove.toBinNumber
//                        ))
//                        _wasLastAPICallSuccessful.value = true
////                        _errorMessage.value!!["moveItemBetweenBins"] = response.response.errorMessage
////                        _wasItemMovedSuccessfully.value = response.response.wasItemMoved
//                    } catch (e: Exception) {
//                        _networkErrorMessage.value = e.message
//                        _wasLastAPICallSuccessful.value = false
//                        Log.i("API Error", "Error -> ${e.message}")
//                    }
//                }
//            }
//            // Wait for all jobs to complete
//            jobs.joinAll()
//
//            // After all jobs are complete, notify the UI
//            _allItemsMoved.value = true // You need to add this LiveData<Boolean> to your ViewModel
//        }
    }

    fun confirmIfBinExistsInDB(binLocation: String){
        // Exception handler for API call
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            Log.i(
                "Confirm Bin - Bin Movement (exceptionHandler) ",
                "Error -> ${exception.message}"
            )
        }

        // API call to get the products in order
        try {
            viewModelScope.launch(exceptionHandler) {
                _hasAPIBeenCalled.value = true
                val response =
                    ScannerAPI.getMovingItemsBetweenBinsService().confirmBin(binLocation, _companyID.value!!,_warehouseNumber.value!!)
                _errorMessage.value!!["confirmBin"] = response.response.errorMessage
                _wasBinConfirmed.value = response.response.wasBinConfirmed
                _wasLastAPICallSuccessful.value = true
            }

        } catch (e: Exception) {
            _networkErrorMessage.value = e.message
            _wasLastAPICallSuccessful.value = false
            Log.i("Confirm Bin - Bin Movement (e) ", "Error -> ${e.message}")
        }
    }

    fun moveItemBetweenBins(itemToMove: BinMovementDataClass){
        // Exception handler for API call
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            Log.i(
                "Move Item Between Bins - Bin Movement (exceptionHandler) ",
                "Error -> ${exception.message}"
            )
        }

        // API call to get the products in order
        try {
            viewModelScope.launch(exceptionHandler) {
                _hasAPIBeenCalled.value = true
                val response =
                    ScannerAPI.getMovingItemsBetweenBinsService().moveItemBetweenBins(itemToMove.rowIDOfItemInFromBin, pickerUserName = _pickerUserName.value!!, itemToMove.toBinNumber, itemToMove.qtyToMoveFromBinToBin.toFloat())
                _wasLastAPICallSuccessful.value = true
                _wasItemMovedSuccessfully.value = response.response.wasItemMoved
                _errorMessage.value!!["moveItemBetweenBins"] = response.response.errorMessage


            }

        } catch (e: Exception) {
            _networkErrorMessage.value = e.message
            _wasLastAPICallSuccessful.value = false
            Log.i("Move Item Between Bins - Bin Movement (e) ", "Error -> ${e.message}")
        }
    }



}