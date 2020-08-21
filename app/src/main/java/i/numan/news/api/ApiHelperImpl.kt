package i.numan.news.api


import dagger.Provides
import dagger.hilt.android.AndroidEntryPoint
import i.numan.news.dataclass_.NewsResponse
import retrofit2.Response
import javax.inject.Inject


class ApiHelperImpl @Inject constructor(val newsApi: NewsAPI) : ApiHelper {
    override suspend fun getBreakingNews(
        countryCode: String,
        pageNumber: Int
    ): Response<List<NewsResponse>> {
     return newsApi.getBreakingNews(countryCode, pageNumber)
    }

    override suspend fun searchNews(
        countryCode: String,
        pageNumber: Int
    ): Response<List<NewsResponse>> {

        return newsApi.searchNews(countryCode, pageNumber)
    }


}