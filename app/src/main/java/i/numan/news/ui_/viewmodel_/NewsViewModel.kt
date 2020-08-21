package i.numan.news.ui_.viewmodel_

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import i.numan.news.R
import i.numan.news.dataclass_.Article
import i.numan.news.dataclass_.NewsResponse
import i.numan.news.di_.NetworkHelper
import i.numan.news.repository_.NewsRepository
import i.numan.news.util_.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel @ViewModelInject constructor(
    val application: Application,
    val networkHelper: NetworkHelper,
    val newsRepository: NewsRepository
/*
* Now to use ApplicationContext we we'll be using
* Android View Model instead of view model
*
 */
) : ViewModel() { // Now we're good to go

    var breakingNews:MutableLiveData<Resource<List<NewsResponse>>> = MutableLiveData()
    var breakingNewsPage: Int = 1
    var breakingNewsResponse: List<NewsResponse>? = null

    /*
   * Information about above declarations
   * We'll be managing pagination here because if we do this in fragment then the current page
   * Number will always reset when we rotate the device and as we know that view model
   * doesn't get destroyed when we rotate the device screen
    */
    var searchNews: MutableLiveData<Resource<List<NewsResponse>>> = MutableLiveData()
    var searchNewsPage: Int = 1
    var searchingNewsResponse: List<NewsResponse>? = null

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

    private fun handleBreakingNewsResponse(response: Response<List<NewsResponse>>): Resource<List<NewsResponse>>? {
        /*
        * Now we'll decide which state we'll whether we want to emit
        * Success state in our breakingNews Live data or Error State
         */
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++ // every time we get response we'll increase the page by 1
                if (breakingNewsResponse == null) { // initially pass the resultResponse to breakingNews Response
                    breakingNewsResponse = resultResponse
                } else {
                    // if this is not the case then our resultResponse will be add to our already BreakingNewsResponse(old += new Responses)
                    val oldArticle = breakingNewsResponse!![0].articles
                    val newArticles = resultResponse[0].articles
                    oldArticle?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<List<NewsResponse>>): Resource<List<NewsResponse>>? {

        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchingNewsResponse == null) {
                    searchingNewsResponse = resultResponse
                } else {
                    val oldSearchResponse = breakingNewsResponse!![0].articles
                    val newSearchResponse = resultResponse[0].articles
                    oldSearchResponse?.addAll(newSearchResponse)
                }

                return Resource.Success(searchingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveNews(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article = article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteNews(news = article)
    }

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            if (networkHelper.hasInternetConnection()) {
                // now we'll make actual response
                val response =
                    newsRepository.getBreakingNews(
                        countryCode = countryCode,
                        pageNumber = breakingNewsPage
                    )
                breakingNews.postValue(handleBreakingNewsResponse(response = response))
            } else {
                breakingNews.postValue(
                    Resource.Error(
                        message = application.getString(
                            R.string.networkFailure
                        )
                    )
                )
            }

        } catch (t: Throwable) {
            when (t) {
                is IOException -> breakingNews.postValue(
                    Resource.Error(message = application.getString(R.string.networkFailure)))
                    else -> breakingNews.postValue(
                    Resource.Error(message = application.getString(
                            R.string.conversionFailure
                        ))
                )
            }

        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNews.postValue(Resource.Loading())
        try {
            if (networkHelper.hasInternetConnection()) {
                val response =
                    newsRepository.searchNews(
                        countryCode = searchQuery,
                        pageNumber = searchNewsPage
                    )
                searchNews.postValue(handleSearchNewsResponse(response = response))
            } else {
                searchNews.postValue(
                    Resource.Error(
                        message = application.getString(
                            R.string.noInternet
                        )
                    )
                )
            }

        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(
                    Resource.Error(
                        message = application.getString(
                            R.string.networkFailure
                        )
                    )
                )
                else -> searchNews.postValue(
                    Resource.Error(
                        message = application.getString(
                            R.string.conversionFailure
                        )
                    )
                )
            }

        }
    }


}