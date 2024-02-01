package com.example.cdihandheldscannerviewactivity.Utils.Network

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
    @Json(name = "wasItemFound") val wasItemFound: Boolean
)


// Requests basic information of item
@JsonClass(generateAdapter = true)
data class ResponseWrapperGetItem(
    @Json(name = "response") val response: ResponseGetItem
)

@JsonClass(generateAdapter = true)
data class ResponseGetItem(
    @Json(name = "itemInfo") val response: GetItemWrapper
)

@JsonClass(generateAdapter = true)
data class GetItemWrapper(
    @Json(name = "item-info") val response: GetItem
)

@JsonClass(generateAdapter = true)
data class GetItem (
    @Json(name = "itemNumber") val itemNumber: String,
    @Json(name = "itemDescription") val itemDescription: String,
    @Json(name = "itemBarcode") val itemBarcode: String
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