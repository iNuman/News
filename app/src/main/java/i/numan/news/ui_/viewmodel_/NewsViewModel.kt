package i.numan.news.ui_.viewmodel_

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import i.numan.news.R
import i.numan.news.dataclass_.Article
import i.numan.news.dataclass_.NewsResponseDataClass
import i.numan.news.di_.NetworkHelper
import i.numan.news.repository_.NewsRepository
import i.numan.news.util_.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel @ViewModelInject constructor(
    private val application: Application,
    val networkHelper: NetworkHelper,
    val newsRepository: NewsRepository
) : ViewModel() { // Now we're good to go

    var breakingNewsDataClass:MutableLiveData<Resource<NewsResponseDataClass>> = MutableLiveData()
    var breakingNewsPage: Int = 1
    var breakingNewsResponseDataClass: NewsResponseDataClass? = null

    /*
   * Information about above declarations
   * We'll be managing pagination here because if we do this in fragment then the current page
   * Number will always reset when we rotate the device and as we know that view model
   * doesn't get destroyed when we rotate the device screen
    */
    var searchNewsDataClass: MutableLiveData<Resource<NewsResponseDataClass>> = MutableLiveData()
    var searchNewsPage: Int = 1
    var searchingNewsResponseDataClass: NewsResponseDataClass? = null

    init {
        // we call the network call function here
        getBreakingNews(countryCode = "us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode = countryCode)

    }

    fun searchBreakingNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery = searchQuery)
    }

    private fun handleBreakingNewsResponse(responseDataClass: Response<NewsResponseDataClass>): Resource<NewsResponseDataClass>? {
        /*
        * Now we'll decide which state we'll whether we want to emit
        * Success state in our breakingNews Live data or Error State
         */
        if (responseDataClass.isSuccessful) {
            responseDataClass.body()?.let { resultResponse ->
                breakingNewsPage++ // every time we get response we'll increase the page by 1
                if (breakingNewsResponseDataClass == null) { // initially pass the resultResponse to breakingNews Response
                    breakingNewsResponseDataClass = resultResponse
                } else {
                    // if this is not the case then our resultResponse will be add to our already BreakingNewsResponse(old += new Responses)
                    val oldArticle = breakingNewsResponseDataClass?.articles
                    val newArticles = resultResponse.articles
                    oldArticle?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponseDataClass ?: resultResponse)
            }
        }
        return Resource.Error(responseDataClass.message())
    }

    private fun handleSearchNewsResponse(responseDataClass: Response<NewsResponseDataClass>): Resource<NewsResponseDataClass>? {

        if (responseDataClass.isSuccessful) {
            responseDataClass.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchingNewsResponseDataClass == null) {
                    searchingNewsResponseDataClass = resultResponse
                } else {
                    val oldSearchResponse = breakingNewsResponseDataClass?.articles
                    val newSearchResponse = resultResponse.articles
                    oldSearchResponse?.addAll(newSearchResponse)
                }

                return Resource.Success(searchingNewsResponseDataClass ?: resultResponse)
            }
        }
        return Resource.Error(responseDataClass.message())
    }

    fun saveNews(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article = article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteNews(news = article)
    }

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        breakingNewsDataClass.postValue(Resource.Loading())
        try {
            if (networkHelper.hasInternetConnection()) {
                // now we'll make actual response
                val response =
                    newsRepository.getBreakingNews(
                        countryCode = countryCode,
                        pageNumber = breakingNewsPage
                    )
                breakingNewsDataClass.postValue(handleBreakingNewsResponse(responseDataClass = response))
            } else {
                breakingNewsDataClass.postValue(
                    Resource.Error(
                        message = application.getString(R.string.networkFailure)
                    )
                )
            }

        } catch (t: Throwable) {
            when (t) {
                is IOException -> breakingNewsDataClass.postValue(
                    Resource.Error(message = application.getString(R.string.networkFailure)))
                    else -> breakingNewsDataClass.postValue(
                    Resource.Error(message = application.getString(R.string.conversionFailure))                )
            }

        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNewsDataClass.postValue(Resource.Loading())
        try {
            if (networkHelper.hasInternetConnection()) {
                val response =
                    newsRepository.searchNews(
                        countryCode = searchQuery,
                        pageNumber = searchNewsPage
                    )
                searchNewsDataClass.postValue(handleSearchNewsResponse(responseDataClass = response))
            } else {
                searchNewsDataClass.postValue(
                    Resource.Error(
                        message = application.getString(R.string.noInternet))
                )
            }

        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNewsDataClass.postValue(
                    Resource.Error(message = application.getString(R.string.networkFailure)
                    )
                )
                else -> searchNewsDataClass.postValue(
                    Resource.Error(message = application.getString(R.string.conversionFailure)
                    )
                )
            }

        }
    }


}