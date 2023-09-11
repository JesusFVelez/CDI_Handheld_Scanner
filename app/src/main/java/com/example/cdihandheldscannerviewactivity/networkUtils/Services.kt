package com.example.cdihandheldscannerviewactivity.networkUtils
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

private const val BASE_URL = "http://76.72.245.174:8811/HandHeldScannerProject/rest/HandHeldScannerProjectService/"


private val moshi = Moshi.Builder()
                         .add(KotlinJsonAdapterFactory())
                         .build()

private val retrofit = Retrofit.Builder()
//    .addConverterFactory(ScalarsConverterFactory.create()) -> this allows for the response to be converted to a string
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface Services{
    @GET("getCompanies")
    suspend fun getCompanies():
            ResponseWrapper

    @POST("login")
    fun isLogedIn(@Body user: requestUser):
            Call<ResponseWrapperUser>

    @GET("getWarehouses")
    suspend fun getWarehousesAvailable():
            ResponseWrapperWarehouse


    // The @Query annotations here are to specifiy Query Parameters for the API Call
    @GET("getItemsInBin")
    suspend fun getAllItemsInBin(@Query("companyCode") companyCode: String, @Query("warehouseNumber") warehouseNumber: Int, @Query("binLocation") binLocation: String):
            ResponseWrapperProductsInBin

}


data class requestUser(
    val request: User
)
data class User(
    val userName: String,
    val password: String,
    val company: String
)



object ScannerAPI {
    val retrofitService : Services by lazy{
        retrofit.create(Services::class.java)
    }
}