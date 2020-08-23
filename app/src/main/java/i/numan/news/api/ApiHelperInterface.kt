package i.numan.news.api


import i.numan.news.dataclass_.NewsResponseDataClass
import retrofit2.Response


interface ApiHelperInterface{

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int): Response<NewsResponseDataClass>
    suspend fun searchNews(countryCode: String, pageNumber: Int): Response<NewsResponseDataClass>
}