package com.scannerapp.cdihandheldscannerviewactivity.ReceivingProductsIntoWarehouse

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.DoorBin
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemsInBinList
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.PreReceivingInfo
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ReceivingItemInfo
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import kotlin.math.abs

class ReceivingProductsViewModel: ViewModel() {

    // Add this variable
    private val _isQuantityLessThanPreReceiving = MutableLiveData<Boolean>()
    val isQuantityLessThanPreReceiving: LiveData<Boolean>
        get() = _isQuantityLessThanPreReceiving

    // LiveData to hold whether the quantity is different from Pre-Receiving
    private val _isQuantityDifferentFromPreReceiving = MutableLiveData<Boolean>()
    val isQuantityDifferentFromPreReceiving: LiveData<Boolean>
        get() = _isQuantityDifferentFromPreReceiving

    // LiveData to hold the Pre-Receiving quantity
    private val _preReceivingQuantity = MutableLiveData<Int>()
    val preReceivingQuantity: LiveData<Int>
        get() = _preReceivingQuantity

    // LiveData to hold any error messages
    private val _validateQuantityErrorMessage = MutableLiveData<String>()
    val validateQuantityErrorMessage: LiveData<String>
        get() = _validateQuantityErrorMessage

    // LiveData to signal when to display the quantity confirmation popup
    private val _shouldShowQuantityConfirmationPopup = MutableLiveData<Boolean>()
    val shouldShowQuantityConfirmationPopup: LiveData<Boolean>
        get() = _shouldShowQuantityConfirmationPopup

    private val _currentlyChosenAdapterPosition = MutableLiveData<Int>()
    val currentlyChosenAdapterPosition: LiveData<Int>
        get() = _currentlyChosenAdapterPosition

    //Info from shared preferences
    private val _warehouseNumber = MutableLiveData<Int>()
    val warehouseNumber: LiveData<Int>
        get() = _warehouseNumber

    private val _isDoorBinEmpty = MutableLiveData<Boolean>()
    val isDoorBinEmpty: LiveData<Boolean>
        get() = _isDoorBinEmpty

    // will hold the list to use in the adapter in the main screen
    private val _listOfItemsToMoveInPreReceiving = MutableLiveData<MutableList<ItemsInBinList>>()
    val listOfItemsToMoveInPreReceiving: LiveData<MutableList<ItemsInBinList>>
        get() = _listOfItemsToMoveInPreReceiving

    private val _networkErrorMessage = MutableLiveData<String>()
    val networkErrorMessage: LiveData<String>
        get() = _networkErrorMessage

    private val _wasItemConfirmed = MutableLiveData<Boolean>()
    val wasItemConfirmed: LiveData<Boolean>
        get() = _wasItemConfirmed

    private val _UOMQtyInBarcode = MutableLiveData<Float>()
    val UOMQtyInBarcode: LiveData<Float>
        get() = _UOMQtyInBarcode

    private val _willEditCurrentValues = MutableLiveData<Boolean>()
    val willEditCurrentValues: LiveData<Boolean>
        get() = _willEditCurrentValues

    private val _companyID = MutableLiveData<String>()
    val companyID: LiveData<String>
        get() = _companyID

    // Map to handle error messages
    private val _errorMessage = MutableLiveData<MutableMap<String, String>>()
    val errorMessage: LiveData<MutableMap<String, String>>
        get() = _errorMessage

    // Objects for temporary tables from backend
    private val _itemInfo = MutableLiveData<ReceivingItemInfo>()
    val itemInfo: LiveData<ReceivingItemInfo>
        get() = _itemInfo

    private val _doorBins = MutableLiveData<List<DoorBin>>()
    val doorBins: LiveData<List<DoorBin>>
        get() = _doorBins

    private val _preReceivingInfo = MutableLiveData<PreReceivingInfo>()
    val preReceivingInfo: LiveData<PreReceivingInfo>
        get() = _preReceivingInfo

    // Booleans for error handling
    private val _wasLastAPICallSuccessful = MutableLiveData<Boolean>()
    val wasLastAPICallSuccessful: LiveData<Boolean>
        get() = _wasLastAPICallSuccessful

