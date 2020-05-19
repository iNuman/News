//package i.numan.news.splash_
//
//import android.content.Context
//import android.content.IntentFilter
//import android.net.ConnectivityManager
//import android.os.Bundle
//import android.view.View
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.Fragment
//import androidx.navigation.fragment.findNavController
//import i.numan.news.R
//import i.numan.news.api.ConnectivityReceiver
//import i.numan.news.ui_.MainActivity
//import kotlinx.coroutines.*
//
//class NewsAppSplashNetworkTest : Fragment(R.layout.news_app_splash_screen),
//    ConnectivityReceiver.ConnectivityReceiverListener {
//
//    override fun onNetworkConnectionChanged(isConnected: Boolean) {
//        showToast(isConnected)
//    }
//
//    private val SPLASH_DELAY = 3000L
//    val coroutine_Splash = CoroutineScope(Dispatchers.Default)
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        context?.registerReceiver(
//            ConnectivityReceiver(),
//            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
//        )
//        startSplash()
//
//    }
//
//    private fun startSplash() {
//
//        coroutine_Splash.launch {
//            delay(SPLASH_DELAY)
//            //findNavController().navigate(R.id.action_newsAppSplashScreen_to_breakingNewsFragment)
////            }
//
////            if (!networkType()){
////                findNavController().navigate(R.id.action_newsAppSplashScreen_to_savedNewsFragment)
////            }else{
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        suspend { coroutine_Splash }
//        coroutine_Splash.cancel()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        println("ffnet: onPause()")
//        suspend { coroutine_Splash }
//        coroutine_Splash.cancel()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        ConnectivityReceiver.connectivityReceiverListener = this@NewsAppSplashNetworkTest
//    }
//
//    private fun showToast(isConnected: Boolean) {
//        if (!isConnected) {
//            Toast.makeText(context, "You are offline now.!!!", Toast.LENGTH_LONG).show()
//        } else {
//            if (networkType()) {
//                Toast.makeText(
//                    context,
//                    "You are online now.!!!" + "\n Connected to Wifi Network",
//                    Toast.LENGTH_LONG
//                ).show()
//            } else {
//                Toast.makeText(
//                    context,
//                    "You are online now.!!!" + "\n Connected to Cellular Network",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
//    }
//
//    private fun networkType(): Boolean {
//        val context = context?.applicationContext
//        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
////        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
////        val isWifi: Boolean = activeNetwork?.type == ConnectivityManager.TYPE_WIFI
//        cm.activeNetworkInfo.also {
//            return it != null && it.isConnected
//        }
//
//    }
//
//
//}
