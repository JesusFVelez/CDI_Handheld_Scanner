package com.example.cdihandheldscannerviewactivity.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseWrapperWarehouse(
    val response: ResponseWarehouse
)

@JsonClass(generateAdapter = true)
data class ResponseWarehouse(
    val warehouses: WarehouseWrapper
)

@JsonClass(generateAdapter = true)
data class WarehouseWrapper(
    val warehouses: List<WarehouseInfo>
)

@JsonClass(generateAdapter = true)
data class WarehouseInfo(
    @Json(name = "warehouseNumber") val warehouseNumber: Int,
    @Json(name = "warehouseName") val warehouseName: String
)
