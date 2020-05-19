package i.numan.news.util_

/*
* This class will be wrap around network responses as recommended by google
* this will be a generic class
* And it's very useful to differentiate between success and error messages
* And also helps us the loading state
*
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {

    class Success<T>(data: T) : Resource<T>(data = data)
    class Error<T>(message: String?, data: T? = null) : Resource<T>(data = data, message = message)
    // this will run after our response comes
    class Loading<T>: Resource<T>()
}