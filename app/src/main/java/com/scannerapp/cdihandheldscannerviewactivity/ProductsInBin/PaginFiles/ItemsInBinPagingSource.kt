package com.scannerapp.cdihandheldscannerviewactivity.ProductsInBin.PaginFiles

import android.util.Log
import androidx.annotation.IntegerRes
import androidx.annotation.RestrictTo
import androidx.compose.foundation.pager.PageSize
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ProductInBinInfo
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ResponseWrapperProductsInBin
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

private const val STARTING_KEY = 0
private const val LOAD_DELAY_MILLIS = 3_000L

class ItemsInBinPagingSource : PagingSource<Int, ProductInBinInfo>(    ) {
    private lateinit var response: ResponseWrapperProductsInBin
    private lateinit var companyID: String
    private lateinit var warehouseNumber: Number
    private lateinit var binNumber: String

    suspend fun getPaginatedItemsInBin (pageNumber:Int, pageSize: Int): List<ProductInBinInfo> {
        var listOfItemsInPage: List<ProductInBinInfo>
        try {
            response = ScannerAPI.getViewProductsInBinService().getPaginatedItemsInBin(
                companyID,
                warehouseNumber.toInt(),
                binNumber,
                pageSize = pageSize,
                pageNumber = pageNumber
            )
            listOfItemsInPage = response.response.itemsInBin.itemsInBin
        }catch(e: Exception){
            listOfItemsInPage = emptyList()
            Log.i("Get Items In Bin Paging", "Error -> ${e.message}")
        }

        return listOfItemsInPage

    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductInBinInfo> {

        val currentPage = params.key ?: STARTING_KEY
        if (currentPage != STARTING_KEY) delay(LOAD_DELAY_MILLIS)
        val listOfItems = getPaginatedItemsInBin(pageNumber = currentPage, pageSize = params.loadSize)
        // Determine the next page number, or set to null if there are no more pages
        val nextPageNumber = if (listOfItems.isEmpty()) null else currentPage + 1
        // Determine the previous page number, or set to null if we're on the first page
        val prevPageNumber = if (currentPage == STARTING_KEY) null else currentPage - 1
        return LoadResult.Page( data = listOfItems,
                                prevKey = prevPageNumber,
                                nextKey = nextPageNumber)
    }
    override fun getRefreshKey(state: PagingState<Int, ProductInBinInfo>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val closestPage = state.closestPageToPosition(anchorPosition) ?: return null

        val prevKey = closestPage.prevKey
        val nextKey = closestPage.nextKey

        return when {
            prevKey != null -> prevKey + 1
            nextKey != null -> nextKey - 1
            else -> null
        }
    }

    //private fun ensureValidKey(key: Int) = max(STARTING_KEY, key)
}