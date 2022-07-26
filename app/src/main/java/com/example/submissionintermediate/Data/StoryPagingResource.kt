package com.example.submissionintermediate.Data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.submissionintermediate.Api.ApiService
import java.lang.Exception

class StoryPagingResource(private val apiService:ApiService, private val token:String): PagingSource<Int, Story>() {


    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?:anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val token = "Bearer $token"
            val responseData = apiService.getStoryItem(token, position, params.loadSize).listStory

            LoadResult.Page(
                data = responseData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.isNullOrEmpty()) null else position + 1
            )
        } catch (e: Exception){
            return LoadResult.Error(e)
        }
    }


    private companion object{
        const val INITIAL_PAGE_INDEX = 1
    }

}