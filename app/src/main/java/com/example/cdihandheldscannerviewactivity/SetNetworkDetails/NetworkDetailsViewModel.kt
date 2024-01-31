package com.example.cdihandheldscannerviewactivity.SetNetworkDetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.lang.Exception

class NetworkDetailsViewModel: ViewModel() {
    private val _hasConnectionToBackendSucceeded = MutableLiveData<Boolean>()
    val hasConnectionToBackendSucceeded : LiveData<Boolean>
        get() = _hasConnectionToBackendSucceeded

    private val _wasLastAPICallSuccessful = MutableLiveData<Boolean>()
    val wasLastAPICallSuccessful : LiveData<Boolean>
        get() = _wasLastAPICallSuccessful

    init{
        _hasConnectionToBackendSucceeded.value = false
    }

//    fun verifyBackendConnection(){
//        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
//            _wasLastAPICallSuccessful.value = false
//            Log.i("Verify Backend Connection" , "Error -> ${exception.message}")
//        }
//        viewModelScope.launch(exceptionHandler) {
//            try{
//
//                val response = ScannerAPI.getLoginService().testConnection()
//                _hasConnectionToBackendSucceeded.value = response
//                _wasLastAPICallSuccessful.value = true
//
//            }catch (e: Exception){
//                Log.i("Verify Backend Connection", "Error -> ${e.message}")
//                _wasLastAPICallSuccessful.value = false
//            }
//        }
//    }

}