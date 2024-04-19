package com.comdist.cdihandheldscannerviewactivity.HomeScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comdist.cdihandheldscannerviewactivity.R
import com.comdist.cdihandheldscannerviewactivity.Utils.BaseViewModel
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.lang.Exception


class HomeScreenViewModel : BaseViewModel(){

    // Hardcoded Menu Options with the action ids, names and function names
    class MenuOptions{
        companion object{
                val ProductInBinMenuOption = MenuOptionDataClass("Product in Bin", R.id.action_homeScreenFragment_to_productToBinFragment, "RfBtnCheckBin")
                val BinsWithProductMenuOption = MenuOptionDataClass("Search Bins With Product",R.id.action_homeScreenFragment_to_searchBinsWithProductFragment, "RfBtnCheckItem" )
                val ItemPickingMenuOption = MenuOptionDataClass("Item Picking", R.id.action_homeScreenFragment_to_orderPickingMainFragment, "RfBtnITEMPICKING")
                val AssignBarcodeMenuOption = MenuOptionDataClass("Assign Barcode", R.id.action_homeScreenFragment_to_assignBarcodeToProductFragment, "RfBtnAssignBarcode")
                val BinToBinMovementOption = MenuOptionDataClass("Bin to Bin Movement", R.id.action_homeScreenFragment_to_binMovementFragment, "RfBtnWHRFMOV")
                val EditItemMenuOption = MenuOptionDataClass("Assign Expiration", R.id.action_homeScreenFragment_to_assignExpirationDateFragment, "RfBtnAssignExpirationDate")
        }
    }
    data class MenuOptionDataClass(
        var menuOptionName: String,
        var menuOptionNavigationAction:Int,
        var menuOptionFunctionName:String
    )

    private val _doesClientUseRPM = MutableLiveData<Boolean>()
    val doesClientUseRPM : LiveData<Boolean>
        get() = _doesClientUseRPM

    // Picker User Name
    private val _userNameOfPicker =  MutableLiveData<String>()
    val userNameOfPicker: LiveData<String>
        get() = _userNameOfPicker


    private val _doesUserHaveAccessToMenuOption = MutableLiveData<Boolean>()
    val doesUserHaveAccessToMenuOption:LiveData<Boolean>
        get() = _doesUserHaveAccessToMenuOption

    private val _errorMessageForUserAccess = MutableLiveData<String>()
    val errorMessageForUserAccess: LiveData<String>
        get() = _errorMessageForUserAccess


    private val _currentlyChosenMenuOption = MutableLiveData<MenuOptionDataClass>()
    val currentlyChosenMenuOption: LiveData<MenuOptionDataClass>
        get() = _currentlyChosenMenuOption





    fun setUserNameOfPickerFromSharedPref(userNameOfPicker: String){
        _userNameOfPicker.value = userNameOfPicker
    }

    fun setCurrentlyChosenMenuOption(menuOption: MenuOptionDataClass){
        _currentlyChosenMenuOption.value = menuOption
    }

    init{

    }

    fun verifyIfClientUsesRPM(){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Does User have RPM" , "Error -> ${exception.message}")
        }
        viewModelScope.launch(exceptionHandler) {
            try{
                _hasAPIBeenCalled.value = true
                val response = ScannerAPI.getRPMAccessService().verifyIfClientUsesRPM(_companyID.value!!)
                _wasLastAPICallSuccessful.value = true
                _doesClientUseRPM.value = response.response.doesClientUseRPM
            }catch (e: Exception){
                Log.i("Does User have RPM viewModelScope.launch method", "Error -> ${e.message}")
                _wasLastAPICallSuccessful.value = false
            }
        }
    }

    fun verifyInBackendIfUserHasAccessToMenuOption(menuOption: MenuOptionDataClass){
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("RPM Access" , "Error -> ${exception.message}")
        }
        viewModelScope.launch(exceptionHandler) {
            try{
                _hasAPIBeenCalled.value = true
                val response = ScannerAPI.getRPMAccessService().checkIfUserHasAccessToFunctionality(_userNameOfPicker.value!!, menuOption.menuOptionFunctionName, _companyID.value!!)
                _wasLastAPICallSuccessful.value = true
                _errorMessageForUserAccess.value = response.response.errorMessage
                _doesUserHaveAccessToMenuOption.value = response.response.doesUserHaveAccessToFunc
            }catch (e: Exception){
                Log.i("RPMAccess viewModelScope.launch method", "Error -> ${e.message}")
                _wasLastAPICallSuccessful.value = false
            }
        }
    }



}