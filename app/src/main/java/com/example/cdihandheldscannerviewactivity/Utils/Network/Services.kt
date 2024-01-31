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


class ServicePaths{
    companion object {
        const val ViewBinsThatHaveItem:String = "/ViewBinsThatHaveItemService/"
        const val CountAllItemsInWarehouse:String = "/CountAllItemsInWarehouseService/"
        const val login:String = "/loginService/"
        const val GeneralServices:String = "/generalServices/"
        const val ViewProductsInBin:String = "/ViewProductsInBinService/"
        const val ItemPicking: String = "/ItemPickingForDispatchService/"
    }
}


fun createRetrofitInstance(ipAddress: String, portNumber: String, servicePath: String): Retrofit {
    val baseUrl = "http://${ipAddress}:${portNumber}/HandHeldScannerProject/rest"
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    return Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(baseUrl + servicePath)
        .build()
}

interface LoginServices{
    // Endpoint for getting companies
    @GET("getCompanies")
    suspend fun getCompanies(): ResponseWrapper

    // Endpoint for checking if a user is logged in
    @POST("login")
    fun isLogedIn(@Body user: RequestUser): Call<ResponseWrapperUser>

    //Endpoint for testing the connection
    @GET("testConnection")
    fun testConnection(): Call<ConnectionTestingWrapper>
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
    private lateinit var ipAddress: String
    private lateinit var portNumber: String

    fun setIpAddressAndPortNumber(ip: String, portNum: String){
        ipAddress = ip
        portNumber = portNum
    }
    fun getLoginService(): LoginServices {
        val retrofit = createRetrofitInstance(ipAddress, portNumber, ServicePaths.login)
        return retrofit.create(LoginServices::class.java)
    }

    fun getGeneralService(): GeneralServices {
        val retrofit = createRetrofitInstance(ipAddress, portNumber, ServicePaths.GeneralServices)
        return retrofit.create(GeneralServices::class.java)
    }

    fun getViewBinsThatHaveItemService(): ViewBinsThatHaveItemServices {
        val retrofit = createRetrofitInstance(ipAddress, portNumber, ServicePaths.ViewBinsThatHaveItem)
        return retrofit.create(ViewBinsThatHaveItemServices::class.java)
    }

    fun getViewProductsInBinService(): ViewProductsInBinServices {
        val retrofit = createRetrofitInstance(ipAddress, portNumber, ServicePaths.ViewProductsInBin)
        return retrofit.create(ViewProductsInBinServices::class.java)
    }

    fun getItemPickingForDispatchService():ItemPickingForDispatchServices{
        val retrofit = createRetrofitInstance(ipAddress, portNumber, ServicePaths.ItemPicking)
        return retrofit.create(ItemPickingForDispatchServices::class.java)
    }


}
