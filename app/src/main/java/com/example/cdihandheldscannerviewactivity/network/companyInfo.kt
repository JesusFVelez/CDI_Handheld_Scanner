package com.example.cdihandheldscannerviewactivity.network

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson
import org.json.JSONArray



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
