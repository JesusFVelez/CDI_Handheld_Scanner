package com.example.cdihandheldscannerviewactivity.BarcodeAssign

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdihandheldscannerviewactivity.Utils.Network.GetItem
import com.example.cdihandheldscannerviewactivity.Utils.Network.ResponseWasItemFound
import com.example.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.util.Scanner

class AssignBarcodeViewModel: ViewModel() {

    // Adapter Position
    private val _currentlyChosenAdapterPosition = MutableLiveData<Int>()
    val currentlyChosenAdapterPosition: LiveData<Int>
        get() = _currentlyChosenAdapterPosition

    //******************//
    // API Calls Values //
    //******************//

    // LiveData for wasItemFound API call //

    private val _wasItemFound = MutableLiveData<Boolean>()
    val wasItemFound: LiveData<Boolean>
        get() = _wasItemFound

    // LiveData for getItem API call //

    private val _itemInfo = MutableLiveData<List<GetItem>>()
    val itemInfo: LiveData<List<GetItem>>
        get() = _itemInfo

    // LiveData for validateBarcode API call //

    private val _isBarcodeValid = MutableLiveData<Boolean>()
    val isBarcodeValid: MutableLiveData<Boolean>
        get() = _isBarcodeValid

    private val _isBarcodeValidError = MutableLiveData<String>()
    val isBarcodeValidError: MutableLiveData<String>
        get() = _isBarcodeValidError

    // LiveData for setBarcode API call //

    private val _isBarcodeSet = MutableLiveData<Boolean>()
    val isBarcodeSet: MutableLiveData<Boolean>
        get() = _isBarcodeSet

    private val _isBarcodeSetError = MutableLiveData<String>()
    val isBarcodeSetError: MutableLiveData<Boolean>
        get() = _isBarcodeSet

    //*********************//
    // API Calls Functions //
    //*********************//

    //Validate Item API Call//
    fun validateItem(itemNumber: String){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        _wasItemFound.value = false
        Log.i("validate item for Barcode Assign API Call Exception Handler", "Error -> ${exception.message}")
        }
        viewModelScope.launch (exceptionHandler){
            try{
                val response = ScannerAPI.getAssignBarcodeService().wasItemFound(itemNumber)
                _wasItemFound.value = response.response.wasItemFound
            } catch(e: Exception){
                _wasItemFound.value = false
                Log.i("get validate item details for Barcode Assign API Call Exception Handler", "Error -> ${e.message}")
            }
        }

    }

    //Get Item API Call//
    fun setItem(itemNumber: String){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.i("set item for Barcode Assign API Call Exception Handler", "Error -> ${exception.message}")
        }

        viewModelScope.launch(exceptionHandler){
            try{
                val response = ScannerAPI.getAssignBarcodeService().getItems(itemNumber)
                // Need to finish this ////////////////////////////////////////////////////////////////////////////////
            } catch(e: Exception) {
                Log.i("get set item for Barcode Assign API Call Exception Handler", "Error -> ${e.message}")
            }
        }
    }
    //Validate Barcode API Call//
    fun validateBarcode(barcode: String){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _isBarcodeValid.value = false
            Log.i("validate barcode for Barcode Assign API Call Exception Handler", "Error -> ${exception.message}")
        }

        viewModelScope.launch(exceptionHandler) {
            try{
                val response = ScannerAPI.getAssignBarcodeService().validateBarcode(barcode)
                _isBarcodeValid.value = response.response.validation
                _isBarcodeValidError.value = response.response.errorMessage
            } catch(e: Exception){
                _isBarcodeValid.value = false
                Log.i("get validate barcode details for Barcode Assign Exception Handler", "Error -> ${e.message}")
        }
        }
    }

    //Set Barcode API Call//
    fun setBarcode(itemNumber: String, barcode: String){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _isBarcodeSet.value = false
            Log.i("set varcode for Barcode Assign API Call Exception Handler", "Error -> ${exception.message}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val response = ScannerAPI.getAssignBarcodeService().setBarcode(itemNumber, barcode)
                _isBarcodeSet.value = response.response.wasBarcodeAssigned
                _isBarcodeSetError.value = response.response.errorMessage
            } catch (e: Exception) {
                _isBarcodeSet.value = false
                Log.i("get validate barcode details for Barcode Assign API Call Exception Handler", "Error -> ${e.message}")
            }
        }
    }


    fun setAdapterPosition(position: Int) {
        if (position >= 0)
            _currentlyChosenAdapterPosition.value = position
        else
            _currentlyChosenAdapterPosition.value = 0
    }

    // Function called when ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
    }
}