package com.example.cdihandheldscannerviewactivity.Utils.Network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ItemPickingResponseWrapper(
    val response: ResponseItemsToPick
)

@JsonClass(generateAdapter = true)
data class ResponseItemsToPick(
    @Json(name = "itemsInOrder") val itemsInOrder: ResponseContentItemsToPick
)

@JsonClass(generateAdapter = true)
data class ResponseContentItemsToPick(
    @Json(name = "itemsInOrder") val itemsInOrder: List<ItemsInOrderInfo>
)

@JsonClass(generateAdapter = true)
data class ItemsInOrderInfo(
    @Json(name = "itemNumber") val itemNumber: String,
    @Json(name = "itemName") val itemName: String,
    @Json(name = "itemDetails") val itemDetails: String,
    @Json(name = "itemPickingStatus") val itemPickingStatus: String,
    @Json(name = "quantityPicked") val quantityPicked: Float,
    @Json(name = "totalQuantityToBePicked") val totalQuantityToBePicked: Float,
    @Json(name = "pendingPickQtyInCases") val pendingPickQtyInCases: Float,
    @Json(name = "pendingPickQtyInIndividualUnits") val pendingPickQtyInIndividualUnits: Float,
    @Json(name = "uomForCases") val uomForCases: String,
    @Json(name = "uomForIndividualItems") val uomForIndividualItems: String,
    @Json(name = "binLocation") val binLocation: String,
    @Json(name = "doesItemHaveSizes") val doesItemHaveSizes: Boolean,
    @Json(name = "doesItemHaveSerialNumber") val doesItemHaveSerialNumber: Boolean,
    @Json(name = "doesItemHaveEachesInOrder") val doesItemHaveEachesInOrder: Boolean,
    @Json(name = "itemSize") val itemSize: String,
    @Json(name = "itemStyleColor") val itemStyleColor: String,
    @Json(name = "howManyIndividualQtysPerUOM") val howManyIndividualQtysPerUOM: Float

)




// Bin Confirmation
@JsonClass(generateAdapter = true)
data class BinConfirmationResponseWrapper(
    val response: responseBinConfirmation
)

@JsonClass(generateAdapter = true)
data class responseBinConfirmation(
    @Json(name = "hasBinBeenConfirmed") val hasBinBeenConfirmed: Boolean,
    @Json(name = "errorMessage") val errorMessage: String
)




// Item Confirmation
@JsonClass(generateAdapter = true)
data class ItemConfirmationResponseWrapper(
    val response: responseItemConfirmation
)

@JsonClass(generateAdapter = true)
data class responseItemConfirmation(
    @Json(name = "wasItemConfirmed") val wasItemConfirmed: Boolean,
    @Json(name = "errorMessage") val errorMessage: String,
    @Json(name = "UOMQtyInBarcode") val UOMQtyInBarcode:Float
)




// Order Has Picking
@JsonClass(generateAdapter = true)
data class OrderHasPickingResponseWrapper(
    val response: responsePickingConfirmation
)

@JsonClass(generateAdapter = true)
data class responsePickingConfirmation(
    @Json(name = "orderHasPicking") val orderHasPicking: Boolean,
    @Json(name = "errorMessage") val errorMessage: String
)




// Confirm Order
@JsonClass(generateAdapter = true)
data class ConfirmOrderResponseWrapper(
    val response: responseOrderConfirmation
)

@JsonClass(generateAdapter = true)
data class responseOrderConfirmation(
    @Json(name = "isOrderAvailable") val isOrderAvailable: Boolean,
    @Json(name = "errorMessage") val errorMessage: String
)




// Verify if Client Account is closed
@JsonClass(generateAdapter = true)
data class VerifyClientResponseWrapper(
    val response: responseClientConfirmation
)

@JsonClass(generateAdapter = true)
data class responseClientConfirmation(
    @Json(name = "isClientAccountClosed") val isClientAccountClosed: Boolean,
    @Json(name = "errorMessage") val errorMessage: String
)