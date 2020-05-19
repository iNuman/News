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


    // companion object to make the method getAppDataBase accessible in other classes
    // simply say static
    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()  // it'll make sure no two threads are working at the
        // same task

        operator fun invoke(context: Context) =
            instance ?: /*
                         * if db instance is not null it'll instantiate else
                         */
            synchronized(LOCK) { /*
                                     * synchronized will make sure that this will not access
                                     * by multiple threads at the same time
                                      */


                instance ?: // will again check for null and initialize else will initialize db
                buildDatabase(context).also { instance = it }
                // whatever return from the database will be assigned to instance of db
            }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            context.getString(R.string.databaseName)
        ).build()
    }

}
