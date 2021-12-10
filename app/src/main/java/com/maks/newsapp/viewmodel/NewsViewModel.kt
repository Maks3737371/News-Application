package com.maks.newsapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.maks.newsapp.app.NewsApp
import com.maks.newsapp.repository.NewsRepository
import com.maks.newsapp.response.NewsResponse
import com.maks.newsapp.storage.UIModeDataStore
import com.maks.newsapp.utils.Resource
import com.maks.newsapp.utils.hasInternetConnection
import com.maks.newsapp.utils.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    application: Application,
    private val repository: NewsRepository
    ): AndroidViewModel(application) {

    private val uiModeDataStore = UIModeDataStore(application)

    val getUIMode = uiModeDataStore.uiMode

    fun saveToDataStore(isNightMode: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            uiModeDataStore.saveToDataStore(isNightMode)
        }
    }

    val newsData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    private val newsDataTemp = MutableLiveData<Resource<NewsResponse>>()

    var searchNewsPage = 1
    private var searchNewsResponse: NewsResponse? = null

    init { getNews() }

    fun getNews() = viewModelScope.launch { fetchNews() }

    fun getSearchNews(searchQuery: String) = viewModelScope.launch { fetchsearchnews(searchQuery) }

    private suspend fun fetchNews() {
        newsData.postValue(Resource.Loading())
        try {
            if (hasInternetConnection<NewsApp>()) {
                val response = repository.getNews()
                newsDataTemp.postValue(Resource.Success(response.body()!!))
                newsData.postValue(handleNewsResponse(response))
            } else {
                newsData.postValue(Resource.Error("No Internet Connection\n\n\nSwipe down to refresh"))
                toast(getApplication(), "No Internet Connection!")
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> newsData.postValue(Resource.Error(t.message!!))
                else -> newsData.postValue(Resource.Error(t.message!!))
            }
        }
    }

    private suspend fun fetchsearchnews(searchQuery: String) {
        newsData.postValue(Resource.Loading())
        try {
            if (hasInternetConnection<NewsApp>()) {
                val response = repository.getSearchNews(searchQuery, searchNewsPage)
                newsData.postValue(handleSearchNewsResponse(response))
            } else {
                newsData.postValue(Resource.Error("No Internet Connection"))
                toast(getApplication(), "No Internet Connection!")
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> newsData.postValue(Resource.Error(t.message!!))
                else -> newsData.postValue(Resource.Error(t.message!!))
            }
        }
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles

                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse -> return Resource.Success(resultResponse) }
        }
        return Resource.Error(response.message())
    }
}