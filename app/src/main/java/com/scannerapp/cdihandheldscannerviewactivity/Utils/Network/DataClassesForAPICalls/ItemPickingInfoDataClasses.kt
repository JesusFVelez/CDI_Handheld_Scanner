package com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls

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
    @Json(name = "howManyIndividualQtysPerUOM") val howManyIndividualQtysPerUOM: Float,
    @Json(name = "uniqueIDForPicking") val uniqueIDForPicking: String
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



// Finish Picking for Single Item
@JsonClass(generateAdapter = true)
data class finishPickingForSingleItemResponseWrapper(
    val response: finishPickingForSingleItemResponse
)

@JsonClass(generateAdapter = true)
data class finishPickingForSingleItemResponse(
    @Json(name = "wasPickingSuccesfull") val wasPickingSuccesfull: Boolean,
    @Json(name = "errorMessage") val errorMessage: String
)



// Get All orders that have picking for suggestions

@JsonClass(generateAdapter = true)
data class getOrdersForSuggestionWrapper(
    val response: ordersThatAreInPickingWrapper
)

@JsonClass(generateAdapter = true)
data class ordersThatAreInPickingWrapper(
    @Json(name = "ordersThatAreInPicking") val ordersThatAreInPicking: ordersThatAreInPickingWrapperWrapper
)

@JsonClass(generateAdapter = true)
data class ordersThatAreInPickingWrapperWrapper(
    @Json(name = "ordersThatAreInPicking") val ordersThatAreInPicking: List<ordersThatAreInPickingClass>
)


@JsonClass(generateAdapter = true)
data class ordersThatAreInPickingClass(
    @Json(name = "orderNumber") val orderNumber: String,
    @Json(name = "customerName") val customerName:String,
    @Json(name = "orderedDate") val orderedDate: String,
    @Json(name = "dateWanted") val dateWanted: String
)