package com.comdist.cdihandheldscannerviewactivity.Utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel: ViewModel(){
    // LiveData and MutableLiveData for various UI states and data
    protected val _wasLastAPICallSuccessful = MutableLiveData<Boolean>()
    val wasLastAPICallSuccessful: LiveData<Boolean>
        get() = _wasLastAPICallSuccessful

    // Error Message
    protected val _errorMessage = MutableLiveData<MutableMap<String, String>>()
    val errorMessage: LiveData<MutableMap<String, String>>
        get() = _errorMessage

    // Company ID
    protected val _companyID =  MutableLiveData<String>()
    val companyID: LiveData<String>
        get() = _companyID

    // Company ID
    protected val _warehouseNumber =  MutableLiveData<Int>()
    val warehouseNumber: LiveData<Int>
        get() = _warehouseNumber

    protected val _hasAPIBeenCalled = MutableLiveData<Boolean>()
    val hasAPIBeenCalled:LiveData<Boolean>
        get() = _hasAPIBeenCalled


    fun setCompanyIDFromSharedPref(companyID: String){
        _companyID.value = companyID
    }

    fun setWarehouseFromSharedPref(warehouse: Int){
        _warehouseNumber.value = warehouse
    }

    fun resetHasAPIBeenCalled(){
        _hasAPIBeenCalled.value = false
    }

    init {
        resetHasAPIBeenCalled()
    }


}