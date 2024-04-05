package com.comdist.cdihandheldscannerviewactivity.Utils.Network

import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.BinConfirmationResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ConfirmOrderResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ConnectionTestingWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemConfirmationResponseWrapper
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
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.confirmBinResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.doesUserHaveRPMResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.finishPickingForSingleItemResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.getAllBinsResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.getOrdersForSuggestionWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.isQuantityValidResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.listOfItemsInBinResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.moveItemBetweenBinsResponseWrapper
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
        const val MoveItemsBetweenBins:String = "/MoveItemsBetweenBinsService/"
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


interface MoveItemsBetweenBinsServices{
    // Endpoint for getting all the bins
    @GET("getAllBins")
    suspend fun getAllBins(@Query("companyID") companyID: String ,@Query("warehouse") warehouseNumber: Int): getAllBinsResponseWrapper

    // Endpoint for getting all the items in a bin
    @GET("getAllItemsInAllBins")
    suspend fun getAllItemsInAllBin(@Query("companyID") companyID: String ,@Query("warehouse") warehouseNumber: Int): listOfItemsInBinResponseWrapper

    //Endpoint for confirming that the entered bin exists
    @GET("confirmBin")
    suspend fun confirmBin(@Query("binNumber") binLocation:String, @Query("companyID") companyID: String, @Query("warehouse") warehouseNumber: Int): confirmBinResponseWrapper

    //Endpoint for verifying if the quantity entered can be used
    @GET("isQuantityValid")
    suspend fun verifyIfQuantityIsValid(@Query("quantity") quantityToVerify: Float, @Query("rowNumber") rowID: String): isQuantityValidResponseWrapper

    //Endpoint for moving items from one bin to another bin
    @GET("moveItemBetweenBins")
    suspend fun moveItemBetweenBins(@Query("rowNumber") rowID:String, @Query("newBin") newBin:String, @Query("quantity") quantityToMove: Float): moveItemBetweenBinsResponseWrapper

    //Endpoint for removing an item from a bin
    @GET("removeItem")
    suspend fun removeItemFromBin(@Query("binNumber")binLocation: String, @Query("itemNumber") itemNumber: String, @Query("companyID") companyID: String, @Query("warehouse") warehouseNumber: Int)
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

    fun getMovingItemsBetweenBinsService():MoveItemsBetweenBinsServices{
        val retrofit = createRetrofitInstance(ipAddress, portNumber, ServicePaths.MoveItemsBetweenBins)
        return retrofit.create(MoveItemsBetweenBinsServices::class.java)
    }


}
