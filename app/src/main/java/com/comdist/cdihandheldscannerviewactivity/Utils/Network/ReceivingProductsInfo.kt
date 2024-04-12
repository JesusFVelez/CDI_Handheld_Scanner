package com.comdist.cdihandheldscannerviewactivity.Utils.Network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// All the JSON Response Data classes for the Receiving backend end point

// Requests all door Bins
@JsonClass(generateAdapter = true)
data class ResponseWrapperDoorBinListWrapper(
    @Json(name="response") val response: ResponseDoorBinListWrapper
)
@JsonClass(generateAdapter = true)
data class ResponseDoorBinListWrapper(
    @Json(name="ttBinList") val ttBinList: ResponseDoorBinList
)
@JsonClass(generateAdapter = true)
data class ResponseDoorBinList(
    @Json(name="tt-bin-list") val tt_bin_list: DoorBinList
)
@JsonClass(generateAdapter = true)
data class DoorBinList (
    @Json(name="bin-number") val bin_number: String,
    @Json(name="bin-receiving") val bin_receiving: String
)

// Request item details
@JsonClass(generateAdapter = true)
data class ResponseWrapperItemDetailsWrapper(
    @Json(name="response") val response: ResponseItemDetailsWrapper
)
@JsonClass(generateAdapter = true)
data class ResponseItemDetailsWrapper(
    @Json(name="errorMessage") val errorMessage: String,
    @Json(name="itemInfo") val itemInfo: ResponseItemInfoList,
    @Json(name="wasItemFound") val wasItemFound: Boolean
)
@JsonClass(generateAdapter = true)
data class ResponseItemInfoList(
    @Json(name="item-info") val item_info: ItemInfoList
)
@JsonClass(generateAdapter = true)
data class ItemInfoList(
    @Json(name="itemNumber") val itemNumber: String,
    @Json(name="itemDescription") val itemDescription: String,
    @Json(name="itemBarcode") val itemBarcode: String
)

// Request Pre-Receiving on Bin if Any
@JsonClass(generateAdapter = true)
data class ResponsePreReceiving(
    @Json(name="response") val response: PreReceiving
)
@JsonClass(generateAdapter = true)
data class PreReceiving (
    @Json(name="preReceivingNumber") val preReceivingNumber: String,
    @Json(name="errorMessage") val errorMessage: String,
    @Json(name="wasPreReveivingFound") val wasPreReceivingFound: Boolean
)

// Request Pre-Receiving Info
@JsonClass(generateAdapter = true)
data class ResponseGetPreReceiving(
    @Json(name="response") val response: GetPreReceiving
)
@JsonClass(generateAdapter = true)
data class GetPreReceiving (
    @Json(name="ttPreReceiving") val ttPreReceiving: PreReceivingInfoWrapper,
    @Json(name="wasPreReFound") val wasPreReFound: Boolean,
    @Json(name="errorMessage") val errorMessage: String
)
@JsonClass(generateAdapter = true)
data class PreReceivingInfoWrapper (
    @Json(name="tt-pre-receiving") val tt_pre_receiving: PreReceivingInfo
)
@JsonClass(generateAdapter = true)
data class PreReceivingInfo (
    @Json(name="tt-receiving-number") val tt_pre_receiving_number: String,
    @Json(name="tt-warehouse-number") val tt_warehouse_number: Int,
    @Json(name="tt-purchase-order") val tt_purchase_order: String
)

// Confirms existence of a bin
@JsonClass(generateAdapter = true)
data class ResponseConfirmBin(
    @Json(name="response") val response: BinConfirmation
)
@JsonClass(generateAdapter = true)
data class BinConfirmation (
    @Json(name="wasBinFound") val wasPreReceivingFound: Boolean,
    @Json(name="errorMessage") val errorMessage: String
)

// Moves an item to a door bin
@JsonClass(generateAdapter = true)
data class ResponseMoveItemToDoorBin(
    @Json(name="response") val response: MovementConfirmationToDoorBin
)
@JsonClass(generateAdapter = true)
data class MovementConfirmationToDoorBin  (
    @Json(name="errorMessage") val errorMessage: String,
    @Json(name="wasItemMovedToDoor") val wasItemMovedToDoor: Boolean
)

// Moves an item to a door bin
@JsonClass(generateAdapter = true)
data class ResponseMoveItemFromDoorBin(
    @Json(name="response") val response: MovementConfirmationFromDoor
)
@JsonClass(generateAdapter = true)
data class MovementConfirmationFromDoor  (
    @Json(name="errorMessage") val errorMessage: String,
    @Json(name="wasItemMoved") val wasItemMoved: Boolean
)