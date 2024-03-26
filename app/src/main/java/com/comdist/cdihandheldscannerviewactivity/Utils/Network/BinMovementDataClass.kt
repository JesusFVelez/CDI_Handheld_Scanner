package com.comdist.cdihandheldscannerviewactivity.Utils.Network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


// getAllBins - backend method output data classes
@JsonClass(generateAdapter = true)
data class getAllBinsResponseWrapper(
    val response:getALlBinsResponse
)

@JsonClass(generateAdapter = true)
data class getALlBinsResponse(
    @Json(name = "ttBinList") val listOfBinsWrapper:listOfBinsWrapper
)

@JsonClass(generateAdapter = true)
data class listOfBinsWrapper(
    @Json(name = "tt-bin-list") val listOfBins: List<binInfo>
)

@JsonClass(generateAdapter = true)
data class binInfo(
    @Json(name = "bin-number") val binNumber: String
)







// getAllItemsInBin - backend method output data classes
@JsonClass(generateAdapter = true)
data class listOfItemsInBinResponseWrapper(
    val response: listOfItemsInBinResponse
)

@JsonClass(generateAdapter = true)
data class listOfItemsInBinResponse(
    @Json(name = "ttBinItem") val listOfItemsInBinWrapper:listOfItemsInBinWrapper
)

@JsonClass(generateAdapter = true)
data class listOfItemsInBinWrapper(
    @Json(name = "tt-bin-item") val listOfItemsInBin:List<itemsInBin>
)

@JsonClass(generateAdapter = true)
data class itemsInBin(
    @Json(name = "tt-warehouse-no") val warehouseNumber: Int,
    @Json(name = "tt-bin-loc") val binLocation: String,
    @Json(name = "tt-item-number") val itemNumber: String,
    @Json(name = "tt-type") val type:String,
    @Json(name = "tt-type-data") val typeData: String,
    @Json(name = "tt-style-color") val styleColor: String,
    @Json(name = "tt-qty-on-hand") val quantityOnHand: Float,
    @Json(name = "tt-in-picking") val quantityInPicking: Float,
    @Json(name = "tt-size") val size: String,
    @Json(name = "tt-date-created") val dateCreated: String,
    @Json(name = "tt-company-code") val companyCode:String,
    @Json(name = "tt-qty-picked") val quantityPicked: Float,
    @Json(name = "tt-lot-number") val lotNumber: String,
    @Json(name = "tt-expire-date") val expireDate: String,
    @Json(name = "tt-weight") val weight: Float,
    @Json(name = "tt-row-id") val rowID: String
    )



// confirmBin - backend method output data classes

@JsonClass(generateAdapter = true)
data class confirmBinResponseWrapper(
    val response: confirmBinResponse
)

@JsonClass(generateAdapter = true)
data class confirmBinResponse(
    @Json(name = "wasBinConfirmed") val wasBinConfirmed: Boolean
)





// isQuantityValid - backend method output data classes
@JsonClass(generateAdapter = true)
data class isQuantityValidResponseWrapper(
    val response: isQuantityValidResponse
)

@JsonClass(generateAdapter = true)
data class isQuantityValidResponse(
    @Json(name = "wasQuantityValid") val wasQuantityValid: Boolean,
    @Json(name = "errorMessage") val errorMessage: String
)





// moveItemBetweenBins - backend method output data classes
@JsonClass(generateAdapter = true)
data class moveItemBetweenBinsResponseWrapper(
    val response: moveItemBetweenBinsResponse
)

@JsonClass(generateAdapter = true)
data class moveItemBetweenBinsResponse(
    @Json(name = "wasItemMoved") val wasItemMoved: Boolean,
    @Json(name = "errorMessage") val errorMessage: String
)

