package com.comdist.cdihandheldscannerviewactivity.Utils.Network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// All the JSON Response Data classes for the Move Items Between Bins backend end point

// Requests all bins on a warehouse
@JsonClass(generateAdapter = true)
data class ResponseWrapperBinListWrapper(
    @Json(name="response") val response: ResponseBinListWrapper
)
@JsonClass(generateAdapter = true)
data class ResponseBinListWrapper(
    @Json(name="ttBinList") val binListWrapper: ResponseBinList
)
@JsonClass(generateAdapter = true)
data class ResponseBinList(
    @Json(name="tt-bin-list") val binList: BinList
)
@JsonClass(generateAdapter = true)
data class BinList(
    @Json(name="bin-number") val binNumber: String
)

// Requests all items in a bin
@JsonClass(generateAdapter = true)
data class ResponseWrapperBinItemListWrapper(
    @Json(name="response") val response: ResponseWrapperItemListWrapper
)
@JsonClass(generateAdapter = true)
data class ResponseWrapperItemListWrapper(
    @Json(name="ttBinItem") val ttBinItem: ResponseWrapperBinItemList
)
@JsonClass(generateAdapter = true)
data class ResponseWrapperBinItemList(
    @Json(name="tt-bin-item") val ttBinItemList: ResponseBinItemList
)
@JsonClass(generateAdapter = true)
data class ResponseBinItemList(
    @Json(name="tt-warehouse-no") val ttWarehouseNo: Int,
    @Json(name="tt-bin-loc") val ttBinLoc: String,
    @Json(name="tt-item-number") val ttItemNumber: String,
    @Json(name="tt-type") val ttType: String,
    @Json(name="tt-type-data") val ttTypeData: String,
    @Json(name="tt-style-color") val ttStyleColor: String,
    @Json(name="tt-qty-on-hand") val ttSQtyOnHand: Float,
    @Json(name="tt-in-picking") val InPicking: Float,
    @Json(name="tt-size") val ttSize: String,
    @Json(name="tt-date-created") val ttDateCreated: String,
    @Json(name="tt-company-code") val ttCompanyCode: String,
    @Json(name="tt-qty-picked") val ttQtyPicked: Float,
    @Json(name="tt-picked-qty") val ttPickedQty: Float,
    @Json(name="tt-lot-number") val ttLotNumber: String,
    @Json(name="tt-expire-date") val ttExpireDate: String,
    @Json(name="tt-weight") val ttWeight: Float,
    @Json(name="tt-row-id") val ttRowId: String,
)

// Confirms existence of a Bin
@JsonClass(generateAdapter = true)
data class ResponseWrapperBinConfirmation(
    @Json(name="response") val response: ResponseBinConfirmation
)
@JsonClass(generateAdapter = true)
data class ResponseBinConfirmation(
    @Json(name="wasBinConfirmed") val wasBinConfirmed: Boolean
)

// Validates quantity send with database quantity
@JsonClass(generateAdapter = true)
data class ResponseWrapperWasQuantityValid(
    @Json(name="response") val response: ResponseWasQuantityValid
)
@JsonClass(generateAdapter = true)
data class ResponseWasQuantityValid(
    @Json(name="wasQuantityValid") val wasQuantityValid: Boolean,
    @Json(name="errorMessage") val errorMessage: String
)
