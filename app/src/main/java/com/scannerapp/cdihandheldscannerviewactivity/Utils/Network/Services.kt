package com.scannerapp.cdihandheldscannerviewactivity.Utils.Network

import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.GetAllBinNumbersResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.GetAllItemsInBinResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.GetBinsByClassCodeByVendorAndByItemNumberResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.GetItemDetailsForPopupResponseWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseItemConfirmWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.UpdateCountResponseWrapper
import com.example.cdihandheldscannerviewactivity.Utils.Network.NetworkDetailsResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.AddBarcodeToItemResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.AssignExpDateResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.AssignLotNumberResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.BinConfirmationResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ConfirmOrderResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ConnectionTestingWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.DisplayInfoResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.GetAllBarcodesForItemResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.GetAllItemsInBinForSuggestionResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.GetItemsInBinFromBarcodeResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemConfirmationResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemPickingResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.OrderHasPickingResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.RPMAccessResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.RemoveBarcodeFromItemResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseConfirmBin
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseConfirmItemWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseDeleteItemFromDoorBin
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseGetPreReceiving
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseItemsInBinWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseMoveItemFromDoorBin
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseMoveItemToDoorBin
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponsePreReceiving
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseValidateLotNumberWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseWrapperBinsWithProduct
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseWrapperDoorBinListWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseWrapperItemDetailsForBinSearch
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseWrapperItemDetailsWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseWrapperProductsInBin
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseWrapperUpdateBarcode
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseWrapperUser
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseWrapperWarehouse
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.VerifyClientResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.confirmBinResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.doesUserHaveRPMResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.finishPickingForSingleItemResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.getAllBinsResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.getOrdersForSuggestionWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.isQuantityValidResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.listOfItemsInBinResponseWrapper
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.moveItemBetweenBinsResponseWrapper
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


class ServicePaths{
    companion object {
        const val ViewBinsThatHaveItem:String = "/ViewBinsThatHaveItemService/"
        const val login:String = "/loginService/"
        const val GeneralServices:String = "/generalServices/"
        const val ViewProductsInBin:String = "/ViewProductsInBinService/"
        const val ItemPicking: String = "/ItemPickingForDispatchService/"
        const val RPMAccess: String = "/RPMAccessService/"
        const val AssignBarcode: String = "/AssignBarcodeService/"
        const val ReceivingProducts: String = "/ReceivingProductsService/"
        const val MoveItemsBetweenBins:String = "/MoveItemsBetweenBinsService/"
        const val AssignExpirationDateService: String = "/AssignExpirationDateService/"
        const val AssignLotNumberService: String = "/AssignLotNumberService/"
        const val InventoryCountService: String = "/InventoryCountService/"
        const val EditItemService : String = "/EditItemService/"
    }
}


fun createRetrofitInstance(ipAddress: String, portNumber: String, servicePath: String): Retrofit {
    val baseUrl = "http://${ipAddress}:${portNumber}/HandHeldScannerProject/rest"
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
//    val okHttpClient = OkHttpClient.Builder()
//        .connectTimeout(3, TimeUnit.MINUTES)
//        .readTimeout(3, TimeUnit.MINUTES)
//        .writeTimeout(3, TimeUnit.MINUTES)
//        .build()
    return Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(baseUrl + servicePath)
//        .client(okHttpClient)
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
    @PUT("moveItemBetweenBins")
    suspend fun moveItemBetweenBins(@Query("rowNumber") rowID:String, @Query("newBin") newBin:String, @Query("quantity") quantityToMove: Float): moveItemBetweenBinsResponseWrapper

    //Endpoint for removing an item from a bin
    @GET("removeItem")
    suspend fun removeItemFromBin(@Query("binNumber")binLocation: String, @Query("itemNumber") itemNumber: String, @Query("companyID") companyID: String, @Query("warehouse") warehouseNumber: Int)
}

interface AssignLotNumberResources {
    @PUT("assignLotNumberToBinItem")
    suspend fun assignLotNumberToBinItem(
        @Query("pItemNumber") pItemNumber: String,
        @Query("pCompanyCode") companyID: String,
        @Query("pBinLocation") pBinLocation: String,
        @Query("pLotNumber") pLotNumber: String,
        @Query("pWarehouseNo") warehouseNumber: Int,
        @Query("pOldLot") pOldLot: String?
    ): AssignLotNumberResponseWrapper
}

