package com.comdist.cdihandheldscannerviewactivity.ItemPicking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemsInOrderInfo
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.RequestTimerParams
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.RequestTimerParamsWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ordersThatAreInPickingClass
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class ItemPickingViewModel: ViewModel() {

    // Adapter Position
    private val _currentlyChosenAdapterPosition = MutableLiveData<Int>()
    val currentlyChosenAdapterPosition : LiveData<Int>
        get() = _currentlyChosenAdapterPosition

    private val _orderNumber = MutableLiveData<String>()
    val orderNumber: LiveData<String>
        get() = _orderNumber


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

    private val _currentlyChosenItem = MutableLiveData<ItemsInOrderInfo>()
    val currentlyChosenItem: LiveData<ItemsInOrderInfo>
        get() = _currentlyChosenItem

    private val _UOMQtyInBarcode = MutableLiveData<Float>()
    val UOMQtyInBarcode:LiveData<Float>
        get() = _UOMQtyInBarcode

    private val _wasPickingSuccesfulyFinished = MutableLiveData<Boolean>()
    val wasPickingSuccesfulyFinished: LiveData<Boolean>
        get() = _wasPickingSuccesfulyFinished


    private val _ordersThatHavePicking = MutableLiveData<List<ordersThatAreInPickingClass>>()
    val ordersThatHavePicking: LiveData<List<ordersThatAreInPickingClass>>
        get() = _ordersThatHavePicking

    private val _hasPickingTimerAlreadyStarted = MutableLiveData<Boolean>()
    val hasPickingTimerAlreadyStarted : LiveData<Boolean>
        get() = _hasPickingTimerAlreadyStarted




    fun setChosenAdapterPosition(position: Int){
        if (position >= 0)
            _currentlyChosenAdapterPosition.value = position
        else
            _currentlyChosenAdapterPosition.value = 0
    }

    fun setCurrentlyChosenItem(){
        _currentlyChosenItem.value = _listOfItemsInOrder.value!![_currentlyChosenAdapterPosition.value!!]
    }


    init {
        _hasPickingTimerAlreadyStarted.value = false
        _ordersThatHavePicking.value = mutableListOf()
        _currentlyChosenAdapterPosition.value = 0
        _errorMessage.value = mutableMapOf("confirmBin" to "",
                                    "confirmItem" to "",
                                    "confirmOrder" to "",
                                    "getAllItemsInOrder" to "",
                                    "verifyIfClientAccountIsClosed" to "",
                                    "verifyIfOrderHasPicking" to "",
                                    "finishPickingForSingleItem" to "")
    }

    // Function called when ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
    }

    // Function to clear the list of products
    fun clearListOfItems(){
        _listOfItemsInOrder.value = listOf()
    }


    fun verifyIfOrderIsAvailableInBackend(){
        // Exception handler for API call
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Confirm Order - Item Picking (exceptionHandler) " , "Error -> ${exception.message}")
        }

        // API call to get the products in order
        try {
            viewModelScope.launch(exceptionHandler) {
                val response = ScannerAPI.getItemPickingForDispatchService().confirmOrder(
                    _orderNumber.value!!,
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



    fun verifyIfClientAccountIsClosedInBackend(){
        // Exception handler for API call
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Verify Client Account - Item Picking (exceptionHandler) " , "Error -> ${exception.message}")
        }

        // API call to get the products in order
        try {
            viewModelScope.launch(exceptionHandler) {
                val response = ScannerAPI.getItemPickingForDispatchService().verifyIfClientAccountIsClosed(
                    _orderNumber.value!!,
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

    fun confirmBin( scannedBin:String, adapterPosition: Int){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Confirm Bin - Item Picking (exceptionHandler) " , "Error -> ${exception.message}")
        }

        // API call to get the products in order
        try {
            val uniqueIDForPicking = listOfItemsInOrder.value!![adapterPosition].uniqueIDForPicking
            viewModelScope.launch(exceptionHandler) {
                val response = ScannerAPI.getItemPickingForDispatchService().confirmBin(scannedBin, uniqueIDForPicking)
                _wasLastAPICallSuccessful.value = true
                _errorMessage.value!!["confirmBin"] = response.response.errorMessage
                _wasBinConfirmed.value = response.response.hasBinBeenConfirmed

            }

        }catch (e: Exception){
            _wasLastAPICallSuccessful.value = false
            Log.i("Confirm Bin - Item Picking (e) " , "Error -> ${e.message}")
        }
    }

    fun verifyIfOrderHasPickingInBackend(){
        // Exception handler for API call
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Verify if Order Has Picking - Item Picking (exceptionHandler) " , "Error -> ${exception.message}")
        }

        // API call to get the products in order
        try {
            viewModelScope.launch(exceptionHandler) {
                val response = ScannerAPI.getItemPickingForDispatchService().verifyIfOrderHasPicking(
                    _orderNumber.value!!,
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



    fun confirmItemInBackend(scannedItemCode:String ){
        // Exception handler for API call
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Confirm Item - Item Picking (exceptionHandler) " , "Error -> ${exception.message}")
        }
        // API call to get the products in order
        try {
            viewModelScope.launch(exceptionHandler) {
                val response = ScannerAPI.getItemPickingForDispatchService().confirmItem(scannedItemCode, currentlyChosenItem.value!!.itemNumber, _companyID.value!!, _orderNumber.value!!)
                _wasLastAPICallSuccessful.value = true
                _errorMessage.value!!["confirmItem"] = response.response.errorMessage
                _UOMQtyInBarcode.value = response.response.UOMQtyInBarcode
                _wasItemConfirmed.value = response.response.wasItemConfirmed
            }

        }catch (e: Exception){
            _wasLastAPICallSuccessful.value = false
            Log.i("Confirm Item - Item Picking (e) " , "Error -> ${e.message}")
        }
    }

    fun setOrderNumber(orderNumber: String){
        _orderNumber.value = orderNumber
    }


    fun getItemsInOrder(){
        // Exception handler for API call
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Get Items In Order - Item Picking (exceptionHandler) " , "Error -> ${exception.message}")
        }

        // API call to get the products in order
        try {
            viewModelScope.launch(exceptionHandler) {
                val response = ScannerAPI.getItemPickingForDispatchService().getAllItemsInOrder(
                    _orderNumber.value!!,
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

    fun finishPickingForSingleItem(quantityBeingPicked: Float){
        // Exception handler for API call
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("finish picking for item - Item Picking (exceptionHandler) " , "Error -> ${exception.message}")
        }
        // API call to get the products in order
        try {
            viewModelScope.launch(exceptionHandler) {
                val response = ScannerAPI.getItemPickingForDispatchService().finishPickingForSingleItem(_currentlyChosenItem.value!!.uniqueIDForPicking,_userNameOfPicker.value!!,quantityBeingPicked)
                _wasLastAPICallSuccessful.value = true
                _errorMessage.value!!["finishPickingForSingleItem"] = response.response.errorMessage
                _wasPickingSuccesfulyFinished.value = response.response.wasPickingSuccesfull
            }

        }catch (e: Exception){
            _wasLastAPICallSuccessful.value = false
            Log.i("finish picking for item - Item Picking (e) " , "Error -> ${e.message}")
        }
    }



    fun startPickingTimer(){
        // Exception handler for API call
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            _hasPickingTimerAlreadyStarted.value = false
            Log.i("Start Picker Timer - Item Picking (exceptionHandler) " , "Error -> ${exception.message}")
        }
        // API call to get the products in order
        try {
            viewModelScope.launch(exceptionHandler) {
                val timerRequestBody = RequestTimerParams(_orderNumber.value!!, _userNameOfPicker.value!!)
                val timerRequestBodyWrapper = RequestTimerParamsWrapper(timerRequestBody)
                ScannerAPI.getItemPickingForDispatchService().startPickerTimer(timerRequestBodyWrapper)
                _wasLastAPICallSuccessful.value = true
                _hasPickingTimerAlreadyStarted.value = true
            }

        }catch (e: Exception){
            _wasLastAPICallSuccessful.value = false
            _hasPickingTimerAlreadyStarted.value = false
            Log.i("Start Picker Timer - Item Picking (e) " , "Error -> ${e.message}")
        }
    }

    fun updatePickingTimer(typeOfUpdate: String){
        // Exception handler for API call
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Update Picker Timer - Item Picking (exceptionHandler) " , "Error -> ${exception.message}")
        }
        // API call to get the products in order
        try {
            viewModelScope.launch(exceptionHandler) {
                ScannerAPI.getItemPickingForDispatchService().updatePickerTimer(_orderNumber.value!!, _userNameOfPicker.value!!)
                _wasLastAPICallSuccessful.value = true
                when(typeOfUpdate){
                    "end" -> {
                        _hasPickingTimerAlreadyStarted.value = false
                    }

                    "update" ->{
                        _hasPickingTimerAlreadyStarted.value = true
                    }
                }
            }

        }catch (e: Exception){
            _wasLastAPICallSuccessful.value = false
            Log.i("Update Picker Timer - Item Picking (e) " , "Error -> ${e.message}")
        }
    }


    fun getAllOrdersThatHavePickingForSuggestions(){
        // Exception handler for API call
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Get All orders for suggestions - Item Picking (exceptionHandler) " , "Error -> ${exception.message}")
        }
        // API call to get the products in order
        try {
            viewModelScope.launch(exceptionHandler) {
                val response = ScannerAPI.getItemPickingForDispatchService().getAllOrdersInPickingForSuggestion(_companyID.value!!)
                val ordersInPicking = response.response.ordersThatAreInPicking.ordersThatAreInPicking
                _ordersThatHavePicking.value = ordersInPicking
                _wasLastAPICallSuccessful.value = true
            }

        }catch (e: Exception){
            _wasLastAPICallSuccessful.value = false
            Log.i("Get All orders for suggestions - Item Picking (e) " , "Error -> ${e.message}")
        }
    }
}