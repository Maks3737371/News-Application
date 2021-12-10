package com.maks.newsapp.repository

import com.maks.newsapp.response.NewsResponse
import com.maks.newsapp.webapi.ApiService
import retrofit2.Response
import javax.inject.Inject

class NewsRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getNews() = apiService.getNews()

    suspend fun getSearchNews(query: String, page: Int): Response<NewsResponse> {
        return apiService.getSearchNews(searchQuery = query, page = page)
    }
}