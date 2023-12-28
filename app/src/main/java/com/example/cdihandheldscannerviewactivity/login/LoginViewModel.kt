package com.example.cdihandheldscannerviewactivity.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.example.cdihandheldscannerviewactivity.Utils.Network.Company
import com.example.cdihandheldscannerviewactivity.Utils.Network.NetworkDetailsResponseWrapper
import com.example.cdihandheldscannerviewactivity.Utils.Network.RequestUser
import com.example.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import com.example.cdihandheldscannerviewactivity.Utils.Network.User
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

    private val _wasLastAPICallSuccessful = MutableLiveData<Boolean>()
    val wasLastAPICallSuccessful : LiveData<Boolean>
        get() = _wasLastAPICallSuccessful

    private val _hasTooManyUsersConnected = MutableLiveData<Boolean>()
    val hasTooManyUsersConnected: LiveData<Boolean>
        get() = _hasTooManyUsersConnected

    private val _canUserLogin = MutableLiveData<Boolean>()
    val canUserLogin: LiveData<Boolean>
        get() = _canUserLogin
    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    // This variable indicates whether the arrow of the spinner is up or not
    var isSpinnerArrowUp: Boolean = false //TODO consider making this variable a LiveData

    // This initializer block is executed when the ViewModel is created. It calls the method to fetch companies from the backend
    init {
        getCompaniesFromBackendForSpinner()
    }

    // This method is called when the ViewModel is about to be destroyed. It's typically used to clean up any resources that the ViewModel is using
    override fun onCleared() {
        super.onCleared()

    }


    fun setUserValues(userName: String, password:String, companyID:String){
        _user.value = User(
            userName,
            password,
            companyID
        )
    }

    fun canLoginWithCredentials(requestBody: RequestUser){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Login" , "Error -> ${exception.message}")
        }
        viewModelScope.launch(exceptionHandler) {
            try{
                val response = ScannerAPI.getLoginService().isLoggedIn(requestBody)
                _canUserLogin.value = response.response.isSignedIn
                _wasLastAPICallSuccessful.value = true
            }catch (e: Exception){
                Log.i("Login", "Error -> ${e.message}")
                _wasLastAPICallSuccessful.value = false
            }
        }
    }

    fun verifyIfTooManyUsersConnected(){

        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Verify User Count" , "Error -> ${exception.message}")
        }
        viewModelScope.launch(exceptionHandler) {
            try{
                val response = ScannerAPI.getLoginService().verifyIfNumberOfUsersHasExceeded()
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