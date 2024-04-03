package com.comdist.cdihandheldscannerviewactivity.Utils.Network

import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.AssignExpDateResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.AssignLotNumberResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.BinConfirmationResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ConfirmOrderResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ConnectionTestingWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.DisplayInfoResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.GetAllItemsInBinForSuggestionResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemConfirmationResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemData
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemPickingResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.OrderHasPickingResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.RPMAccessResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseWrapperBinsWithProduct
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseWrapperGetItem
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseWrapperItemDetailsForBinSearch
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseWrapperProductsInBin
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseWrapperSetBarcode
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseWrapperUser
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseWrapperValidateBarcode
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseWrapperWarehouse
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseWrapperWasItemFound
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.VerifyClientResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.doesUserHaveRPMResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.finishPickingForSingleItemResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.getOrdersForSuggestionWrapper
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
        const val RPMAccess: String = "/RPMAccessService/"
        const val AssignBarcode: String = "/AssignBarcodeService/"
        const val AssignExpirationDateService: String = "/AssignExpirationDateService/"
        const val AssignLotNumberService: String = "/AssignLotNumberService/"
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
interface SuggestionServices {
    @GET("getSuggestionsForItemOrBin")
    suspend fun getSuggestionsForItemOrBin(@Query("query") query: List<ItemData>): GetAllItemsInBinForSuggestionResponseWrapper
}

interface AssignLotNumberResources {
    @PUT("assignLotNumberToBinItem")
    suspend fun assignLotNumberToBinItem(@Query("pItemNumber") pItemNumber:String, @Query("companyID") companyID: Int, @Query("pBinLocation") pBinLocation:String, @Query("pLotNumber") pLotNumber:String, @Query("warehouseNumber") warehouseNumber: String): AssignLotNumberResponseWrapper
}

//Assign Expiration Date inaterface
interface AssignExpirationDateResources {
    @PUT("assignExpireDate")
    suspend fun assignExpireDate(@Query("pItemNumber")pItemNumber:String, @Query("pBinLocation")pBinLocation:String, @Query("pExpireDate")pExpireDate:String, @Query("pLotNumber") pLotNumber:String): AssignExpDateResponseWrapper

    @GET("getItemInformation")
    suspend fun getItemInformation(@Query("pItemNumber")pItemNumber:String, @Query("pBinLocation")pBinLocation:String, @Query("pLotNumber") pLotNumber: String): DisplayInfoResponseWrapper
}

interface LoginServices{
    // Endpoint for getting companies
    @GET("getCompanies")
    suspend fun getCompanies(): ResponseWrapper

    // Endpoint for checking if a user is logged in
    @POST("login")
    suspend fun isLoggedIn(@Body user: RequestUser): ResponseWrapperUser

    //Endpoint for testing the connection
    @GET("testConnection")
    fun testConnection(): Call<ConnectionTestingWrapper>

    // Endpoint for getting available warehouses
    @GET("getWarehouses")
    suspend fun getWarehousesAvailable(): ResponseWrapperWarehouse
}

interface RPMAccessServices{
    @GET("checkIfUserHasAccessToFunctionality")
    suspend fun checkIfUserHasAccessToFunctionality(@Query("userName")userName:String, @Query("functionality") functionality:String, @Query("companyID")companyID:String): RPMAccessResponseWrapper

    @GET("verifyIfClientUsesRPM")
    suspend fun verifyIfClientUsesRPM(@Query("companyID") companyID: String): doesUserHaveRPMResponseWrapper
}

interface ViewBinsThatHaveItemServices{
    @GET("getBinsThatHaveItem")
    suspend fun getAllBinsThatHaveProduct(@Query("companyID") companyID:String, @Query("warehouseNumber") warehouseNumber: Int, @Query("itemNumber") itemNumber:String): ResponseWrapperBinsWithProduct

    @GET("getItemDetailsForBinSearch")
    suspend fun getItemDetailsForBinSearch(@Query("companyID") companyID: String, @Query("warehouseNumber") warehouseNumber: Int, @Query("scannedCode") scannedCode: String) : ResponseWrapperItemDetailsForBinSearch
}

interface ItemPickingForDispatchServices{
    @GET("getAllItemsInOrder")
    suspend fun getAllItemsInOrder(@Query("orderNumber") orderNumber: String, @Query("companyID") companyID:String, @Query("pickerUserName") pickerUserName:String) : ItemPickingResponseWrapper

