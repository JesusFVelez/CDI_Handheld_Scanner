package com.example.cdihandheldscannerviewactivity.BarcodeAssign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AssignBarcodeViewModel: ViewModel() {

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