//Assign Expiration Date interface
interface AssignExpirationDateResources {

    @GET("getAllItemsInBinForSuggestion")
    suspend fun getSuggestionsForItemOrBin(): GetAllItemsInBinForSuggestionResponseWrapper

    @PUT("assignExpireDate")
    suspend fun assignExpireDate(@Query("pItemNumber")pItemNumber:String, @Query("pBinLocation")pBinLocation:String, @Query("pExpireDate")pExpireDate:String, @Query("pLotNumber") pLotNumber:String, @Query("pWarehouseNo") warehouseNumber: Int): AssignExpDateResponseWrapper

    @GET("getItemInformation")
    suspend fun getItemInformation(@Query("pItemNumber")pItemNumber:String, @Query("pBinLocation")pBinLocation:String, @Query("pLotNumber") pLotNumber: String): DisplayInfoResponseWrapper

    @GET("getItemsInBinFromBarcode")
    suspend fun getItemsInBinFromBarcode(
        @Query("pItemNumber") pItemNumber: String
    ): GetItemsInBinFromBarcodeResponseWrapper
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

    @POST("logout")
    fun logoutUser(@Query("companyID") companyID: String):Call<Void>

    @GET("verifyIfNumberOfUsersHasExceeded")
    suspend fun verifyIfNumberOfUsersHasExceeded(@Query("companyID") companyID: String): NetworkDetailsResponseWrapper


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
    suspend fun finishPickingForSingleItem(@Query("pickingROWID")pickingROWID: String, @Query("userNameOfPicker") userNameOfPicker:String, @Query("quantityBeingPicked") quantityBeingPicked:Float, @Query("weightBeingPicked") weightBeingPicked:Float): finishPickingForSingleItemResponseWrapper

    @POST("startPickerTimer")
    suspend fun startPickerTimer(@Body request: RequestTimerParamsWrapper)

    @PUT("updatePickerTimer")
    suspend fun updatePickerTimer(@Query("orderNumber")orderNumber: String, @Query("pickerUserName")pickerUserName: String)

    @GET("getAllOrdersInPickingForSuggestion")
    suspend fun getAllOrdersInPickingForSuggestion(@Query("companyID") companyID: String): getOrdersForSuggestionWrapper
}


interface ReceivingProductsServices {
    @GET("getDoorBins")
    suspend fun getDoorBins(@Query("warehouse") warehouseNumber: Int, @Query("companyID") companyID: String): ResponseWrapperDoorBinListWrapper

    @GET("getItemInfo")
    suspend fun getItemInfo(@Query("scannedCode") scannedCode: String, @Query("warehouse") warehouseNumber: Int, @Query("companyID") companyID: String): ResponseWrapperItemDetailsWrapper

    @GET("getPreReceiving")
    suspend fun getPreReceiving(@Query("binNumber") binNumber: String, @Query("warehouse") warehouseNumber: Int, @Query("companyID") companyID: String): ResponsePreReceiving

    @GET("getPreReceivingInfo")
    suspend fun getPreReceivingInfo(@Query("preReceiving") preReceivingNumber: String, @Query("warehouse") warehouseNumber: Int, @Query("companyID") companyID: String): ResponseGetPreReceiving

    @GET("getItemsInBin")
    suspend fun getItemsInBin(@Query("doorBinNumber") doorBin: String, @Query("warehouse") warehouseNumber: Int, @Query("companyID") companyID: String): ResponseItemsInBinWrapper

    @GET("wasBinFound")
    suspend fun wasBinFound(@Query("binNumber") bunNumber: String, @Query("warehouse") warehouseNumber: Int, @Query("companyID") companyID: String): ResponseConfirmBin

    @PUT("addItemToDoorBin")
    suspend fun addItemToDoorBin(@Query("itemNumber") itemNumber: String, @Query("doorBin") doorBin: String, @Query("quantity") quantity: Int, @Query("lotNumber") lotNumber: String, @Query("weight") weight: Float, @Query("expireDate") expireDate: String, @Query("warehouse") warehouseNumber: Int, @Query("companyID") companyID: String): ResponseMoveItemToDoorBin

