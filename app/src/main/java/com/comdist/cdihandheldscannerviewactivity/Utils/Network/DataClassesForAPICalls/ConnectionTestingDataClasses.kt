package com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ConnectionTestingWrapper(
    @Json (name = "response") val response: ConnectionTestingResponse
)

@JsonClass(generateAdapter = true)
data class ConnectionTestingResponse(
    @Json(name = "hasConnectionSucceeded") val hasConnectionSucceeded: Boolean
)
