package com.example.cdihandheldscannerviewactivity.Utils.Network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// Base URL for the API calls
// 76.72.245.174 IP Publico de CDI
private const val BASE_URL = "http://10.0.0.43:8811/HandHeldScannerProject/rest"
private val SERVICE_PATHS = mapOf("ViewBinsThatHaveItem" to "/ViewBinsThatHaveItemService/",
                                  "CountAllItemsInWarehouse" to "/CountAllItemsInWarehouseService/",
                                  "login" to "/loginService/",
                                  "GeneralServices" to "/generalServices/",
                                  "ViewProductsInBin" to "/ViewProductsInBinService/")


// Moshi instance for converting JSON to Kotlin objects
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// Retrofit instance for making API calls
private val retrofitLogin = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi)) // Use Moshi for JSON conversion
    .baseUrl(BASE_URL + SERVICE_PATHS["login"]) // Set the base URL for API calls
    .build()

// Retrofit instance for making API calls
private val retrofitCountAllItemsInWarehouse = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi)) // Use Moshi for JSON conversion
    .baseUrl(BASE_URL + SERVICE_PATHS["CountAllItemsInWarehouse"]) // Set the base URL for API calls
    .build()

// Retrofit instance for making API calls
private val retrofitGeneralService = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi)) // Use Moshi for JSON conversion
    .baseUrl(BASE_URL + SERVICE_PATHS["GeneralServices"]) // Set the base URL for API calls
    .build()

// Retrofit instance for making API calls
private val retrofitViewBinsThatHaveItemService = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi)) // Use Moshi for JSON conversion
    .baseUrl(BASE_URL + SERVICE_PATHS["ViewBinsThatHaveItem"]) // Set the base URL for API calls
    .build()

// Retrofit instance for making API calls
private val retrofitViewProductsInBinService = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi)) // Use Moshi for JSON conversion
    .baseUrl(BASE_URL + SERVICE_PATHS["ViewProductsInBin"]) // Set the base URL for API calls
    .build()



interface loginServices{
    // Endpoint for getting companies
    @GET("getCompanies")
    suspend fun getCompanies(): ResponseWrapper

    // Endpoint for checking if a user is logged in
    @POST("login")
    fun isLogedIn(@Body user: requestUser): Call<ResponseWrapperUser>
}

interface viewBinsThatHaveItemServices{
    @GET("getBinsThatHaveItem")
    suspend fun getAllBinsThatHaveProduct(@Query("companyID") companyID:String, @Query("warehouseNumber") warehouseNumber: Int, @Query("itemNumber") itemNumber:String): ResponseWrapperBinsWithProduct

    @GET("getItemDetailsForBinSearch")
    suspend fun getItemDetailsForBinSearch(@Query("companyID") companyID: String, @Query("warehouseNumber") warehouseNumber: Int, @Query("scannedCode") scannedCode: String) : ResponseWrapperItemDetailsForBinSearch


}

interface viewProductsInBinServices{
    // Endpoint for getting all items in a bin. The Query annotations are used to specify the query parameters for the API call
    @GET("getItemsInBin")
    suspend fun getAllItemsInBin(@Query("companyCode") companyCode: String, @Query("warehouseNumber") warehouseNumber: Int, @Query("binLocation") binLocation: String): ResponseWrapperProductsInBin
}

interface generalServives{
    // Endpoint for getting available warehouses
    @GET("getWarehouses")
    suspend fun getWarehousesAvailable(): ResponseWrapperWarehouse

}



// Data class for the user request
data class requestUser(val request: User)
data class User(val userName: String, val password: String, val company: String)

// Object for accessing the API services
object ScannerAPI {
    val LoginService : loginServices by lazy{
        retrofitLogin.create(loginServices::class.java)
    }

//    val CountAllItemsInWarehouseService : Services by lazy{
//        retrofitCountAllItemsInWarehouseService.create(Services::class.java)
//    }

    val GeneralService : generalServives by lazy{
        retrofitGeneralService.create(generalServives::class.java)
    }

    val ViewBinsThatHaveItemService : viewBinsThatHaveItemServices by lazy{
        retrofitViewBinsThatHaveItemService.create(viewBinsThatHaveItemServices::class.java)
    }

    val ViewProductsInBinService : viewProductsInBinServices by lazy{
        retrofitViewProductsInBinService.create(viewProductsInBinServices::class.java)
    }

}
