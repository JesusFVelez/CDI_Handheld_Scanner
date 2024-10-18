package com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ResponseWrapperProductsInBin(
    val response: ResponseProdInBin
)


@JsonClass(generateAdapter = true)
data class ResponseProdInBin(
    @Json(name = "wasBinFound") val wasBinFound: Boolean,
    @Json(name = "errorMessage") val errorMessage: String,
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


// API response for Items In Bin using paging

@JsonClass(generateAdapter = true)
data class ResponseWrapperProductsInBinPaging(
    val response: ResponseProdInBinPaging
)

@JsonClass(generateAdapter = true)
data class ResponseProdInBinPaging(
    @Json(name = "wasBinFound") val wasBinFound: Boolean,
    @Json(name = "errorMessage") val errorMessage: String,
    @Json(name = "itemsInBin") val itemsInBin: ResponseContentProdInBIn,
    @Json(name = "pageLimit") val maxNumberOfPagesForList: Int
)

