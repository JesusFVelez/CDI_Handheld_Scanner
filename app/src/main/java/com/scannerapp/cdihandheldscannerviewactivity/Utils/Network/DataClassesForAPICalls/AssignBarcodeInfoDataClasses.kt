package com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


// All the JSON Response Data classes for the BarcodeAssign backend end point

// Gets all barcodes for item
@JsonClass(generateAdapter = true)
data class GetAllBarcodesForItemResponseWrapper(
    val response: GetAllBarcodesForItemResponse
)

data class GetAllBarcodesForItemResponse(
    @Json(name = "mainBarcode") val mainBarcode: String,
    @Json(name = "listOfBarcodes") val listOfBarcodes: List<String>,
    @Json(name = "wasItemFound") val wasItemFound: Boolean,
    @Json(name = "errorMessage") val errorMessage: String
)


// Add Barcode for an item

data class AddBarcodeToItemResponseWrapper(
    @Json(name = "response") val response: AddBarcodeToItemResponse
)

data class AddBarcodeToItemResponse(
    @Json(name = "couldAddBarcode") val couldAddBarcode: Boolean,
    @Json(name = "errorMessage") val errorMessage: String
)



// Validates existence of item in backend
@JsonClass(generateAdapter = true)
data class ResponseWrapperWasItemFound(
    val response: ResponseWasItemFound
)
@JsonClass(generateAdapter = true)
data class ResponseWasItemFound(
    @Json(name = "wasItemFound") val wasItemFound: Boolean,
    @Json(name = "errorMessage") val errorMessage: String
)


// Requests basic information of item
@JsonClass(generateAdapter = true)
data class ResponseWrapperGetItem(
    @Json(name = "response") val response: ResponseGetItem
)

@JsonClass(generateAdapter = true)
data class ResponseGetItem(
    @Json(name = "itemInfo") val itemInfo: GetItemWrapper
)

@JsonClass(generateAdapter = true)
data class GetItemWrapper(
    @Json(name = "item-info") val item_info: List<GetItem>
)

@JsonClass(generateAdapter = true)
data class GetItem (
    @Json(name = "itemNumber") val itemNumber: String,
    @Json(name = "itemDescription") val itemDescription: String,
    @Json(name = "itemBarcode") val itemBarcode: String,
)

// Validates length of barcode
@JsonClass(generateAdapter = true)
data class ResponseWrapperValidateBarcode(
    @Json(name = "response") val response: ValidateBarcode
)

@JsonClass(generateAdapter = true)
data class ValidateBarcode(
    @Json(name = "validation") val validation: Boolean,
    @Json(name= "errorMessage") val errorMessage: String
)

// Sets barcode to selected item
@JsonClass(generateAdapter = true)
data class ResponseWrapperSetBarcode(
    @Json(name = "response") val response: SetBarcode
)

@JsonClass(generateAdapter = true)
data class SetBarcode(
    @Json(name = "wasBarcodeAssigned") val wasBarcodeAssigned: Boolean,
    @Json(name = "errorMessage") val errorMessage: String
)