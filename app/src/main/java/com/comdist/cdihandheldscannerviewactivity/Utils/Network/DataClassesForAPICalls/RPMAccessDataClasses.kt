package com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


// checkIfUserHasAccessToFunctionality data classes
@JsonClass(generateAdapter = true)
data class RPMAccessResponseWrapper(
    val response: RPMAccessResponse
)

@JsonClass(generateAdapter = true)
data class RPMAccessResponse(
    @Json(name = "doesUserHaveAccessToFunc") val doesUserHaveAccessToFunc: Boolean,
    @Json(name = "errorMessage") val errorMessage: String
)



// verifyIfClientUsesRPM data classes
@JsonClass(generateAdapter = true)
data class doesUserHaveRPMResponseWrapper(
    val response: doesUserHaveRPMResponse
)

@JsonClass(generateAdapter = true)
data class doesUserHaveRPMResponse(
    @Json(name = "doesClientUseRPM") val doesClientUseRPM: Boolean
)

