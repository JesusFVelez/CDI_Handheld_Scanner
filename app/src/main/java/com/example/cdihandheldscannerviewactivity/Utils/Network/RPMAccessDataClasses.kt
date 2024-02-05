package com.example.cdihandheldscannerviewactivity.Utils.Network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RPMAccessResponseWrapper(
    @Json(name = "response") val response: RPMAccessResponse
)

data class RPMAccessResponse(
    @Json(name = "doesUserHaveAccessToFunc") val doesUserHaveAccessToFunc: Boolean,
    @Json(name = "errorMessage") val errorMessage: String
)
