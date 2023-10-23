package com.example.cdihandheldscannerviewactivity.Utils.Network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


// All the JSON Response Data classes for the getBinsThatHaveItem backend end point
@JsonClass(generateAdapter = true)
data class ResponseWrapperBinsWithProduct(
    val response: ResponseBinsWithProduct
)

@JsonClass(generateAdapter = true)
data class ResponseBinsWithProduct(
    @Json(name = "hasBinBeenFoundWithItem") val hasBinBeenFoundWithItem: Boolean,
    @Json(name = "binsThatHaveItem") val binsThatHaveItem: BinsThatHaveItemWrapper
)

@JsonClass(generateAdapter = true)
data class BinsThatHaveItemWrapper(
    @Json(name = "binsThatHaveItem") val binsThatHaveItem:List<BinInfo>
)

@JsonClass(generateAdapter = true)
data class BinInfo(
    @Json(name = "itemNumberOfItemInBIn") val itemNumberOfItemInBIn: String,
    @Json(name = "binLocation") val binLocation: String,
    @Json(name = "qtyOnHandInBin") val qtyOnHandInBin: Double,
    @Json(name = "qtyInPickingInBin") val qtyInPickingInBin : Double,
    @Json(name = "qtyAvailable") val qtyAvailable : Double,
    @Json(name = "expirationDateOfItemInBin") val expirationDateOfItemInBin: String?,
    @Json(name = "creationDateOfBin") val creationDateOfBin: String? ,
    @Json(name = "binType") val binType: String,
)








// All the JSON Response Data classes for the getItemDetails backend end point
@JsonClass(generateAdapter = true)
data class ResponseWrapperItemDetailsForBinSearch(
    @Json(name = "response") val response: ResponseItemDetailsForBinSearch
)



@JsonClass(generateAdapter = true)
data class ResponseItemDetailsForBinSearch(
    @Json(name = "isBarCodeValid") val isBarCodeValid: Boolean,
    @Json(name = "barcodeErrorMessage") val barcodeErrorMessage: String,
    @Json(name = "itemDetailsForBinSearch") val itemDetailsForBinSearch: ItemDetailsForBinSearchWrapper
)

@JsonClass(generateAdapter = true)
data class ItemDetailsForBinSearchWrapper(
    @Json(name = "itemDetailsForBinSearch") val itemDetailsForBinSearch: List<ItemDetailsForBinSearch>
)


@JsonClass(generateAdapter = true)
data class ItemDetailsForBinSearch(
    @Json(name = "itemNumber") val itemNumber: String,
    @Json(name = "inventoryType") val inventoryType: String,
    @Json(name = "vendorItemNumber") val vendorItemNumber: String,
    @Json(name = "itemName") val itemName: String,
    @Json(name = "barCode") val barCode: String,
    @Json(name = "UnitOfMeasurement") val unitOfMeasurement: String,
    @Json(name = "quantityInUOM") val quantityInUOM: Double,
    @Json(name = "totalQuantityOnHand") val totalQuantityOnHand: Double,
    @Json(name = "binForPicking") val binForPicking: String,
    @Json(name = "itemDetails") val itemDetails: String
)

