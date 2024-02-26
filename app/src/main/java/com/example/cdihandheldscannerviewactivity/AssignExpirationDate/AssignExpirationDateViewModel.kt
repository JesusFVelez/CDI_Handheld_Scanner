package com.example.cdihandheldscannerviewactivity.AssignExpirationDate

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class AssignExpirationDateViewModel: ViewModel(){

        public val _opSuccess = MutableLiveData<Boolean>()
        val assignExpirationDate : LiveData<Boolean>
            get() = _opSuccess

        public val _opMessage = MutableLiveData<String>()
        val listOfCompanies : LiveData<String>
            get() = _opMessage

        public val _wasLastAPICallSuccessful = MutableLiveData<Boolean>()
        val wasLastAPICallSuccessful : LiveData<Boolean>
            get() = _wasLastAPICallSuccessful


    init{
    }

    fun assignExpirationDate(pItemNumber: String, pBinLocation: String, pExpireDate: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Assign Expire Date " , "Error -> ${exception.message}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getAssignExpirationDateResources().assignExpireDate(pItemNumber, pBinLocation, pExpireDate)
                _opSuccess.value = response.response.opSuccess
                _opMessage.value = response.response.opMessage
                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                Log.i("Assign Expire Date (e)", "Error -> ${e.message}")
                _wasLastAPICallSuccessful.value = false
            }
        }
    }


}