package com.example.cdihandheldscannerviewactivity.Utils.Network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ResponseWrapperProductsInBin(
    val response: ResponseProdInBin
)


@JsonClass(generateAdapter = true)
data class ResponseProdInBin(
    @Json(name = "wasBinFound") val wasBinFound: Boolean,
    @Json(name = "itemsInBin") val itemsInBin: ResponseContentProdInBIn
)

@JsonClass(generateAdapter = true)
data class ResponseContentProdInBIn(
    @Json(name = "itemsInBin") val itemsInBin: List<ProductInBinInfo>
)

@JsonClass(generateAdapter = true)
data class ProductInBinInfo(
    @Json(name = "binLocation") val binLocation: String,
    @Json(name = "itemNumber") val itemNumber: String,
    @Json(name = "itemName") val itemName: String,
    @Json(name = "itemSize") val itemSize: String,
    @Json(name = "quantityOnHand") val quantityOnHand: Double,
    @Json(name = "quantityInPicking") val quantityInPicking: Double,
    @Json(name = "expirationDateOfItem") val expirationDate: String?,
    @Json(name = "barCode") val barCode: String?,
    @Json(name = "unitOfMeasurement") val unitOfMeasurement: String,
    @Json(name = "quantityInUOM") val quantityInUOM: Double,
    @Json(name = "companyCode") val companyCode: String,
    @Json(name = "itemDetails") val itemDetails: String

    )
