package i.numan.news.db_

import androidx.room.TypeConverter
import i.numan.news.dataclass_.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }

    /*
    * And finally we'll add them in our database class
     */
}
