package i.numan.news.ui_.viewmodel_

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import i.numan.news.dataclass_.Article
import i.numan.news.dataclass_.NewsResponse
import i.numan.news.repository_.NewsRepository
import i.numan.news.util_.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    val newsRepository: NewsRepository
/*
* Since we can't use constructor view model by default in out view model
* but if we want we will have to make view model provider class
* to define how our own view model should be created
 */
) : ViewModel() {
    /*
    * Now here we'll make mutable live  data
    * of News response which will be
    * wrapped by out generic resource class
     */
    var breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage: Int = 1
    var breakingNewsResponse: NewsResponse? = null

    /*
   * Information about above declarations
   * We'll be managing pagination here because if we do this in fragment then the current page
   * Number will always reset when we rotate the device and as we know that view model
   * doesn't get destroyed when we rotate the device screen
    */
    var searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage : Int = 1
    var searchingNewsResponse: NewsResponse? = null

    init {
        // we call the network call function here
        getBreakingNews(countryCode = "us")
    }

     fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        // So now here before making actual network call we'll load the state from our
        // mutable live data above as we now know we're ready to make network call so we should
        // emit the loading state
        breakingNews.postValue(Resource.Loading())
        // now we'll make actual response
        val response =
            newsRepository.getBreakingNews(countryCode = countryCode, pageNumber = breakingNewsPage)

        /*
        * Now that response is saved
        * Now i'll handle this response
        * and pagination later
        * for this i'll make a private function below
         */

        // post the response either success or error and our fragment will auto get notified about the change
        breakingNews.postValue(handleBreakingNewsResponse(response = response))
    }

    fun searchBreakingNews(searchQuery: String) = viewModelScope.launch {

        searchNews.postValue(Resource.Loading())
        val response =
            newsRepository.searchNews(countryCode = searchQuery, pageNumber = searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response = response))
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
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
                    val oldArticle = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticle?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {

        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchingNewsResponse == null) {
                    searchingNewsResponse = resultResponse
                } else {
                    val oldSearchResponse = breakingNewsResponse?.articles
                    val newSearchResponse = resultResponse.articles
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

}