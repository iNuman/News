package i.numan.news.repository_

import i.numan.news.api.ApiHelper
import i.numan.news.api.RetrofitInstance
import i.numan.news.dataclass_.Article
import i.numan.news.db_.AppDatabase
import javax.inject.Inject

class NewsRepository @Inject constructor(
    val db: AppDatabase,
    val apiHelper: ApiHelper
) {


    /*
    * Adding and deleting data to room
     */
    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    // this will be a normal function as it returns live data
    fun getSavedNews() = db.getArticleDao().getAllArticles()
    suspend fun deleteNews(news: Article) = db.getArticleDao().deleteArticle(news)

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
     /*   RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)*/
       apiHelper.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(countryCode: String, pageNumber: Int) =
        /*RetrofitInstance.api.searchNews(countryCode, pageNumber)*/
        apiHelper.searchNews(countryCode, pageNumber)

}