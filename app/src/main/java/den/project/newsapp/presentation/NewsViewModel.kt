package den.project.newsapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import den.project.newsapp.models.NewsResponse
import den.project.newsapp.repository.NewsRepository
import den.project.newsapp.utils.Constants.Companion.COUNTRY
import den.project.newsapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    private val newsRepository: NewsRepository
) : ViewModel() {

    val allNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var allNewsPage = 1

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var searchNewsPage = 1

    init {
        getAllNews(COUNTRY)
    }

    private fun getAllNews(countryCode: String) = viewModelScope.launch {
        allNews.postValue(Resource.Loading())
        val response = newsRepository.getAllNewsRep(countryCode, allNewsPage)
        allNews.postValue(handleAllNewsResponse(response))
    }

    fun searchInAllNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response = newsRepository.searchNewsRep(searchQuery, searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
    }

    private fun handleAllNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}