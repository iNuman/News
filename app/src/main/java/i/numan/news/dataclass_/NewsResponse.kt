package i.numan.news.dataclass_

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)