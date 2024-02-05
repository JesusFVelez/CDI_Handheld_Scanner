package com.example.cdihandheldscannerviewactivity.BarcodeAssign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cdihandheldscannerviewactivity.Utils.Network.GetItem
import com.example.cdihandheldscannerviewactivity.Utils.Network.ResponseWasItemFound

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

    fun validateItem(itemNumber: String){
        validateItem(itemNumber)

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