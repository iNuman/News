package i.numan.news.api

import i.numan.news.dataclass_.NewsResponseDataClass
import i.numan.news.util_.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


/*
* This is the first thing we should make when working with apis
* after this we'll create singleton class inside api to be able to call the retrofit callback
* anywhere in our app
 */
interface NewsAPI {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        /*
       * I want to be able to specify the country from which i want to get the news
        */
        @Query("country")
        countryCode: String = "us",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY

        /* page for pagination purpose we'll get 20 articles at a time and
        * if we want more  we'll again request the page
        * i set the page number to 1 initially
         */

    ):Response<NewsResponseDataClass>

    /*
    * Now for searching
     */
    @GET("v2/everything")
    suspend fun searchNews(
        /*
       * I want to be able to specify the country from which i want to get the news
        */
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ):Response<NewsResponseDataClass>



}