package com.example.cdihandheldscannerviewactivity.Utils.Network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkDetailsResponseWrapper(
    @Json(name = "response") val response: NetworkDetailsResponse
)

@JsonClass(generateAdapter = true)
data class NetworkDetailsResponse(
    @Json(name = "hasTooManyConnections") val hasTooManyConnections: Boolean
)