    private val _wasPreReceivingFound = MutableLiveData<Boolean>()
    val wasPreReceivingFound: LiveData<Boolean>
        get() = _wasPreReceivingFound

    private val _wasItemFound = MutableLiveData<Boolean>()
    val wasItemFound: LiveData<Boolean>
        get() = _wasItemFound

    private val _wasBinFound = MutableLiveData<Boolean>()
    val wasBinFound: LiveData<Boolean>
        get() = _wasBinFound

    private val _wasItemMovedToDoor = MutableLiveData<Boolean>()
    val wasItemMovedToDoor: LiveData<Boolean>
        get() = _wasItemMovedToDoor

    private val _wasItemMovedToBin = MutableLiveData<Boolean>()
    val wasItemMovedFromBin: LiveData<Boolean>
        get() = _wasItemMovedToBin

    private val _isLotNumberValid = MutableLiveData<Boolean>()
    val isLotNumberValid: LiveData<Boolean>
        get() = _isLotNumberValid

    // Picker User name
    private val _pickerUserName = MutableLiveData<String>()
    val pickerUserName: LiveData<String>
        get() = _pickerUserName

    private val _hasAPIBeenCalled = MutableLiveData<Boolean>()
    val hasAPIBeenCalled: LiveData<Boolean>
        get() = _hasAPIBeenCalled

    private val _isValidDestinationBin = MutableLiveData<Boolean>()
    val isValidDestinationBin: LiveData<Boolean>
        get() = _isValidDestinationBin

    private val _currentlyChosenDoorBin = MutableLiveData<DoorBin>()
    val currentlyChosenDoorBin: LiveData<DoorBin>
        get() = _currentlyChosenDoorBin

    private val _itemsMoved = MutableLiveData<List<ItemsInBinList>>()
    val itemsMoved: LiveData<List<ItemsInBinList>>
        get() = _itemsMoved

    private val _allItemsMoved = MutableLiveData<Boolean>()
    val allItemsMoved: LiveData<Boolean>
        get() = _allItemsMoved

    private val _weightFromBarcode = MutableLiveData<Float>()
    val weightFromBarcode: LiveData<Float>
        get() = _weightFromBarcode


    private val _wasItemDeleted = MutableLiveData<Boolean>()
    val wasItemDeleted: LiveData<Boolean>
        get() = _wasItemDeleted

    private val _destinationBin = MutableLiveData<String>()
    val destinationBin: LiveData<String>
        get() = _destinationBin

    fun setPickerUserName(pickerUserName: String) {
        _pickerUserName.value = pickerUserName
    }


    init {
        _errorMessage.value = mutableMapOf(
            "wasItemFoundError" to "",
            "wasPreReceivingFoundError" to "",
            "wasBinFoundError" to "",
            "wasItemMovedToDoorError" to "",
            "wasItemMovedToBinError" to "",
            "getItemsInBin" to "",
            "validateLotNumber" to "",
            "confirmItem" to "",
            "wasItemDeleted" to "",
            "validateDestinationBin" to ""
        )
        _listOfItemsToMoveInPreReceiving.value = mutableListOf()
        willNotEditCurrentValues()
        _allItemsMoved.value = false
        _UOMQtyInBarcode.value = 0f
        _networkErrorMessage.value = ""
    }

    fun setCurrentlyChosenDoorBin(chosenDoorBin: DoorBin) {
        _currentlyChosenDoorBin.value = chosenDoorBin
    }

    fun willEditCurrentValues() {
        _willEditCurrentValues.value = true
    }

    fun willNotEditCurrentValues() {
        _willEditCurrentValues.value = false
    }


    fun resetHasAPIBeenCalled() {
        _hasAPIBeenCalled.value = false
    }

    fun setCurrentlyChosenItemAdapterPosition(position: Int) {
        _currentlyChosenAdapterPosition.value = position
    }

    // Get company and warehouse from shared preferences
    fun setCompanyIDFromSharedPref(companyID: String) {
        _companyID.value = companyID
    }

    fun setWarehouseNumberFromSharedPref(warehouseNumber: Int) {
        _warehouseNumber.value = warehouseNumber
    }

