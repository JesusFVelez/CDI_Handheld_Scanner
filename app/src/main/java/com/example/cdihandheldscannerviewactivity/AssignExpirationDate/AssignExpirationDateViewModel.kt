package com.example.cdihandheldscannerviewactivity.AssignExpirationDate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AssignExpirationDateViewModel: ViewModel(){

        // LiveData and MutableLiveData for various UI states and data
        private val _wasLastAPICallSuccessful = MutableLiveData<Boolean>()
        val wasLastAPICallSuccessful : LiveData<Boolean>
            get() = _wasLastAPICallSuccessful


        init{
        }

    }