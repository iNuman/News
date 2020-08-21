package i.numan.news.api


import dagger.Provides
import i.numan.news.dataclass_.NewsResponse
import retrofit2.Response
import javax.inject.Inject


interface ApiHelper{

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int): Response<List<NewsResponse>>
    suspend fun searchNews(countryCode: String, pageNumber: Int): Response<List<NewsResponse>>
}