package i.numan.news.splash_

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import i.numan.news.R
import i.numan.news.api.ConnectivityReceiver
import i.numan.news.ui_.MainActivity
import kotlinx.android.synthetic.main.news_app_splash_screen.*
import kotlinx.coroutines.*

class NewsAppSplashScreen : AppCompatActivity(R.layout.news_app_splash_screen),
    ConnectivityReceiver.ConnectivityReceiverListener {

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        checkingInternetConnectionRepeatedly(isConnected)
    }

    private val SPLASH_DELAY = 3000L
    val coroutine_Splash = CoroutineScope(Dispatchers.Default)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news_app_splash_screen)
        registerReceiver(
            ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }



    override fun onDestroy() {
        super.onDestroy()
        suspend { coroutine_Splash }
        coroutine_Splash.cancel()
    }

    override fun onPause() {
        super.onPause()
        suspend { coroutine_Splash }
        coroutine_Splash.cancel()
    }


    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this@NewsAppSplashScreen
    }

    private fun checkingInternetConnectionRepeatedly(isConnected: Boolean) {
        if (!isConnected) {
            start_progress.isIndeterminate = false
            start_feedback.apply {
                setTextColor(resources.getColor(R.color.colorYellow, null))
                text = getString(R.string.noInternet)
            }
        } else {
            if (networkType()) {
                start_progress.isIndeterminate = true
                start_feedback.apply {
                    setTextColor(resources.getColor(R.color.whiteColor, null))
                    start_feedback.text = getString(R.string.loading)
                }
                coroutine_Splash.launch {
                    delay(SPLASH_DELAY)
                    startActivity(Intent(this@NewsAppSplashScreen, MainActivity::class.java))
                    finish()
                }

            } else {
                start_progress.isIndeterminate = true
                start_feedback.apply {
                    setTextColor(resources.getColor(R.color.whiteColor, null))
                    start_feedback.text = getString(R.string.loading)
                }
                coroutine_Splash.launch {
                    delay(SPLASH_DELAY)
                    startActivity(Intent(this@NewsAppSplashScreen, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun networkType(): Boolean {

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.type == ConnectivityManager.TYPE_WIFI


    }


}
