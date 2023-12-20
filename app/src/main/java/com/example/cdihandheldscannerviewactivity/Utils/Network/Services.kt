package com.example.cdihandheldscannerviewactivity.Utils.Network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

// Base URL for the API calls
// 76.72.245.174 IP Publico de CDI
private const val BASE_URL = "http://10.0.0.43:8811/HandHeldScannerProject/rest"
private val SERVICE_PATHS = mapOf("ViewBinsThatHaveItem" to "/ViewBinsThatHaveItemService/",
                                  "CountAllItemsInWarehouse" to "/CountAllItemsInWarehouseService/",
                                  "login" to "/loginService/",
                                  "GeneralServices" to "/generalServices/",
                                  "ViewProductsInBin" to "/ViewProductsInBinService/",
                                  "ItemPicking" to "/ItemPickingForDispatchService/")


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

// Retrofit instance for making API calls
private val retrofitItemPickingForDispatchService = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))// Use Moshi for JSON conversion
    .baseUrl(BASE_URL + SERVICE_PATHS["ItemPicking"])// Set the base URL for API calls
    .build()





interface LoginServices{
    // Endpoint for getting companies
    @GET("getCompanies")
    suspend fun getCompanies(): ResponseWrapper

    // Endpoint for checking if a user is logged in
    @POST("login")
    fun isLogedIn(@Body user: RequestUser): Call<ResponseWrapperUser>
}

interface ViewBinsThatHaveItemServices{
    @GET("getBinsThatHaveItem")
    suspend fun getAllBinsThatHaveProduct(@Query("companyID") companyID:String, @Query("warehouseNumber") warehouseNumber: Int, @Query("itemNumber") itemNumber:String): ResponseWrapperBinsWithProduct

    @GET("getItemDetailsForBinSearch")
    suspend fun getItemDetailsForBinSearch(@Query("companyID") companyID: String, @Query("warehouseNumber") warehouseNumber: Int, @Query("scannedCode") scannedCode: String) : ResponseWrapperItemDetailsForBinSearch
}

interface ItemPickingForDispatchServices{
    @GET("getAllItemsInOrder")
    suspend fun getAllItemsInOrder(@Query("orderNumber") orderNumber: String, @Query("companyID") companyID:String, @Query("pickerUserName") pickerUserName:String) :ItemPickingResponseWrapper

    @GET("confirmBin")
    suspend fun confirmBin(@Query("binLocationToConfirm") binLocationToConfirm: String, @Query("pickingROWID") pickingROWID: String) :BinConfirmationResponseWrapper

    @GET("confirmItem")
    suspend fun confirmItem(@Query("scannedCode") scannedCode: String, @Query("actualItemNumber") actualItemNumber: String, @Query("companyID") companyID: String, @Query("orderNumber") orderNumber: String) :ItemConfirmationResponseWrapper

    @GET("verifyIfOrderHasPicking")
    suspend fun verifyIfOrderHasPicking(@Query("orderNumber") orderNumber: String, @Query("companyID") companyID: String) :OrderHasPickingResponseWrapper

    @GET("confirmOrder")
    suspend fun confirmOrder(@Query("orderNumber") orderNumber: String, @Query("companyID") companyID: String) :ConfirmOrderResponseWrapper

    @GET("verifyIfClientAccountIsClosed")
    suspend fun verifyIfClientAccountIsClosed(@Query("orderNumber") orderNumber: String, @Query("companyID") companyID: String) :VerifyClientResponseWrapper

    @PUT("finishPickingForSingleItem")
    suspend fun finishPickingForSingleItem(@Query("pickingROWID")pickingROWID: String, @Query("userNameOfPicker") userNameOfPicker:String, @Query("quantityBeingPicked") quantityBeingPicked:Float): finishPickingForSingleItemResponseWrapper

    @POST("startPickerTimer")
    suspend fun startPickerTimer(@Body request: RequestTimerParamsWrapper)

    @POST("endPickerTimer")
    suspend fun endPickerTimer(@Body request: RequestTimerParamsWrapper)
}

interface ViewProductsInBinServices{
    // Endpoint for getting all items in a bin. The Query annotations are used to specify the query parameters for the API call
    @GET("getItemsInBin")
    suspend fun getAllItemsInBin(@Query("companyCode") companyCode: String, @Query("warehouseNumber") warehouseNumber: Int, @Query("binLocation") binLocation: String): ResponseWrapperProductsInBin
}

interface GeneralServices{
    // Endpoint for getting available warehouses
    @GET("getWarehouses")
    suspend fun getWarehousesAvailable(): ResponseWrapperWarehouse

}


// Data class for the user request
data class RequestUser(val request: User)
data class User(val userName: String, val password: String, val company: String)

data class RequestTimerParamsWrapper(val request: RequestTimerParams)
data class RequestTimerParams(val orderNumber: String, val userNameOfPicker: String)
// Object for accessing the API services
object ScannerAPI {
    val LoginService : LoginServices by lazy{
        retrofitLogin.create(LoginServices::class.java)
    }

//    val CountAllItemsInWarehouseService : Services by lazy{
//        retrofitCountAllItemsInWarehouseService.create(Services::class.java)
//    }

    val GeneralService : GeneralServices by lazy{
        retrofitGeneralService.create(GeneralServices::class.java)
    }

    val ViewBinsThatHaveItemService : ViewBinsThatHaveItemServices by lazy{
        retrofitViewBinsThatHaveItemService.create(ViewBinsThatHaveItemServices::class.java)
    }

    val ViewProductsInBinService : ViewProductsInBinServices by lazy{
        retrofitViewProductsInBinService.create(ViewProductsInBinServices::class.java)
    }

    val ItemPickingForDispatchService: ItemPickingForDispatchServices by lazy{
        retrofitItemPickingForDispatchService.create(ItemPickingForDispatchServices::class.java)
    }

}
