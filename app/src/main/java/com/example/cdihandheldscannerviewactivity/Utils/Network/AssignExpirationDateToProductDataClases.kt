package com.example.cdihandheldscannerviewactivity.Utils.Network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class AssignExpDateResponseWrapper(
    @Json(name = "response") val response: AssignExpDateResponse
)
@JsonClass(generateAdapter = true)
data class AssignExpDateResponse(
    @Json(name = "opSuccess") val opSuccess: Boolean,
    @Json(name = "opMessage") val opMessage: String
)


@JsonClass(generateAdapter = true)
data class DisplayInfoResponseWrapper(
    @Json(name = "response") val response: DisplayInfoResponse
)
@JsonClass(generateAdapter = true)
data class DisplayInfoResponse(
    @Json(name = "binItemInfo") val binItemInfo: ItemInfoWrapper,
    @Json(name = "opSuccess") val opSuccess: Boolean,
    @Json(name = "opMessage") val opMessage: String
)
@JsonClass(generateAdapter = true)
data class ItemInfoWrapper(
    @Json(name = "bin-item-info") val response: ItemInfo,
)
@JsonClass(generateAdapter = true)
data class ItemInfo(
    @Json(name = "itemNumber") val itemNumber: String,
    @Json(name = "itemDescription") val itemDescription: String,
    @Json(name = "binLocation") val binLocation: String,
    @Json(name = "expireDate") val expireDate: Date
)
