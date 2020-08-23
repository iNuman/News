package i.numan.news.dataclass_

data class NewsResponseDataClass(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)