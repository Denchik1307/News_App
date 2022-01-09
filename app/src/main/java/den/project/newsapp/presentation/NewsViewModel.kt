package den.project.newsapp.presentation

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import den.project.newsapp.NewsApp
import den.project.newsapp.models.Article
import den.project.newsapp.models.NewsResponse
import den.project.newsapp.repository.NewsRepository
import den.project.newsapp.utils.Constants.Companion.COUNTRY
import den.project.newsapp.utils.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class NewsViewModel(
    app: Application,
    private val newsRepository: NewsRepository
) : AndroidViewModel(app) {

    val allNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var allNewsPage = 1
    private var allNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    private var searchNewsResponse: NewsResponse? = null

    init {
        getAllNews(COUNTRY)
    }

    fun getAllNews(countryCode: String) = viewModelScope.launch {
        safeAllNewsCall(countryCode)
    }

    fun searchInAllNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    private fun handleAllNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                allNewsPage++
                if (allNewsResponse == null) {
                    allNewsResponse = resultResponse
                } else {
                    val oldNews = allNewsResponse?.articles
                    val newNews = resultResponse.articles
                    oldNews?.addAll(newNews)
                }
                return Resource.Success(allNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                allNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldNews = searchNewsResponse?.articles
                    val newNews = resultResponse.articles
                    oldNews?.addAll(newNews)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveNews(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteNews(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }



    private suspend fun safeAllNewsCall(countryCode: String) {
        allNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getAllNewsRep(countryCode, allNewsPage)
                allNews.postValue(handleAllNewsResponse(response))
            } else {
                allNews.postValue(Resource.Error("No ethernet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> allNews.postValue(Resource.Error("Network failure"))
                else -> allNews.postValue(Resource.Error("Error conversion"))
            }
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.searchNewsRep(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error("No ethernet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(Resource.Error("Network failure"))
                else -> searchNews.postValue(Resource.Error("Error conversion"))
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectionManager = getApplication<NewsApp>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectionManager.activeNetwork ?: return false
        val capabilities = connectionManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(TRANSPORT_WIFI) -> true
            capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
            else -> false
        }
        return false
    }

}