    @PUT("moveItemFromDoor")
    suspend fun moveItemFromDoorBin(@Query("rowIDForDoorBin") rowIDForDoorBin: String, @Query("quantity") quantity: Int): ResponseMoveItemFromDoorBin

    @DELETE("deleteItemFromDoorBin")
    suspend fun deleteItemFromDoorInBin(@Query("rowIDForDoorBin") rowIDForDoorBin: String): ResponseDeleteItemFromDoorBin

    @GET("confirmItem")
    suspend fun confirmItem(@Query("scannedCode") scannedCode: String,@Query("receivingNumber") receivingNumber: String, @Query("actualItemNumber") actualItemNumber: String, @Query("companyID") companyID: String, @Query("warehouseNumber") warehouseNumber: Int ) : ResponseConfirmItemWrapper

    @GET("validateWhetherLotIsInIVLOT")
    suspend fun validateWhetherLotIsInIVLOT(@Query("itemNumber") itemNumber: String, @Query("lotNumber") lotNumber: String, @Query("warehouseNumber") warehouseNumber: Int):ResponseValidateLotNumberWrapper


}
interface ViewProductsInBinServices{
    // Endpoint for getting all items in a bin. The Query annotations are used to specify the query parameters for the API call
    @GET("getItemsInBin")
    suspend fun getAllItemsInBin(@Query("companyCode") companyCode: String, @Query("warehouseNumber") warehouseNumber: Int, @Query("binLocation") binLocation: String): ResponseWrapperProductsInBin
}

interface InventoryCountServices {

    @GET("confirmItem")
    suspend fun confirmItem(
        @Query("scannedCode") scannedCode: String,
        @Query("companyID") companyID: String,
        @Query("warehouseNumber") warehouseNumber: Int,
        @Query("actualItemNumber") actualItemNumber: String
    ): ResponseItemConfirmWrapper

    @PUT("UpdateCount")
    suspend fun updateCount(
        @Query("pItemNumber") pItemNumber: String?,
        @Query("pWarehouseNo") pWarehouseNo: Int,
        @Query("pBinLocation") pBinLocation: String,
        @Query("pQtyCounted") pQtyCounted: Int,
        @Query("pWeight") pWeight: Double,
        @Query("pCompanyID") pCompanyID: String,
        @Query("pExpireDate") pExpireDate: String,
        @Query("pLotNumber") pLotNumber: String
    ): UpdateCountResponseWrapper

    @GET("getAllItemsInBinForSuggestion")
    suspend fun getAllItemsInBinForSuggestion(
        @Query("pBinLocation") pBinLocation: String,
        @Query("pWarehouse") pWarehouse: Int,
        @Query("pCompanyID") pCompanyID: String
    ): GetAllItemsInBinResponseWrapper  // Changed to return the correct response wrapper

    @GET("GetBinsByClassCodeByVendorAndByItemNumber")
    suspend fun getBinsByClassCodeByVendorAndByItemNumber(
        @Query("pClassCode") pClassCode: String,
        @Query("pVendor") pVendor: String,
        @Query("pItemNumber") pItemNumber: String,
        @Query("pCompanyID") pCompanyID: String,
        @Query("pWarehouseNo") pWarehouseNo: Int
    ): GetBinsByClassCodeByVendorAndByItemNumberResponseWrapper

    @GET("GetAllBinNumbers")
    suspend fun getAllBinNumbers(
        @Query("pCompanyID") pCompanyID: String,
        @Query("pWarehouseNo") pWarehouseNo: Int
    ): GetAllBinNumbersResponseWrapper

    @GET("getItemDetailsForPopup")
    fun getItemDetailsForPopup(
        @Query("pItemNumberOrBarCode") itemNumberOrBarCode: String,
        @Query("pWarehouse") warehouse: Int,
        @Query("pCompanyID") companyID: String
    ): Call<GetItemDetailsForPopupResponseWrapper>
}

interface EditItemServices{

    val BarcodeService: AssignBarcode
    val AssignLotNumberService: AssignLotNumberResources
    val AssignExpirationDateService: AssignExpirationDateResources

    interface AssignBarcode{
        @GET("barcode")
        suspend fun getBarcodesForItem(
            @Query("itemNumber") itemNumber: String,
            @Query("companyID") companyID: String): GetAllBarcodesForItemResponseWrapper

