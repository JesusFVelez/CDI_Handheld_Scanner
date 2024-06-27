package com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


/*Assign Lot number to item*/
@JsonClass(generateAdapter = true)
data class AssignLotNumberResponseWrapper(
    @Json(name = "response") val response: AssignLotNumberResponse
)
@JsonClass(generateAdapter = true)
data class AssignLotNumberResponse(
    @Json(name = "opSuccess") val opSuccess: Boolean,
    @Json(name = "opMessage") val opMessage: String
)


/*Assigns Expirationdate to item in bin*/
@JsonClass(generateAdapter = true)
data class AssignExpDateResponseWrapper(
    @Json(name = "response") val response: AssignExpDateResponse
)
@JsonClass(generateAdapter = true)
data class AssignExpDateResponse(
    @Json(name = "opSuccess") val opSuccess: Boolean,
    @Json(name = "opMessage") val opMessage: String
)


/*Displays information in UpprDiv*/
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
    @Json(name = "bin-item-info") val response: List<ItemInfo>,
)


@JsonClass(generateAdapter = true)
data class ItemInfo(
    @Json(name = "itemNumber") val itemNumber: String,
    @Json(name = "itemDescription") val itemDescription: String,
    @Json(name = "binLocation") val binLocation: String,
    @Json(name = "expireDate") val expireDate: String?,
    @Json(name = "lotNumber") val lotNumber: String?
)


/*Get data for suggestion list*/
@JsonClass(generateAdapter = true)
data class GetAllItemsInBinForSuggestionResponseWrapper(
    @Json(name = "response") val response: GetAllItemsInBinForSuggestionWrapper
)

@JsonClass(generateAdapter = true)
data class GetAllItemsInBinForSuggestionWrapper(
    @Json(name = "binItemInfo") val binItemInfo: GetAllItemsInBinForSuggestion
)

@JsonClass(generateAdapter = true)
data class GetAllItemsInBinForSuggestion(
    @Json(name = "bin-item-info") val binItemInfo:List<ItemData>
)

@JsonClass(generateAdapter = true)
@Parcelize
data class ItemData(
    @Json(name = "itemNumber") val itemNumber: String,
    @Json(name = "itemDescription") val itemDescription: String,
    @Json(name = "binLocation") val binLocation: String,
    @Json(name = "expireDate") val expireDate: String?,
    @Json(name = "lotNumber") val lotNumber: String?,
    @Json(name = "barCode") val barCode: String?
): Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeString(itemNumber)
        p0.writeString(itemDescription)
        p0.writeString(binLocation)
        p0.writeString(expireDate)
        p0.writeString(lotNumber)
    }
}

