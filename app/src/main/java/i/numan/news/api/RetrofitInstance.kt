package i.numan.news.api

import i.numan.news.util_.Constants.Companion.Base_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        /*
        * Here lazy means
        * we'll only initialize any thing we put inside lazy's
        * curly braces
        *
        * Another thing is that I had added is logging interceptor library
        * which is useful for debugging purpose
        * we'll attach this interceptor to our retrofit object to be able to see
        * what requests we're making and what the responses are
         */
        private val retrofit by lazy {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)// means we'll actually see body response

            /*
            * We can use that interceptor to create network client
             */
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
            Retrofit.Builder()
                .baseUrl(Base_URL)
                .addConverterFactory(GsonConverterFactory.create()) // this to show how our response should be interpreted and converted to kotlin objects
                .client(client)
                .build()
        }

        val api by lazy {
            retrofit.create(NewsAPI::class.java)
        }
    }
}