package com.scannerapp.cdihandheldscannerviewactivity.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.Company
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.WarehouseInfo
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.RequestUser
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.User
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class LoginViewModel:ViewModel() {

    // These are LiveData objects that hold the list of companies and the status of the last API call
    // LiveData allows data to be observed for changes, which is useful for updating the UI in response to changes
    private val _listOfCompanies = MutableLiveData<List<Company>>()
    val listOfCompanies : LiveData<List<Company>>
        get() = _listOfCompanies

    private val _currentlyChosenCompany = MutableLiveData<Company>()
    val currentlyChosenCompany: LiveData<Company>
        get() = _currentlyChosenCompany

    private val _listOfWarehouses = MutableLiveData<List<WarehouseInfo>>()
    val listOfWarehouses : LiveData<List<WarehouseInfo>>
        get() = _listOfWarehouses

    private val _currentlyChosenWarehouse = MutableLiveData<WarehouseInfo>()
    val currentlyChosenWarehouse: LiveData<WarehouseInfo>
        get() = _currentlyChosenWarehouse

    private val _wasLastAPICallSuccessful = MutableLiveData<Boolean>()
    val wasLastAPICallSuccessful : LiveData<Boolean>
        get() = _wasLastAPICallSuccessful

    private val _hasTooManyUsersConnected = MutableLiveData<Boolean>()
    val hasTooManyUsersConnected: LiveData<Boolean>
        get() = _hasTooManyUsersConnected

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean>
        get() = _isUserLoggedIn
    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user


    // This initializer block is executed when the ViewModel is created. It calls the method to fetch companies from the backend
    init {
        _listOfCompanies.value = listOf()
        _listOfWarehouses.value = listOf()
        getCompaniesFromBackendForSpinner()
        getWarehousesFromBackendForSpinner()
    }


    fun setUserValues(userName: String, password:String, companyID:String, warehouseNumber:Int){
        _user.value = User(
            userName,
            password,
            companyID,
            warehouseNumber

        )
    }

    fun verifyIfTooManyUsersConnected(){

        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Verify User Count" , "Error -> ${exception.message}")
        }
        viewModelScope.launch(exceptionHandler) {
            try{
                val response = ScannerAPI.getLoginService().verifyIfNumberOfUsersHasExceeded(_user.value!!.company)
                _hasTooManyUsersConnected.value = response.response.hasTooManyConnections
                _wasLastAPICallSuccessful.value = true

            }catch (e: Exception){
                Log.i("Verify User Count", "Error -> ${e.message}")
                _wasLastAPICallSuccessful.value = false
            }
        }
    }



    // This method fetches the list of companies from the backend using a coroutine. If an error occurs during the API call, it logs the error and updates the LiveData object to indicate that the API call was unsuccessful
    fun getCompaniesFromBackendForSpinner() {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("get Companies API Call" , "Error -> ${exception.message}")
        }
        viewModelScope.launch(exceptionHandler) {
            try{
                val response = ScannerAPI.getLoginService().getCompanies()
                _listOfCompanies.value = response.response.companies.companies
                _wasLastAPICallSuccessful.value = true

            }catch (e: Exception){
                Log.i("Login viewModelScope.launch method", "Error -> ${e.message}")
                _wasLastAPICallSuccessful.value = false
            }
        }
    }



    fun setCompanyID(selectedCompanyInSpinner: String){
        for (aCompany in _listOfCompanies.value!!) {
            if (selectedCompanyInSpinner == aCompany.companyName) {
                _currentlyChosenCompany.value = Company(aCompany.companyID,aCompany.companyName)
            }
        }
    }

    fun setWarehouse(selectedWarehouseInSpinner : String){
        for(aWarehouse in listOfWarehouses.value!!){
            if (selectedWarehouseInSpinner == aWarehouse.warehouseName){
                _currentlyChosenWarehouse.value = WarehouseInfo(aWarehouse.warehouseNumber, aWarehouse.warehouseName)
            }
        }
    }


    fun getWarehousesFromBackendForSpinner(){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Login Warehouse" , "Error -> ${exception.message}")
        }

        // API call to get list of warehouses
        viewModelScope.launch (exceptionHandler) {
            try{
                val response = ScannerAPI.getLoginService().getWarehousesAvailable()
                _listOfWarehouses.value = response.response.warehouses.warehouses
                _wasLastAPICallSuccessful.value = true
            }catch (e: Exception){
                _wasLastAPICallSuccessful.value = false
                Log.i("Login warehouse (e)", "Error -> ${e.message}")
            }
        }
    }

    fun logUserIn(username:String, password: String){

        val user = User(
            username,
            password,
            _currentlyChosenCompany.value!!.companyID,
            _currentlyChosenWarehouse.value!!.warehouseNumber
        )
        val requestBody = RequestUser(user)
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Login", "Error -> ${exception.message}")
        }
        // API call to get list of warehouses
        viewModelScope.launch (exceptionHandler) {
            try{
                val response = ScannerAPI.getLoginService().isLoggedIn(requestBody)
                _isUserLoggedIn.value = response.response.isSignedIn
                _wasLastAPICallSuccessful.value = true
            }catch (e: Exception){
                _wasLastAPICallSuccessful.value = false
                Log.i("Login (e)", "Error -> ${e.message}")
            }
        }
    }
}


//              How to call an API without a ViewModel
//        ScannerAPI.retrofitService.getCompanies().enqueue(object : Callback<ResponseWrapper> {
//            override fun onResponse(
//                call: Call<ResponseWrapper>,
//                response: Response<ResponseWrapper>
//            ) {
//                println(
//                    "Response" + (response.body()?.response?.companies?.companies?.get(0)
//                        .toString())
//                )
//                listOfCompanies = response.body()!!.response.companies.companies
//                //fillSpinnerWithCompanies()
//            }
//
//            override fun onFailure(call: Call<ResponseWrapper>, t: Throwable) {
//                println("Something failed: \n " + t.message)
////                Toast.makeText(this@loginActivity, "There has been an error with the connection", Toast.LENGTH_SHORT).show()
//            }
//        })