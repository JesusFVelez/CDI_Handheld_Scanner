package com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls

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
    @Json(name="tt-bin-list") val tt_bin_list: List<DoorBin>
)
@JsonClass(generateAdapter = true)
data class DoorBin (
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
    @Json(name="item-info") val item_info: List<ReceivingItemInfo>
)
@JsonClass(generateAdapter = true)
data class ReceivingItemInfo(
    @Json(name="itemNumber") val itemNumber: String,
    @Json(name="itemDescription") val itemDescription: String,
    @Json(name="itemBarcode") val itemBarcode: String?,
    @Json(name = "doesItemUseLotNumber") val doesItemUseLotNumber: Boolean,
    @Json(name = "doesItemHaveWeight") val doesItemHaveWeight: Boolean,
    @Json(name= "weight") val weight: Float
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
    @Json(name="wasPreReceivingFound") val wasPreReceivingFound: Boolean
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
    @Json(name="tt-pre-receiving") val tt_pre_receiving: List<PreReceivingInfo>
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

// Get items on door bin
@JsonClass(generateAdapter = true)
data class ResponseItemsInBinWrapper(
    @Json(name="response") val response: ItemsInBinWrapper
)
@JsonClass(generateAdapter = true)
data class ItemsInBinWrapper(
    @Json(name="errorMessage") val errorMessage: String,
    @Json(name="isBinEmpty") val isBinEmpty: Boolean,
    @Json(name="ttBinItem") val ttBinItem: ItemsInBinListWrapper
)
@JsonClass(generateAdapter = true)
data class ItemsInBinListWrapper(
    @Json(name="tt-bin-item") val tt_bin_list: List<ItemsInBinList>
)
@JsonClass(generateAdapter = true)
data class ItemsInBinList(
    @Json(name="tt-warehouse-no") val warehouseNumber: Int,
    @Json(name="tt-bin-loc") val binLocation: String,
    @Json(name="tt-item-number") val itemNumber: String,
    @Json(name="tt-type") val type: String,
    @Json(name="tt-type-data") val typeData: String,
    @Json(name="tt-style-color") val styleColor: String,
    @Json(name="tt-qty-on-hand") val qtyOnHand: Float,
    @Json(name="tt-in-picking") val inPicking: Float,
    @Json(name="tt-size") val size: String,
    @Json(name="tt-date-created") val dateCreated: String,
    @Json(name="tt-company-code") val companyID: String,
    @Json(name="tt-qty-picked") val qtyPicked: Float,
    @Json(name="tt-picked-qty") val pickedQty: Float,
    @Json(name="tt-lot-number") val lotNumber: String,
    @Json(name="tt-expire-date") val expirationDate: String?,
    @Json(name="tt-weight") val weight: Float,
    @Json(name="tt-row-id") val rowID: String,
    @Json(name="tt-item-name") val itemName: String,
    @Json(name="tt-has-lot-number") val doesItemHaveLotNumber: Boolean

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

// Deletes items from door bins
@JsonClass(generateAdapter = true)
data class ResponseDeleteItemFromDoorBin(
    @Json(name="response") val response: DeleteItemsFromDoorBin
)
@JsonClass(generateAdapter = true)
data class DeleteItemsFromDoorBin(
    @Json(name="wasItemDeleted") val wesItemDeleted: Boolean,
    @Json(name="errorMessage") val errorMessage: String
)


// Data classes for validating whether a lot number exists in the DB
@JsonClass(generateAdapter = true)
data class ResponseValidateLotNumberWrapper(
    val response: ResponseValidateLotNumber
)

@JsonClass(generateAdapter = true)
data class ResponseValidateLotNumber(
    @Json(name = "isLotNumberValid") val isLotNumberValid: Boolean,
    @Json(name = "errorMessage") val errorMessage: String
)


// Data classes for confirming an item
@JsonClass(generateAdapter = true)
data class ResponseConfirmItemWrapper(
    val response: ResponseConfirmItem
)

@JsonClass(generateAdapter = true)
data class ResponseConfirmItem(
    @Json(name = "wasItemConfirmed") val wasItemConfirmed: Boolean,
    @Json(name = "errorMessage") val errorMessage: String,
    @Json(name = "UOMQtyInBarcode") val UOMQtyInBarcode: Float ,
    @Json(name = "weightInBarcode") val weightInBarcode:Float)