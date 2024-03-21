package com.example.cdihandheldscannerviewactivity.AssignExpirationDateAndLot

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdihandheldscannerviewactivity.Utils.Network.ItemData
import com.example.cdihandheldscannerviewactivity.Utils.Network.ItemInfo
import com.example.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class AssignExpirationDateAndLotNumberViewModel: ViewModel(){

        private val _opSuccess = MutableLiveData<Boolean>()
        val opSuccess : LiveData<Boolean>
            get() = _opSuccess


        private val _opMessage = MutableLiveData<String>()
        val opMessage : LiveData<String>
            get() = _opMessage

        private val _itemInfo = MutableLiveData<List<ItemInfo>>()
        val itemInfo : LiveData<List<ItemInfo>>
            get() = _itemInfo

    private val _wasLastAPICallSuccessful = MutableLiveData<Boolean>()
    val wasLastAPICallSuccessful : LiveData<Boolean>
        get() = _wasLastAPICallSuccessful

    private val _suggestions = MutableLiveData<List<ItemData>>()
    val suggestions: LiveData<List<ItemData>>
        get() = _suggestions

    init{}
    fun fetchSuggestionsForItemOrBin(query: String, callback: (List<ItemData>) -> Unit) {
        viewModelScope.launch {
            try {
                val response = ScannerAPI.getSuggestionService().getSuggestionsForItemOrBin(query)
                callback.invoke(response.response.binItemInfo)
            } catch (e: Exception) {
                Log.e("FetchSuggestions", "Failed to fetch suggestions: ${e.message}")

            }
        }
    }
    fun assignExpirationDate(pItemNumber: String, pBinLocation: String, pExpireDate: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Assign Expire Date " , "Error -> ${exception.message}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getAssignExpirationDateService().assignExpireDate(pItemNumber, pBinLocation, pExpireDate)
                _wasLastAPICallSuccessful.value = true
                _opMessage.value = response.response.opMessage
                _opSuccess.value = response.response.opSuccess
            } catch (e: Exception) {
                _wasLastAPICallSuccessful.value = false
                Log.i("Assign Expire Date (e)", "Error -> ${e.message}")
            }
        }
    }


    fun getItemInfo(pItemNumber: String, pBinLocation: String){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Item Info " , "Error -> ${exception.message}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getAssignExpirationDateService().getItemInformation(pItemNumber, pBinLocation, "")
                _wasLastAPICallSuccessful.value = true
                _opMessage.value = response.response.opMessage
                _itemInfo.value = response.response.binItemInfo.response
                _opSuccess.value = response.response.opSuccess
            }catch (e: Exception){
                _wasLastAPICallSuccessful.value = false
                Log.i("Item Info", "Error -> ${e.message}")
            }

        }
    }

}