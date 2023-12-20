package com.example.cdihandheldscannerviewactivity.BarcodeAssign

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdihandheldscannerviewactivity.Utils.Network.BinInfo
import com.example.cdihandheldscannerviewactivity.Utils.Network.ItemDetailsForBinSearch
import com.example.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import com.example.cdihandheldscannerviewactivity.Utils.Network.WarehouseInfo
import com.example.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.util.Scanner
import kotlin.Exception

class BarcodeAssignViewModel: ViewModel() {

    // Adapter Position
    private val _currentlyChosenAdapterPosition = MutableLiveData<Int>()
    val currentlyChosenAdapterPosition: LiveData<Int>
        get() = _currentlyChosenAdapterPosition

    // Item number searched
    private val _itemNumber = MutableLiveData<String>()
    val itemNumber: LiveData<String>
        get() = _itemNumber

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