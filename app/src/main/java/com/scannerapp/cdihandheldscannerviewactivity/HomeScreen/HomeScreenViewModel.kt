package com.scannerapp.cdihandheldscannerviewactivity.HomeScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scannerapp.cdihandheldscannerviewactivity.R
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch


class HomeScreenViewModel : ViewModel(){

    // Hardcoded Menu Options with the action ids, names and function names
    class MenuOptions{
        companion object{
                val ProductInBinMenuOption = MenuOptionDataClass("Product in Bin", R.id.action_homeScreenFragment_to_productToBinFragment, "RfBtnCheckBin")
                val BinsWithProductMenuOption = MenuOptionDataClass("Search Bins With Product",R.id.action_homeScreenFragment_to_searchBinsWithProductFragment, "RfBtnCheckItem" )
                val ItemPickingMenuOption = MenuOptionDataClass("Item Picking", R.id.action_homeScreenFragment_to_orderPickingMainFragment, "RfBtnITEMPICKING")
                val BinToBinMovementOption = MenuOptionDataClass("Bin to Bin Movement", R.id.action_homeScreenFragment_to_binMovementFragment, "RfBtnBinMov")
                val EditItemMenuOption = MenuOptionDataClass("Assign Expiration", R.id.action_homeScreenFragment_to_editItemMainFragment, "RfBtnEditItem")
                val ReceivingMenuOption = MenuOptionDataClass("Receiving", R.id.action_homeScreenFragment_to_receivingProductsMainFragment, "RfBtnReceiving")
                val PhysicalCountMenuOption = MenuOptionDataClass("Physical Count", R.id.searchBinProductPhysicalCountFragment, "RfBtnItemPhysicalCount")
        }
    }
    data class MenuOptionDataClass(
        var menuOptionName: String,
        var menuOptionNavigationAction:Int,
        var menuOptionFunctionName:String
    )

    private val _wasLastAPICallSuccessful = MutableLiveData<Boolean>()
    val wasLastAPICallSuccessful : LiveData<Boolean>
        get() = _wasLastAPICallSuccessful

    private val _doesClientUseRPM = MutableLiveData<Boolean>()
    val doesClientUseRPM : LiveData<Boolean>
        get() = _doesClientUseRPM

    private val _networkErrorMessage = MutableLiveData<String>()
    val networkErrorMessage : LiveData<String>
        get() = _networkErrorMessage

    // Picker User Name
    private val _userNameOfPicker =  MutableLiveData<String>()
    val userNameOfPicker: LiveData<String>
        get() = _userNameOfPicker


    // Company ID
    private val _companyID =  MutableLiveData<String>()
    val companyID: LiveData<String>
        get() = _companyID

    private val _doesUserHaveAccessToMenuOption = MutableLiveData<Boolean>()
    val doesUserHaveAccessToMenuOption:LiveData<Boolean>
        get() = _doesUserHaveAccessToMenuOption

    private val _errorMessageForUserAccess = MutableLiveData<String>()
    val errorMessageForUserAccess: LiveData<String>
        get() = _errorMessageForUserAccess


    private val _currentlyChosenMenuOption = MutableLiveData<MenuOptionDataClass>()
    val currentlyChosenMenuOption: LiveData<MenuOptionDataClass>
        get() = _currentlyChosenMenuOption

    private val _didUserLogOutSuccessfully = MutableLiveData<Boolean>()
    val didUserLogoutSuccessfully: LiveData<Boolean>
        get() = _didUserLogOutSuccessfully






    fun setCompanyIDFromSharedPref(companyID: String){
        _companyID.value = companyID
    }
    fun setUserNameOfPickerFromSharedPref(userNameOfPicker: String){
        _userNameOfPicker.value = userNameOfPicker
    }

    fun setCurrentlyChosenMenuOption(menuOption: MenuOptionDataClass){
        _currentlyChosenMenuOption.value = menuOption
    }

    init{
        _didUserLogOutSuccessfully.value = false
    }

    fun logUserOut(){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            _didUserLogOutSuccessfully.value = false
            Log.i("Log out user homescreen viewmodel" , "Error -> ${exception.message}")
        }
        viewModelScope.launch(exceptionHandler) {
            try{
                ScannerAPI.getLoginService().logoutUser(_companyID.value!!)
                _didUserLogOutSuccessfully.value = true
                _wasLastAPICallSuccessful.value = true
            }catch (e: Exception){
                _networkErrorMessage.value = e.message
                Log.i("Log out user (e) homescreen viewmodel", "Error -> ${e.message}")
                _didUserLogOutSuccessfully.value = false
                _wasLastAPICallSuccessful.value = false
            }
        }
    }

    fun verifyIfClientUsesRPM(){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            Log.i("Does User have RPM" , "Error -> ${exception.message}")
        }
        viewModelScope.launch(exceptionHandler) {
            try{
                val response = ScannerAPI.getRPMAccessService().verifyIfClientUsesRPM(_companyID.value!!)
                _wasLastAPICallSuccessful.value = true
                _doesClientUseRPM.value = response.response.doesClientUseRPM
            }catch (e: Exception){
                _networkErrorMessage.value = e.message
                Log.i("Does User have RPM viewModelScope.launch method", "Error -> ${e.message}")
                _wasLastAPICallSuccessful.value = false
            }
        }
    }

    fun verifyInBackendIfUserHasAccessToMenuOption(menuOption: MenuOptionDataClass){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            Log.i("RPM Access" , "Error -> ${exception.message}")
        }
        viewModelScope.launch(exceptionHandler) {
            try{
                val response = ScannerAPI.getRPMAccessService().checkIfUserHasAccessToFunctionality(_userNameOfPicker.value!!, menuOption.menuOptionFunctionName, _companyID.value!!)
                _wasLastAPICallSuccessful.value = true
                _errorMessageForUserAccess.value = response.response.errorMessage
                _doesUserHaveAccessToMenuOption.value = response.response.doesUserHaveAccessToFunc
            }catch (e: Exception){
                _networkErrorMessage.value = e.message
                Log.i("RPMAccess viewModelScope.launch method", "Error -> ${e.message}")
                _wasLastAPICallSuccessful.value = false
            }
        }
    }



}