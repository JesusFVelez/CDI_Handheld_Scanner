package com.example.cdihandheldscannerviewactivity.AssignExpirationDate

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdihandheldscannerviewactivity.Utils.Network.ItemInfo
import com.example.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.util.Date

class AssignExpirationDateViewModel: ViewModel(){

        private val _opSuccess = MutableLiveData<Boolean>()
        val opSuccess : LiveData<Boolean>
            get() = _opSuccess


        private val _opMessage = MutableLiveData<String>()
        val opMessage : LiveData<String>
            get() = _opMessage

        private val _itemInfo = MutableLiveData<ItemInfo>()
        val itemInfo : LiveData<ItemInfo>
            get() = _itemInfo


    init{
    }

    fun assignExpirationDate(pItemNumber: String, pBinLocation: String, pExpireDate: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.i("Assign Expire Date " , "Error -> ${exception.message}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getAssignExpirationDateService().assignExpireDate(pItemNumber, pBinLocation, pExpireDate)
                _opSuccess.value = response.response.opSuccess
                _opMessage.value = response.response.opMessage
            } catch (e: Exception) {
                Log.i("Assign Expire Date (e)", "Error -> ${e.message}")
            }
        }
    }

    fun getItemInfo(pItemNumber: String, itemDescription: String, pBinLocation: String, pExpireDate: Date){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.i("Item Info " , "Error -> ${exception.message}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getAssignExpirationDateService().getItemInformation(pItemNumber, pBinLocation)
                _itemInfo.value = response.response.binItemInfo.response
            }catch (e: Exception){
                Log.i("Item Info", "Error -> ${e.message}")
            }

        }
    }
}