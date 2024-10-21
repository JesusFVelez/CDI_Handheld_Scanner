package com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/*---------------------------------------------------------------*/
/* Confirm Item Response Wrapper                                 */
/*---------------------------------------------------------------*/
@JsonClass(generateAdapter = true)
data class ResponseItemConfirmWrapper(
    @Json(name = "response") val response: ResponseItemConfirm
)

@JsonClass(generateAdapter = true)
data class ResponseItemConfirm(
    @Json(name = "wasItemConfirmed") val wasItemConfirmed: Boolean,
    @Json(name = "UOMQtyInBarcode") val UOMQtyInBarcode: Double,
    @Json(name = "weightInBarcode") val weightInBarcode: Double,
    @Json(name = "errorMessage") val errorMessage: String
)

/*---------------------------------------------------------------*/
/* Get Item Details For Popup Response Wrapper                   */
/*---------------------------------------------------------------*/
@JsonClass(generateAdapter = true)
data class GetItemDetailsForPopupResponseWrapper(
    @Json(name = "response") val response: GetItemDetailsForPopupResponse
)

@JsonClass(generateAdapter = true)
data class GetItemDetailsForPopupResponse(
    @Json(name = "ttItemInfo") val ttItemInfo: TtItemInfoWrapp
)

@JsonClass(generateAdapter = true)
data class TtItemInfoWrapp(
    @Json(name = "ttItemInfo") val ttItemInfo: List<TtItemInf>
)

@JsonClass(generateAdapter = true)
data class TtItemInf(
    @Json(name = "itemNumber") val itemNumber: String,
    @Json(name = "itemDescription") val itemDescription: String,
    @Json(name = "binLocation") val binLocation: String,
    @Json(name = "expireDate") val expireDate: String?,
    @Json(name = "lotNumber") val lotNumber: String?,
    @Json(name = "qtyCounted") val qtyCounted: Int,
    @Json(name = "inCount") val inCount: Boolean,
    @Json(name = "barCode") val barCode: String?,
    @Json(name = "weight") val weight: Double?,
    @Json(name = "doesItemHaveWeight") val doesItemHaveWeight: Boolean,
    @Json(name = "isItemInIvlot") val isItemInIvlot: Boolean,
    @Json(name = "multiLotId") val multiLotId: Boolean,
    @Json(name = "dateParamfileStatus") val dateParamfileStatus: Boolean
)

/*---------------------------------------------------------------*/
/* Update Count Response Wrapper                                 */
/*---------------------------------------------------------------*/

@JsonClass(generateAdapter = true)
data class UpdateCountResponseWrapper(
    @Json(name = "response") val response: UpdateCountResponse
)

@JsonClass(generateAdapter = true)
data class UpdateCountResponse(
    @Json(name = "opSuccess") val opSuccess: Boolean,
    @Json(name = "opMessage") val opMessage: String
)

/*---------------------------------------------------------------*/
/* Get All Items In Bin For Suggestion Response Wrapper          */
/*---------------------------------------------------------------*/
@JsonClass(generateAdapter = true)
data class GetAllItemsInBinResponseWrapper(
    @Json(name = "response") val response: Response
)

@JsonClass(generateAdapter = true)
data class Response(
    @Json(name = "ttItemInfo") val ttItemInfo: TtItemInfoWrapper
)

@JsonClass(generateAdapter = true)
data class TtItemInfoWrapper(
    @Json(name = "ttItemInfo") val ttItemInfo: List<TtItemInfo>
)

@JsonClass(generateAdapter = true)
data class TtItemInfo(
    @Json(name = "itemNumber") val itemNumber: String,
    @Json(name = "itemDescription") val itemDescription: String,
    @Json(name = "binLocation") val binLocation: String,
    @Json(name = "expireDate") val expireDate: String?,
    @Json(name = "lotNumber") val lotNumber: String?,
    @Json(name = "barCode") val barCode: String?,
    @Json(name = "qtyCounted") val qtyCounted: Int?,
    @Json(name = "inCount") val inCount: Boolean,
    @Json(name = "weight") val weight: Double?,
    @Json(name = "doesItemHaveWeight") val doesItemHaveWeight: Boolean,
    @Json(name = "isItemInIvlot") val isItemInIvlot: Boolean,
    @Json(name = "multiLotId") val multiLotId: Boolean,
    @Json(name = "dateParamfileStatus") val dateParamfileStatus: Boolean
)

/*---------------------------------------------------------------*/
/* Get All Bin Numbers Response Wrapper                          */
/*---------------------------------------------------------------*/
@JsonClass(generateAdapter = true)
data class GetAllBinNumbersResponseWrapper(
    @Json(name = "response") val response: BinNumberResponse
)

@JsonClass(generateAdapter = true)
data class BinNumberResponse(
    @Json(name = "ttBinInfo") val ttBinInfo: TtBinInfoWrapper
)

@JsonClass(generateAdapter = true)
data class TtBinInfoWrapper(
    @Json(name = "ttBinInfo") val ttBinInfo: List<TtBinInfo>
)

@JsonClass(generateAdapter = true)
data class TtBinInfo(
    @Json(name = "binLocation") val binLocation: String
)


/*---------------------------------------------------------------*/
/* Get Bins By ClassCode,Vendor and ItemNumber Response Wrappers */
/*---------------------------------------------------------------*/
@JsonClass(generateAdapter = true)
data class GetBinsByClassCodeByVendorAndByItemNumberResponseWrapper(
    @Json(name = "response") val response: BinsByClassCodeByVendorAndByItemNumberResponse
)

@JsonClass(generateAdapter = true)
data class BinsByClassCodeByVendorAndByItemNumberResponse(
    @Json(name = "ttBinInfo") val ttBinInfo: BinsByClassCodeByVendorAndByItemNumberWrapper
)

@JsonClass(generateAdapter = true)
data class BinsByClassCodeByVendorAndByItemNumberWrapper(
    @Json(name = "ttBinInfo") val ttBinInfo: List<BinsByClassCodeByVendorAndByItemNumber>
)

@JsonClass(generateAdapter = true)
data class BinsByClassCodeByVendorAndByItemNumber(
    @Json(name = "binLocation") val binLocation: String,
    @Json(name = "classCode") val classCode: String?,
    @Json(name = "vendor") val vendor: String?,
    @Json(name = "itemNumber") val itemNumber: String?
)

