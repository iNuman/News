package i.numan.news.api


import i.numan.news.dataclass_.NewsResponseDataClass
import retrofit2.Response
import javax.inject.Inject


class ApiHelperInterfaceImpl @Inject constructor(val newsApi: NewsAPI) : ApiHelperInterface {

    override suspend fun getBreakingNews(
        countryCode: String,
        pageNumber: Int
    ): Response<NewsResponseDataClass> {
        val breakingNews_ = newsApi.getBreakingNews(countryCode, pageNumber)
        return breakingNews_
    }

    override suspend fun searchNews(
        countryCode: String,
        pageNumber: Int
    ): Response<NewsResponseDataClass> {
        val searchNews_ = newsApi.searchNews(countryCode, pageNumber)
        return searchNews_
    }


}