    @GET("confirmBin")
    suspend fun confirmBin(@Query("binLocationToConfirm") binLocationToConfirm: String, @Query("pickingROWID") pickingROWID: String) : BinConfirmationResponseWrapper

    @GET("confirmItem")
    suspend fun confirmItem(@Query("scannedCode") scannedCode: String, @Query("actualItemNumber") actualItemNumber: String, @Query("companyID") companyID: String, @Query("orderNumber") orderNumber: String) : ItemConfirmationResponseWrapper

    @GET("verifyIfOrderHasPicking")
    suspend fun verifyIfOrderHasPicking(@Query("orderNumber") orderNumber: String, @Query("companyID") companyID: String) : OrderHasPickingResponseWrapper

    @GET("confirmOrder")
    suspend fun confirmOrder(@Query("orderNumber") orderNumber: String, @Query("companyID") companyID: String) : ConfirmOrderResponseWrapper

    @GET("verifyIfClientAccountIsClosed")
    suspend fun verifyIfClientAccountIsClosed(@Query("orderNumber") orderNumber: String, @Query("companyID") companyID: String) : VerifyClientResponseWrapper

    @PUT("finishPickingForSingleItem")
    suspend fun finishPickingForSingleItem(@Query("pickingROWID")pickingROWID: String, @Query("userNameOfPicker") userNameOfPicker:String, @Query("quantityBeingPicked") quantityBeingPicked:Float): finishPickingForSingleItemResponseWrapper

    @POST("startPickerTimer")
    suspend fun startPickerTimer(@Body request: RequestTimerParamsWrapper)

    @PUT("updatePickerTimer")
    suspend fun updatePickerTimer(@Query("orderNumber")orderNumber: String, @Query("pickerUserName")pickerUserName: String)

    @GET("getAllOrdersInPickingForSuggestion")
    suspend fun getAllOrdersInPickingForSuggestion(@Query("companyID") companyID: String): getOrdersForSuggestionWrapper
}

// All services for assign barcode
interface AssignBarcodeToItemServices {
    @GET("getItem")
    suspend fun getItems(@Query("itemNumber") itemNumber: String): ResponseWrapperGetItem

    @GET("wasItemFound")
    suspend fun wasItemFound(@Query("itemNumber") itemNumber: String): ResponseWrapperWasItemFound

    @GET("validateBarcode")
    suspend fun validateBarcode(@Query("barCode") barCode: String): ResponseWrapperValidateBarcode

    @PUT("setBarcode")
    suspend fun setBarcode(@Query("itemNumber") itemNumber: String, @Query("selectedBarcode") selectedBarcode: String): ResponseWrapperSetBarcode
}

interface ViewProductsInBinServices{
    // Endpoint for getting all items in a bin. The Query annotations are used to specify the query parameters for the API call
    @GET("getItemsInBin")
    suspend fun getAllItemsInBin(@Query("companyCode") companyCode: String, @Query("warehouseNumber") warehouseNumber: Int, @Query("binLocation") binLocation: String): ResponseWrapperProductsInBin
}

interface GeneralServices{

}


// Data class for the user request
data class RequestUser(val request: User)
data class User(val userName: String, val password: String, val company: String, val warehouseNumber: Int)

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

    fun getRPMAccessService():RPMAccessServices{
        val retrofit = createRetrofitInstance(ipAddress, portNumber, ServicePaths.RPMAccess)
        return retrofit.create(RPMAccessServices::class.java)
    }

    fun getAssignBarcodeService(): AssignBarcodeToItemServices{
        val retrofit = createRetrofitInstance(ipAddress, portNumber, ServicePaths.AssignBarcode)
        return retrofit.create(AssignBarcodeToItemServices::class.java)
    }

    fun getAssignExpirationDateService(): AssignExpirationDateResources{
        val retrofit = createRetrofitInstance(ipAddress, portNumber, ServicePaths.AssignExpirationDateService)
        return retrofit.create(AssignExpirationDateResources::class.java)
    }

    fun getSuggestionService(): SuggestionServices {
        val retrofit = createRetrofitInstance(ipAddress, portNumber, "/suggestionService/")
        return retrofit.create(SuggestionServices::class.java)
    }

    fun getAssignLotNumberService(): AssignLotNumberResources {
        val retrofit = createRetrofitInstance(ipAddress, portNumber, ServicePaths.AssignLotNumberService)
        return retrofit.create(AssignLotNumberResources::class.java)
    }

}
