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

    private val _listOfItemsInOrder = MutableLiveData<List<ItemsInOrderInfo>>() // This string will change to be an object with all the details of an item in an order
    val listOfItemsInOrder: LiveData<List<ItemsInOrderInfo>>
        get() = _listOfItemsInOrder

    private val _companyID =  MutableLiveData<String>()
    val companyID: LiveData<String>
        get() = _companyID

    private val _userNameOfPicker =  MutableLiveData<String>()
    val userNameOfPicker: LiveData<String>
        get() = _userNameOfPicker

    private val _wasLastAPICallSuccessful = MutableLiveData<Boolean>()
    val wasLastAPICallSuccessful : LiveData<Boolean>
        get() = _wasLastAPICallSuccessful

    // Order validation
    private val _wasOrderFound : MutableLiveData<Boolean>()
    val wasOrderFound : LiveData<Boolean>
        get() = _wasOrderFound


    init {

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