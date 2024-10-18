package com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseWrapperUser(
    @Json (name = "response") val response: Userinfo
)

@JsonClass(generateAdapter = true)
data class Userinfo(
    @Json(name = "isSignedIn") val isSignedIn: Boolean
)
