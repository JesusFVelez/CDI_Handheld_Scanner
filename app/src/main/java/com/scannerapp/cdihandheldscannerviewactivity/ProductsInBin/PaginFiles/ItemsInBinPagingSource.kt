package com.scannerapp.cdihandheldscannerviewactivity.ProductsInBin.PaginFiles

import android.util.Log
import androidx.annotation.RestrictTo
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ProductInBinInfo
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

private const val STARTING_KEY = 0
private const val LOAD_DELAY_MILLIS = 3_000L

class ItemsInBinPagingSource : PagingSource<Int, ProductInBinInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductInBinInfo> {
        val startKey = params.key ?: STARTING_KEY
        if (startKey != STARTING_KEY) delay(LOAD_DELAY_MILLIS)

        var 
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _wasLastAPICallSuccessful.value = false
            Log.i("Products In Bin View Model Product Info API Call" , "Error -> ${exception.message}")
        }
        try {
            GlobalScope.launch {
                val response = ScannerAPI.getViewProductsInBinService().getPaginatedItemsInBin(_companyIDOfUser.value!!,
                    _warehouseNumberOfUser.value!!,
                    binNumber,
                    pageSize = pageSize,
                    pageNumber = pageNumber )
            }
        }catch (e: Exception){
            Log.i("", "Error -> ${e.message}")
        }
        return LoadResult.Page(
            data =
            },
            prevKey = when (startKey) {
                STARTING_KEY -> null
                else -> when (val prevKey = ensureValidKey(key = range.first - params.loadSize)) {
                    // We're at the start, there's nothing more to load
                    STARTING_KEY -> null
                    else -> prevKey
                }
            },
            nextKey = range.last + 1)
//            data = range.map { number ->
//                Article(
//                    id = number,
//                    title = "Article $number",
//                    description = "This describes article $number",
//                    created = firstArticleCreatedTime.minusDays(number.toLong())
//                )
//            },
//            prevKey = when (startKey) {
//                STARTING_KEY -> null
//                else -> when (val prevKey = ensureValidKey(key = range.first - params.loadSize)) {
//                    // We're at the start, there's nothing more to load
//                    STARTING_KEY -> null
//                    else -> prevKey
//                }
//            },
//            nextKey = range.last + 1
//        )
    }
    override fun getRefreshKey(state: PagingState<Int, ProductInBinInfo>): Int? {
        TODO("Not yet implemented")
    }

    private fun ensureValidKey(key: Int) = max(STARTING_KEY, key)
}