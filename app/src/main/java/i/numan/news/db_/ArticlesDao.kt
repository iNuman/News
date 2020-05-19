package i.numan.news.db_

import androidx.lifecycle.LiveData
import androidx.room.*
import i.numan.news.dataclass_.Article

@Dao
interface ArticlesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    @Query("Select *from articles")
    fun getAllArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}