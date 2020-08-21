package i.numan.news.db_

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import i.numan.news.R

import i.numan.news.dataclass_.Article

@Database(
    entities = [Article::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(Converters::class) // added the converter here
abstract class AppDatabase : RoomDatabase() {

    // we'll make a function on lhs for each interface and interface define on rhs
    abstract fun getArticleDao(): ArticlesDao
}
