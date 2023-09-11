package com.example.cdihandheldscannerviewactivity.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdihandheldscannerviewactivity.networkUtils.Company
import com.example.cdihandheldscannerviewactivity.networkUtils.ScannerAPI
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginViewModel:ViewModel() {

    private val _listOfCompanies = MutableLiveData<List<Company>>()
    val listOfCompanies : LiveData<List<Company>>
        get() = _listOfCompanies


    // TODO Consider if maybe placing this value as LiveData
    var isSpinnerArrowUp: Boolean = false
    init {
        getCompaniesFromBackendForSpinner()
    }

    override fun onCleared() {
        super.onCleared()

    }
    fun getCompaniesFromBackendForSpinner() {

        viewModelScope.launch {
            try{
                var response = ScannerAPI.retrofitService.getCompanies()
                _listOfCompanies.value = response.response.companies.companies

            }catch (e: Exception){
                Log.i("Login viewModelScope.launch method", "Error -> ${e.message}")
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