    // API call functions
    fun getDoorBins() {
        _hasAPIBeenCalled.value = true
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            Log.i("get door bins API call", "Error -> ${exception.message}")
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                _hasAPIBeenCalled.value = true
                val response = ScannerAPI.getReceivingProductService()
                    .getDoorBins(_warehouseNumber.value!!, _companyID.value!!)
                _doorBins.value = response.response.ttBinList.tt_bin_list
                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                _networkErrorMessage.value = e.message
                _wasLastAPICallSuccessful.value = false
                Log.i("get door bins API call", "Error -> ${e.message}")
            }
        }
    }

//    fun getPreReceiving(binNumber: String, warehouseNumber: Int, companyID: String) {
//       val exceptionHandler = CoroutineExceptionHandler{ _, exception ->
//           _wasLasAPICallSuccessful.value = false
//           _wasPreReceivingFound.value = false
//           Log.i("set pre-receiving for Receiving API Call Exception Handler", "Error -> ${exception.message}")
//       }
//        viewModelScope.launch(exceptionHandler){
//            try{
//                _hasAPIBeenCalled.value = true
//                val response = ScannerAPI.getReceivingProductService().getPreReceiving(binNumber, warehouseNumber, companyID)
//                _preReceivingNumber.value = response.response.preReceivingNumber
//                _wasPreReceivingFound.value = response.response.wasPreReceivingFound
//                _errorMessage.value!!["wasPreReceivingFoundError"] = response.response.errorMessage
//                _wasLasAPICallSuccessful.value = true
//
//            }catch(e: Exception){
//                _wasLasAPICallSuccessful.value = false
//                Log.i("get set the pre-receiving for Receiving API Call Exception Handler", "Error -> ${e.message}")
//            }
//        }
//    }

    fun getPreReceivingInfo() {
        _hasAPIBeenCalled.value = true
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            Log.i(
                "set pre-receiving info for Receiving API Call Exception Handler",
                "Error -> ${exception.message}"
            )
        }
        viewModelScope.launch(exceptionHandler) {
            try {
                _hasAPIBeenCalled.value = true
                val response = ScannerAPI.getReceivingProductService().getPreReceivingInfo(
                    _currentlyChosenDoorBin.value!!.bin_receiving,
                    _warehouseNumber.value!!,
                    _companyID.value!!
                )
                _errorMessage.value!!["wasPreReceivingFoundError"] = response.response.errorMessage
                _wasPreReceivingFound.value = response.response.wasPreReFound
                if (_wasPreReceivingFound.value!!)
                    _preReceivingInfo.value = response.response.ttPreReceiving.tt_pre_receiving[0]
                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                _networkErrorMessage.value = e.message
                _wasLastAPICallSuccessful.value = false
                Log.i(
                    "get set the pre-receiving info for Receiving API Call Exception Handler",
                    "Error -> ${e.message}"
                )
            }
        }
    }

    fun getItemsInDoor() {
        _hasAPIBeenCalled.value = true
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            Log.i("getItemsInDoor api call", "Error -> ${exception.message}")
        }
        viewModelScope.launch(exceptionHandler) {
            try {
                _hasAPIBeenCalled.value = true
                val response = ScannerAPI.getReceivingProductService().getItemsInBin(
                    _currentlyChosenDoorBin.value!!.bin_number,
                    _warehouseNumber.value!!,
                    _companyID.value!!
                )
                val itemsInDoorBin = response.response.ttBinItem.tt_bin_list
                _listOfItemsToMoveInPreReceiving.value!!.clear()
                for (item in itemsInDoorBin) {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd")
                    var newlyParsedExpDateString = ""
                    if (item.expirationDate != null) {
                        val newlyParsedDate = inputFormat.parse(item.expirationDate)
                        val outputFormat = SimpleDateFormat("MM-dd-yyyy")
                        newlyParsedExpDateString = outputFormat.format(newlyParsedDate)
                    }
                    item.expirationDate = newlyParsedExpDateString
                    item.weight = abs(item.weight)
                    item.qtyOnHand = abs(item.qtyOnHand)
                    _listOfItemsToMoveInPreReceiving.value!!.add(item)
                }
                _errorMessage.value!!["getItemsInBin"] = response.response.errorMessage
                _isDoorBinEmpty.value = response.response.isBinEmpty
                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                _networkErrorMessage.value = e.message
                _wasLastAPICallSuccessful.value = false
                Log.i("getItemsInDoor api call (e)", "Error -> ${e.message}")
            }
        }
    }

    fun getItemInfo(scannedCode: String, isEditing: Boolean) {
        _hasAPIBeenCalled.value = true
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            _wasItemFound.value = false
            Log.i("getItemInfo api call", "Error -> ${exception.message}")
        }
        viewModelScope.launch(exceptionHandler) {
            try {
                _hasAPIBeenCalled.value = true
                val response = ScannerAPI.getReceivingProductService()
                    .getItemInfo(
                        scannedCode = scannedCode,
                        doorBin = _currentlyChosenDoorBin.value!!.bin_number,
                        isEditing = isEditing, warehouseNumber.value!!,
                        companyID = companyID.value!!
                    )
                _errorMessage.value!!["wasItemFoundError"] = response.response.errorMessage
                _wasItemFound.value = response.response.wasItemFound
                val listOfItemInfo = response.response.itemInfo.item_info
                if (listOfItemInfo.isNotEmpty())
                    _itemInfo.value = response.response.itemInfo.item_info[0]

                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                _networkErrorMessage.value = e.message
                _wasLastAPICallSuccessful.value = false
                Log.i("getItemInfo api call (e)", "Error -> ${e.message}")
            }
        }
    }

    fun wasBinFound(binNumber: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            _wasBinFound.value = false
            Log.i(
                "set was bin found for Receiving API Call Exception Handler",
                "Error -> ${exception.message}"
            )
        }
        viewModelScope.launch(exceptionHandler) {
            try {
                _hasAPIBeenCalled.value = true
                val response = ScannerAPI.getReceivingProductService()
                    .wasBinFound(binNumber, _warehouseNumber.value!!, _companyID.value!!)
                _wasBinFound.value = response.response.wasPreReceivingFound
                _errorMessage.value!!["wasBinFoundError"] = response.response.errorMessage
                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                _networkErrorMessage.value = e.message
                _wasLastAPICallSuccessful.value = false
                Log.i(
                    "get set the was bin found for Receiving API Call Exception Handler",
                    "Error -> ${e.message}"
                )
            }
        }
    }

    fun moveItemToDoor(itemToAddToDoor: ReceivingProductsDetailsFragment.ItemToAddToDoorBin) {
        _hasAPIBeenCalled.value = true
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            _wasItemMovedToDoor.value = false
            Log.i(
                "set move item to door for Receiving API Call Exception Handler",
                "Error -> ${exception.message}"
            )
        }
        viewModelScope.launch(exceptionHandler) {
            try {
                _hasAPIBeenCalled.value = true
                val response = ScannerAPI.getReceivingProductService().addItemToDoorBin(
                    itemNumber = itemToAddToDoor.itemNumber,
                    doorBin = itemToAddToDoor.doorBin,
                    pickerUserName = _pickerUserName.value!!,
                    quantity = itemToAddToDoor.quantityOfItemsAddedToDoorBin,
                    lotNumber = itemToAddToDoor.lotNumber,
                    weight = itemToAddToDoor.weight,
                    expireDate = itemToAddToDoor.expirationDate,
                    warehouseNumber = _warehouseNumber.value!!,
                    companyID = _companyID.value!!
                )
                _errorMessage.value!!["wasItemMovedToDoorError"] = response.response.errorMessage
                _wasItemMovedToDoor.value = response.response.wasItemMovedToDoor
                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                _networkErrorMessage.value = e.message
                _wasLastAPICallSuccessful.value = false
                Log.i(
                    "set the move item to door for Receiving API Call Exception Handler (e)",
                    "Error -> ${e.message}"
                )
            }
        }
    }

    fun moveItemToBin(rowIDForDoorBin: String, quantity: Int, destinationBin: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            _wasItemMovedToBin.value = false
            Log.i(
                "set move item to bin for Receiving API Call Exception Handler",
                "Error -> ${exception.message}"
            )
        }
        viewModelScope.launch(exceptionHandler) {
            try {
                _hasAPIBeenCalled.value = true
                val response = ScannerAPI.getReceivingProductService().moveItemFromDoorBin(
                    rowIDForDoorBin = rowIDForDoorBin,
                    pickerUserName = _pickerUserName.value!!,
                    quantity,
                    destinationBin
                )
                _wasItemMovedToBin.value = response.response.wasItemMoved
                _errorMessage.value!!["wasItemMovedToBinError"] = response.response.errorMessage
                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                _networkErrorMessage.value = e.message
                _wasLastAPICallSuccessful.value = false
                Log.i(
                    "get set the move item to bin for Receiving API Call Exception Handler",
                    "Error -> ${e.message}"
                )
            }
        }
    }


    fun resetAllItemsMovedFlag() {
        _allItemsMoved.value = false
    }

    fun moveItemsToFloorBin(destinationBin: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.postValue(exception.message)
            _wasLastAPICallSuccessful.postValue(false)
            Log.i(
                "Move Items To Floor Bin Exception Handler",
                "Error -> ${exception.message}"
            )
        }

        viewModelScope.launch(exceptionHandler) {
            _hasAPIBeenCalled.postValue(true)
            val itemsMovedList = mutableListOf<ItemsInBinList>()

            for (itemToMove in _listOfItemsToMoveInPreReceiving.value!!) {
                if (!itemToMove.wasItemAlreadyReceived) {
                    try {
                        val response = ScannerAPI.getReceivingProductService().moveItemFromDoorBin(
                            rowIDForDoorBin = itemToMove.rowID,
                            pickerUserName = _pickerUserName.value!!,
                            quantity = itemToMove.qtyOnHand.toInt(),
                            destinationBin = destinationBin
                        )

                        // Update the item with the movement result
                        val movedItem = itemToMove.copy(
                            // You can add additional fields to ItemsInBinList if needed
                        )

                        // Collect the result for each item
                        itemsMovedList.add(movedItem)

                        _wasItemMovedToBin.postValue(response.response.wasItemMoved)
                        _errorMessage.value!!["wasItemMovedToBinError"] =
                            response.response.errorMessage
                        _wasLastAPICallSuccessful.postValue(true)

                    } catch (e: Exception) {
                        _networkErrorMessage.postValue(e.message)
                        _wasLastAPICallSuccessful.postValue(false)
                        Log.i(
                            "Move Items To Floor Bin Exception",
                            "Error -> ${e.message}"
                        )
                    }
                }
            }

            // After processing all items, update LiveData
            _itemsMoved.postValue(itemsMovedList)
            _allItemsMoved.postValue(true)
        }
    }


    fun validateDestinationBin(destinationBin: String) {
        _hasAPIBeenCalled.value = true
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            Log.i(
                "set validate destination bin for Receiving API Call Exception Handler",
                "Error -> ${exception.message}"
            )
        }
        viewModelScope.launch(exceptionHandler) {
            try {
                _hasAPIBeenCalled.value = true
                val response = ScannerAPI.getReceivingProductService().validateDestinationBin(
                    destinationBin,
                    _warehouseNumber.value!!,
                    _companyID.value!!
                )
                _errorMessage.value!!["validateDestinationBin"] = response.response.errorMessage
                if (response.response.isBinValid)
                    _destinationBin.value = destinationBin
                _isValidDestinationBin.value = response.response.isBinValid
                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                _networkErrorMessage.value = e.message
                _wasLastAPICallSuccessful.value = false
                Log.i(
                    "get set the validate destination bin for Receiving API Call Exception Handler",
                    "Error -> ${e.message}"
                )
            }

        }
    }


    fun confirmItem(scannedCode: String) {
        _hasAPIBeenCalled.value = true
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            Log.i("Confirm Item API Call Exception Handler", "Error -> ${exception.message}")
        }
        viewModelScope.launch(exceptionHandler) {
            try {
                _hasAPIBeenCalled.value = true
                val response = ScannerAPI.getReceivingProductService().confirmItem(
                    scannedCode,
                    _currentlyChosenDoorBin.value!!.bin_receiving,
                    itemInfo.value!!.itemNumber,
                    _companyID.value!!,
                    _warehouseNumber.value!!
                )
                _wasLastAPICallSuccessful.value = true
                _UOMQtyInBarcode.value = response.response.UOMQtyInBarcode
                _weightFromBarcode.value = response.response.weightInBarcode
                _errorMessage.value!!["confirmItem"] = response.response.errorMessage
                _wasItemConfirmed.value = response.response.wasItemConfirmed
            } catch (e: Exception) {
                _networkErrorMessage.value = e.message
                _wasLastAPICallSuccessful.value = false
                Log.i("Confirm Item API Call Exception Handler", "Error -> ${e.message}")
            }
        }

    }

    fun deleteItemFromDoorBin(rowIDForDoorBin: String) {
        _hasAPIBeenCalled.value = true
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            Log.i(
                "set delete item from door bin API Call Exception Handler",
                "Error -> ${exception.message}"
            )
        }
        viewModelScope.launch(exceptionHandler) {
            try {

                val response = ScannerAPI.getReceivingProductService().deleteItemFromDoorInBin(
                    rowIDForDoorBin = rowIDForDoorBin,
                    pickerUserName = _pickerUserName.value!!
                )

                _errorMessage.value!!["wasItemDeleted"] = response.response.errorMessage
                _wasItemDeleted.value = response.response.wasItemDeleted
                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                _networkErrorMessage.value = e.message
                _wasLastAPICallSuccessful.value = false
                Log.i(
                    "get delete item from door bin API Call Exception Handler",
                    "Error -> ${e.message}"
                )
            }
        }
    }

    fun validateLotNumber(lotNumber: String, itemNumber: String) {
        _hasAPIBeenCalled.value = true
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            Log.i("Validate Lot Number", "Error -> ${exception.message}")
        }
        viewModelScope.launch(exceptionHandler) {
            try {

                val response = ScannerAPI.getReceivingProductService()
                    .validateWhetherLotIsInIVLOT(
                        itemNumber,
                        lotNumber,
                        _warehouseNumber.value!!
                    )
                _errorMessage.value!!["validateLotNumber"] = response.response.errorMessage
                _isLotNumberValid.value = response.response.isLotNumberValid
                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                _networkErrorMessage.value = e.message
                _wasLastAPICallSuccessful.value = false
                Log.i("Validate Lot Number (e)", "Error -> ${e.message}")
            }
        }
    }

    fun clearListOfItems() {
        _listOfItemsToMoveInPreReceiving.value = mutableListOf()
    }

    fun clearDoorBinText() {
        _doorBins.value = listOf()
    }

    fun validateQuantityAgainstPreReceiving(
        itemNumber: String,
        quantityEntered: Int
    ) {
        _hasAPIBeenCalled.value = true
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _networkErrorMessage.value = exception.message
            _wasLastAPICallSuccessful.value = false
            Log.i(
                "validateQuantityAgainstPreReceiving API Call Exception Handler",
                "Error -> ${exception.message}"
            )
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val response =
                    ScannerAPI.getReceivingProductService().validateQuantityAgainstPreReceiving(
                        itemNumber = itemNumber,
                        quantityEntered = quantityEntered,
                        receivingNumber = _currentlyChosenDoorBin.value!!.bin_receiving,
                        warehouseNumber = _warehouseNumber.value!!,
                        companyID = _companyID.value!!
                    )

                // Update LiveData variables with the response
                _isQuantityLessThanPreReceiving.value =
                    response.response.isQuantityLessThanPreReceiving
                _preReceivingQuantity.value = response.response.preReceivingQuantity
                _validateQuantityErrorMessage.value = response.response.errorMessage

                _wasLastAPICallSuccessful.value = true
            } catch (e: Exception) {
                _networkErrorMessage.value = e.message
                _wasLastAPICallSuccessful.value = false
                Log.i(
                    "validateQuantityAgainstPreReceiving API Call Exception Handler",
                    "Error -> ${e.message}"
                )
            }
        }

    }
}