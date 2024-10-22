package com.scannerapp.cdihandheldscannerviewactivity.SetNetworkDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NetworkDetailsViewModel: ViewModel() {
    private val _hasConnectionToBackendSucceeded = MutableLiveData<Boolean>()
    val hasConnectionToBackendSucceeded : LiveData<Boolean>
        get() = _hasConnectionToBackendSucceeded

    private val _wasLastAPICallSuccessful = MutableLiveData<Boolean>()
    val wasLastAPICallSuccessful : LiveData<Boolean>
        get() = _wasLastAPICallSuccessful

    private val _networkErrorMessage = MutableLiveData<String>()
    val networkErrorMessage : LiveData<String>
        get() = _networkErrorMessage

    init{
        _hasConnectionToBackendSucceeded.value = false
        _networkErrorMessage.value = ""
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