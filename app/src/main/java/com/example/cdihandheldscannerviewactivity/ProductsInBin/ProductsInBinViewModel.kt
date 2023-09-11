package com.example.cdihandheldscannerviewactivity.ProductsInBin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdihandheldscannerviewactivity.networkUtils.ProductInBinInfo
import com.example.cdihandheldscannerviewactivity.networkUtils.ScannerAPI
import com.example.cdihandheldscannerviewactivity.networkUtils.WarehouseInfo
import kotlinx.coroutines.launch
import java.lang.Exception

class ProductsInBinViewModel: ViewModel() {

    private val _wasBinFound = MutableLiveData<Boolean>()
    val wasBinFound : LiveData<Boolean>
        get() = _wasBinFound


    private val _listOfProducts = MutableLiveData<List<ProductInBinInfo>>()
    val listOfProducts : LiveData<List<ProductInBinInfo>>
        get() = _listOfProducts


    private val _isSpinnerArrowUp =  MutableLiveData<Boolean>()
    val isSpinnerArrowUp : LiveData<Boolean>
        get() = _isSpinnerArrowUp


    private val _listOfWarehouses = MutableLiveData<List<WarehouseInfo>>()
    val listOfWarehouses : LiveData<List<WarehouseInfo>>
        get() = _listOfWarehouses


    private val _currentWarehouseNumber = MutableLiveData<Int>()
    val currentWarehouseNumber: LiveData<Int>
        get() = _currentWarehouseNumber


    private val _numberOfItemsInBin = MutableLiveData<Int>()
    val numberOfItemsInBin : LiveData<Int>
        get() = _numberOfItemsInBin

    private val _currentlyChosenAdapterPosition = MutableLiveData<Int>()
    val currentlyChosenAdapterPosition: LiveData<Int>
        get() = _currentlyChosenAdapterPosition


    fun setChosenAdapterPosition(position : Int){
        if (position >= 0)
            _currentlyChosenAdapterPosition.value = position
        else
            _currentlyChosenAdapterPosition.value = 0
    }

    init {
        getWarehousesFromBackendForSpinner()
        _isSpinnerArrowUp.value = false
        _currentWarehouseNumber.value = 0
        _numberOfItemsInBin.value = 0
        _currentlyChosenAdapterPosition.value = 0
    }


    override fun onCleared() {
        super.onCleared()
    }

    fun setIsSpinnerArrowUp(isSpinnerUp: Boolean){
        _isSpinnerArrowUp.value = isSpinnerUp
    }

    private fun setWarehouseNumber(selectedWarehouseInSpinner : String){
        for(aWarehouse in listOfWarehouses.value!!){
            if (selectedWarehouseInSpinner == aWarehouse.warehouseName){
                _currentWarehouseNumber.value = aWarehouse.warehouseNumber.toInt()
            }
        }
    }

    // Note: I am currently hardcoding the companyCode due to time constraints but now I'm starting to see that it might be necessary to make a database
    fun getProductInfoFromBackend(warehouseName : String, binNumber : String ){
        setWarehouseNumber(warehouseName)
        try{
            viewModelScope.launch {
                val response = ScannerAPI.retrofitService.getAllItemsInBin("F", _currentWarehouseNumber.value!!, binNumber )
                _listOfProducts.value = response.response.itemsInBin.itemsInBin
                _wasBinFound.value = response.response.wasBinFound
                _numberOfItemsInBin.value = _listOfProducts.value!!.size
                Log.i("Products In Bin View Model Product Info API Call", "Response -> ${response.toString()}" )
            }
        }catch (e : Exception){
            Log.i("Products In Bin View Model Product Info API Call" , "Error -> ${e.message}")
        }
    }

    private fun getWarehousesFromBackendForSpinner(){
        viewModelScope.launch {
            try{
                val response = ScannerAPI.retrofitService.getWarehousesAvailable()
                _listOfWarehouses.value = response.response.warehouses.warehouses
            }catch (e: Exception){
                Log.i("Products In Bin View Model WH API Call", "Error -> ${e.message}")
            }
        }
    }


}