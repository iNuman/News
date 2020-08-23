package i.numan.news.ui_.viewmodel_

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import i.numan.news.application_.NewsApplication
import i.numan.news.di_.NetworkHelper
import i.numan.news.repository_.NewsRepository

class NewsViewModeProviderFactory (
    val application: NewsApplication,
    val networkHelper: NetworkHelper,
    val newsRepository: NewsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(
            application = application,
            newsRepository = newsRepository,
            networkHelper = networkHelper
        ) as T
    }
}
