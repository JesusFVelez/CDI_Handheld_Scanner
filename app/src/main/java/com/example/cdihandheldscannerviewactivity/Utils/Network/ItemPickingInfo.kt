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
    @Json(name = "oumForIndividualCases") val uomForIndividualCases: String,
    @Json(name = "binLocation") val binLocation: String,
    @Json(name = "doesItemHaveSizes") val doesItemHaveSizes: Boolean,
    @Json(name = "doesItemHaveSerialNumber") val doesItemHaveSerialNumber: Boolean,
    @Json(name = "doesItemHaveEachesInOrder") val doesItemHaveEachesInOrder: Boolean,
    @Json(name = "itemSize") val itemSize: String,
    @Json(name = "itemStyleColor") val itemStyleColor: String,
    @Json(name = "howManyIndividualQtysPerUOM") val howManyIndividualQtysPerUOM: Float
)




