package com.velmurugan.paging3android.Adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import java.lang.Exception

class CustomPagingSource<T : Any>(private val loadData: suspend (Int) -> List<T>) : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val position = params.key ?: 1
            val data = loadData(position)
            LoadResult.Page(
                data = data,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (data.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition
    }
}
