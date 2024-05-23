package com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/* Update Count Response Wrapper */
@JsonClass(generateAdapter = true)
data class UpdateCountResponseWrapper(
    @Json(name = "response") val response: UpdateCountResponse
)

@JsonClass(generateAdapter = true)
data class UpdateCountResponse(
    @Json(name = "opSuccess") val opSuccess: Boolean,
    @Json(name = "opMessage") val opMessage: String
)

/* Get All Items In Bin For Suggestion Response Wrapper */
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

/* Get All Bin Numbers Response Wrapper */
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



