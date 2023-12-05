package com.example.cdihandheldscannerviewactivity.ItemPicking

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdihandheldscannerviewactivity.Utils.Network.ItemsInOrderInfo
import com.example.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import com.example.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class ItemPickingViewModel: ViewModel() {

    // Adapter Position
    private val _currentlyChosenAdapterPosition = MutableLiveData<Int>()
    val currentlyChosenAdapterPosition : LiveData<Int>
        get() = _currentlyChosenAdapterPosition


    // List Of Items
    private val _listOfItemsInOrder = MutableLiveData<List<ItemsInOrderInfo>>() // This string will change to be an object with all the details of an item in an order
    val listOfItemsInOrder: LiveData<List<ItemsInOrderInfo>>
        get() = _listOfItemsInOrder


    // Company ID
    private val _companyID =  MutableLiveData<String>()
    val companyID: LiveData<String>
        get() = _companyID


    // Picker User Name
    private val _userNameOfPicker =  MutableLiveData<String>()
    val userNameOfPicker: LiveData<String>
        get() = _userNameOfPicker


    // Error Message
    private val _errorMessage = MutableLiveData<MutableMap<String,String>>()
    val errorMessage : LiveData<MutableMap<String,String>>
        get() = _errorMessage


    // API Call Confirmation
    private val _wasLastAPICallSuccessful = MutableLiveData<Boolean>()
    val wasLastAPICallSuccessful : LiveData<Boolean>
        get() = _wasLastAPICallSuccessful


    // Order Found Validation
    private val _wasOrderFound = MutableLiveData<Boolean>()
    val wasOrderFound : LiveData<Boolean>
        get() = _wasOrderFound



    // Order Has Picking
    private val _orderHasPicking = MutableLiveData<Boolean>()
    val orderHasPicking : LiveData<Boolean>
        get() = _orderHasPicking


    // Bin Confirmation
    private val _wasBinConfirmed = MutableLiveData<Boolean>()
    val wasBinConfirmed : LiveData<Boolean>
        get() = _wasBinConfirmed


    // Item Confirmation
    private val _wasItemConfirmed = MutableLiveData<Boolean>()
    val wasItemConfirmed : LiveData<Boolean>
        get() = _wasItemConfirmed

    // Client account Confirmation
    private val _wasClientAccountClosed = MutableLiveData<Boolean>()
    val wasClientAccountClosed : LiveData<Boolean>
        get() = _wasClientAccountClosed



    fun setChosenAdapterPosition(position: Int){
        if (position >= 0)
            _currentlyChosenAdapterPosition.value = position
        else
            _currentlyChosenAdapterPosition.value = 0
    }


    init {
        _currentlyChosenAdapterPosition.value = 0
        _errorMessage.value = mutableMapOf("confirmBin" to "",
                                    "confirmItem" to "",
                                    "confirmOrder" to "",
                                    "getAllItemsInOrder" to "",
                                    "verifyIfClientAccountIsClosed" to "",
                                    "verifyIfOrderHasPicking" to "")
    }

    // Function called when ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
    }


    fun verifyIfOrderIsAvailableInBackend(orderNumber:String){
        // Exception handler for API call
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Confirm Order - Item Picking (exceptionHandler) " , "Error -> ${exception.message}")
        }

        // API call to get the products in order
        try {
            viewModelScope.launch(exceptionHandler) {
                val response = ScannerAPI.ItemPickingForDispatchService.confirmOrder(
                    orderNumber,
                    _companyID.value!!
                )
                _wasLastAPICallSuccessful.value = true
                _errorMessage.value!!["confirmOrder"] = response.response.errorMessage
                _wasOrderFound.value = response.response.isOrderAvailable

            }

        }catch (e: Exception){
            _wasLastAPICallSuccessful.value = false
            Log.i("Confirm Order - Item Picking (e) " , "Error -> ${e.message}")
        }
    }

    fun setCompanyIDFromSharedPref(companyID: String){
        _companyID.value = companyID
    }
    fun setUserNameOfPickerFromSharedPref(userNameOfPicker: String){
        _userNameOfPicker.value = userNameOfPicker
    }


    fun verifyIfClientAccountIsClosedInBackend(orderNumber:String){
        // Exception handler for API call
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Verify Client Account - Item Picking (exceptionHandler) " , "Error -> ${exception.message}")
        }

        // API call to get the products in order
        try {
            viewModelScope.launch(exceptionHandler) {
                val response = ScannerAPI.ItemPickingForDispatchService.verifyIfClientAccountIsClosed(
                    orderNumber,
                    _companyID.value!!
                )
                _wasLastAPICallSuccessful.value = true
                _errorMessage.value!!["verifyIfClientAccountIsClosed"] = response.response.errorMessage
                _wasClientAccountClosed.value = response.response.isClientAccountClosed

            }

        }catch (e: Exception){
            _wasLastAPICallSuccessful.value = false
            Log.i("Verify Client Account - Item Picking (e) " , "Error -> ${e.message}")
        }
    }

    fun confirmBin(orderNumber: String, scannedBin:String, adapterPosition: Int){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Confirm Bin - Item Picking (exceptionHandler) " , "Error -> ${exception.message}")
        }

        // API call to get the products in order
        try {
            val binLocation = listOfItemsInOrder.value!![adapterPosition].binLocation
            val itemNumber = listOfItemsInOrder.value!![adapterPosition].itemNumber
            viewModelScope.launch(exceptionHandler) {
                val response = ScannerAPI.ItemPickingForDispatchService.confirmBin(scannedBin, orderNumber, itemNumber, binLocation)
                _wasLastAPICallSuccessful.value = true
                _wasBinConfirmed.value = response.response.hasBinBeenConfirmed
                _errorMessage.value!!["confirmBin"] = response.response.errorMessage
            }

        }catch (e: Exception){
            _wasLastAPICallSuccessful.value = false
            Log.i("Confirm Bin - Item Picking (e) " , "Error -> ${e.message}")
        }
    }

    fun verifyIfOrderHasPickingInBackend(orderNumber:String){
        // Exception handler for API call
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Verify if Order Has Picking - Item Picking (exceptionHandler) " , "Error -> ${exception.message}")
        }

        // API call to get the products in order
        try {
            viewModelScope.launch(exceptionHandler) {
                val response = ScannerAPI.ItemPickingForDispatchService.verifyIfOrderHasPicking(
                    orderNumber,
                    _companyID.value!!
                )
                _wasLastAPICallSuccessful.value = true
                _errorMessage.value!!["verifyIfOrderHasPicking"] = response.response.errorMessage
                _orderHasPicking.value = response.response.orderHasPicking

            }

        }catch (e: Exception){
            _wasLastAPICallSuccessful.value = false
            Log.i("Verify if Order Has Picking - Item Picking (e) " , "Error -> ${e.message}")
        }
    }

    fun getItemsInOrder(orderNumber: String){
        // Exception handler for API call
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Get Items In Order - Item Picking (exceptionHandler) " , "Error -> ${exception.message}")
        }

        // API call to get the products in order
        try {
            viewModelScope.launch(exceptionHandler) {
                val response = ScannerAPI.ItemPickingForDispatchService.getAllItemsInOrder(
                    orderNumber,
                    _companyID.value!!,
                    _userNameOfPicker.value!!
                )
                _wasLastAPICallSuccessful.value = true
                _listOfItemsInOrder.value = response.response.itemsInOrder.itemsInOrder
            }

        }catch (e: Exception){
            _wasLastAPICallSuccessful.value = false
            Log.i("Get Items In Order - Item Picking (e) " , "Error -> ${e.message}")
        }
    }

}