package com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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
    @Json(name = "lotNumber") val lotNumber: String,
    @Json(name = "barCode") val barCode: String?,
    @Json(name = "qtyCounted") val qtyCounted: Int?,
    @Json(name = "inCount") val inCount: Boolean    //Boolean For Counting status
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



/*---------------------------------------------------------------*/
/* The following will not be used in the final project           */
/*---------------------------------------------------------------*/
/* Get All Bin Numbers with ClassCode Response Wrapper */
@JsonClass(generateAdapter = true)
data class GetBinsByClassCodeResponseWrapper(
    @Json(name = "response") val response: GetBinsByClassCodeResponse
)

@JsonClass(generateAdapter = true)
data class GetBinsByClassCodeResponse(
    @Json(name = "ttBinInfo") val ttBinInfo: TtBinInfoWithClassCodeWrapper
)

@JsonClass(generateAdapter = true)
data class TtBinInfoWithClassCodeWrapper(
    @Json(name = "ttBinInfo") val ttBinInfo: List<BinInfo>
)

@JsonClass(generateAdapter = true)
data class BinInfo(
    @Json(name = "binLocation") val binLocation: String,
    @Json(name = "classCode") val classCode: String?,
    @Json(name = "vendor") val vendor: String?,
    @Json(name = "itemNumber") val itemNumber: String
)

/* Get Bins By Vendor Response Wrapper */
@JsonClass(generateAdapter = true)
data class GetBinsByVendorResponseWrapper(
    @Json(name = "response") val response: GetBinsByVendorResponse
)

@JsonClass(generateAdapter = true)
data class GetBinsByVendorResponse(
    @Json(name = "ttBinInfo") val ttBinInfo: TtBinInfoWithVendorWrapper
)

@JsonClass(generateAdapter = true)
data class TtBinInfoWithVendorWrapper(
    @Json(name = "ttBinInfo") val ttBinInfo: List<VendorBinInfo>
)

@JsonClass(generateAdapter = true)
data class VendorBinInfo(
    @Json(name = "binLocation") val binLocation: String,
    @Json(name = "classCode") val classCode: String?,
    @Json(name = "vendor") val vendor: String?,
    @Json(name = "itemNumber") val itemNumber: String
)

/* Get Bins By Item Number Response Wrapper */
@JsonClass(generateAdapter = true)
data class GetBinsByItemNumberResponseWrapper(
    @Json(name = "response") val response: GetBinsByItemNumberResponse
)

@JsonClass(generateAdapter = true)
data class GetBinsByItemNumberResponse(
    @Json(name = "ttBinInfo") val ttBinInfo: TtBinInfoWithItemNumberWrapper
)

@JsonClass(generateAdapter = true)
data class TtBinInfoWithItemNumberWrapper(
    @Json(name = "ttBinInfo") val ttBinInfo: List<ItemNumberBinInfo>
)

@JsonClass(generateAdapter = true)
data class ItemNumberBinInfo(
    @Json(name = "binLocation") val binLocation: String,
    @Json(name = "classCode") val classCode: String?,
    @Json(name = "vendor") val vendor: String?,
    @Json(name = "itemNumber") val itemNumber: String
)

