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
    @Json(name = "listOfBarcodes") val listOfBarcodes: List<String>?,
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


// Remove barcode from item
@JsonClass(generateAdapter = true)
data class RemoveBarcodeFromItemResponseWrapper(
    @Json(name = "response") val response: RemoveBarcodeFromItemResponse
)
@JsonClass(generateAdapter = true)
data class RemoveBarcodeFromItemResponse(
    @Json(name = "couldRemoveBarcode") val couldRemoveBarcode: Boolean,
    @Json(name = "errorMessage") val errorMessage: String
)



@JsonClass(generateAdapter = true)
data class ResponseWrapperUpdateBarcode(
    @Json(name = "response") val response: ResponseUpdateBarcode
)

@JsonClass(generateAdapter = true)
data class ResponseUpdateBarcode(
    @Json(name = "couldUpdateBarcode") val couldUpdateBarcode: Boolean,
    @Json(name = "errorMessage") val errorMessage: String
)