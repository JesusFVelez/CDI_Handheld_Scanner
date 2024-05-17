package com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


// All the JSON Response Data classes for the BarcodeAssign backend end point

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