package i.numan.news.di_

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import i.numan.news.BuildConfig
import i.numan.news.R
import i.numan.news.api.ApiHelperInterface
import i.numan.news.api.ApiHelperInterfaceImpl
import i.numan.news.api.NewsAPI
import i.numan.news.db_.AppDatabase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideBaseUrl() = BuildConfig.BASE_URL

    @Provides
    @Singleton
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    } else OkHttpClient
        .Builder()
        .build()


    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        BASE_URL: String
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    /*
    * Avoid typing your data class here instead this method will return our response
    * */
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): NewsAPI = retrofit.create(NewsAPI::class.java)

    @Provides
    fun provideApiHelper(apiHelper: ApiHelperInterfaceImpl): ApiHelperInterface = apiHelper

/*
        * If we leave this function like this and whenever we call this function let say
        * we need this function in two classes
        * then there'll be two different instances of database
        * but we want it to be singleton/same instance in both classes
        * So, for this purpose we we'll be annotating it with @Singleton
        * Then instance of this function/db will be singleton application wise
        *
        *
        * Generally we don't need to access this database class instead
        * We need to access DAO Class for this we'll make another method down below
        * */
    @Singleton
    @Provides
    fun provideRunningDatabase(
        @ApplicationContext app: Context
        /* since dagger don't know where this context come from so we'll annotate it with @App....
         * and this it the beauty of dagger hilt library because there's other things
         * to handle except only writing this @App.. for context */
    ): AppDatabase = Room.databaseBuilder(
        app,
        AppDatabase::class.java,
        app.getString(R.string.databaseName)
    ).build()


    @Singleton
    @Provides
    fun provideRunDao(db: AppDatabase) = db.getArticleDao()


}