        @POST("barcode")
        suspend fun addBarcodeToItem(
            @Query("itemNumber") itemNumber: String,
            @Query("companyID") companyID: String,
            @Query("barcodeToAdd") barcodeToAdd: String,
            @Query("isMainBarcode") isMainBarcode: Boolean):AddBarcodeToItemResponseWrapper

        @PUT("barcode")
        suspend fun updateBarcodeOfItem(
            @Query("itemNumber") itemNumber: String,
            @Query("companyID") companyID: String,
            @Query("isMainBarcode") isMainBarcode: Boolean,
            @Query("oldBarcode") oldBarcode: String,
            @Query("newBarcode") newBarcode: String
        ):ResponseWrapperUpdateBarcode

        @DELETE("barcode")
        suspend fun removeBarcodeFromItem(
            @Query("itemNumber") itemNumber: String,
            @Query("companyID") companyID: String,
            @Query("barcodeToRemove") barcodeToRemove: String,
            @Query("isMainBarcode") isMainBarcode: Boolean): RemoveBarcodeFromItemResponseWrapper


    }

    //Assign Expiration Date interface
    interface AssignExpirationDateResources {

        @GET("getAllItemsInBinForSuggestion")
        suspend fun getSuggestionsForItemOrBin(): GetAllItemsInBinForSuggestionResponseWrapper

        @PUT("assignExpireDate")
        suspend fun assignExpireDate(
            @Query("pItemNumber")pItemNumber:String,
            @Query("pBinLocation")pBinLocation:String,
            @Query("pExpireDate")pExpireDate:String,
            @Query("pLotNumber") pLotNumber:String,
            @Query("pWarehouseNo") warehouseNumber: Int
        ): AssignExpDateResponseWrapper

        @GET("getItemInformation")
        suspend fun getItemInformation(
            @Query("pItemNumber")pItemNumber:String,
            @Query("pBinLocation")pBinLocation:String,
            @Query("pLotNumber") pLotNumber: String
        ): DisplayInfoResponseWrapper
    }

    interface AssignLotNumberResources {
        @PUT("assignLotNumberToBinItem")
        suspend fun assignLotNumberToBinItem(
            @Query("pItemNumber") pItemNumber: String,
            @Query("pCompanyCode") companyID: String,
            @Query("pBinLocation") pBinLocation: String,
            @Query("pLotNumber") pLotNumber: String,
            @Query("pWarehouseNo") warehouseNumber: Int,
            @Query("pOldLot") pOldLot: String?
        ): AssignLotNumberResponseWrapper
    }




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

    fun getMovingItemsBetweenBinsService():MoveItemsBetweenBinsServices{
        val retrofit = createRetrofitInstance(ipAddress, portNumber, ServicePaths.MoveItemsBetweenBins)
        return retrofit.create(MoveItemsBetweenBinsServices::class.java)
    }

    fun getReceivingProductService(): ReceivingProductsServices{
        val retrofit = createRetrofitInstance(ipAddress, portNumber, ServicePaths.ReceivingProducts)
        return retrofit.create(ReceivingProductsServices::class.java)
    }

    fun getAssignExpirationDateService(): AssignExpirationDateResources{
        val retrofit = createRetrofitInstance(ipAddress, portNumber, ServicePaths.AssignExpirationDateService)
        return retrofit.create(AssignExpirationDateResources::class.java)
    }

    fun getAssignLotNumberService(): AssignLotNumberResources {
        val retrofit = createRetrofitInstance(ipAddress, portNumber, ServicePaths.AssignLotNumberService)
        return retrofit.create(AssignLotNumberResources::class.java)
    }

    fun getEditItemService(): EditItemServices{
        val retrofit = createRetrofitInstance(ipAddress, portNumber, ServicePaths.EditItemService)
        val editItemServices = object: EditItemServices {
            override val BarcodeService = retrofit.create(EditItemServices.AssignBarcode::class.java)
            override val AssignLotNumberService = retrofit.create(EditItemServices.AssignLotNumberResources::class.java)
            override val AssignExpirationDateService = retrofit.create(EditItemServices.AssignExpirationDateResources::class.java)
        }
        return editItemServices
    }

    fun getInventoryCountService(): InventoryCountServices {
        val retrofit = createRetrofitInstance(ipAddress, portNumber, ServicePaths.InventoryCountService)
        return retrofit.create(InventoryCountServices::class.java)
    }
}
