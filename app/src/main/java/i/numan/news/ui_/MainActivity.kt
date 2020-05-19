package i.numan.news.ui_

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import i.numan.news.R
import i.numan.news.api.ConnectivityReceiver
import i.numan.news.db_.AppDatabase
import i.numan.news.repository_.NewsRepository
import i.numan.news.ui_.viewmodel_.NewsViewModeProviderFactory
import i.numan.news.ui_.viewmodel_.NewsViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){

    lateinit var newsViewModel: NewsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView.setupWithNavController(nav_host_fragment.findNavController())

        val newsRepository = NewsRepository(db = AppDatabase(this))
        val newsViewModeProviderFactory =
            NewsViewModeProviderFactory(newsRepository = newsRepository)
        newsViewModel =
            ViewModelProvider(this, newsViewModeProviderFactory).get(NewsViewModel::class.java)


    }


}
