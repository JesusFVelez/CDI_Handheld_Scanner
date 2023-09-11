package com.example.cdihandheldscannerviewactivity.networkUtils

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ResponseWrapper(
    val response: Response
)

@JsonClass(generateAdapter = true)
data class Response(
    val companies: CompanyWrapper
)

@JsonClass(generateAdapter = true)
data class CompanyWrapper(
    val companies: List<Company>
)

@JsonClass(generateAdapter = true)
data class Company(
    @Json(name = "companyID") val companyID: String,
    @Json(name = "companyName") val companyName: String
)
