package i.numan.news.splash_

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import i.numan.news.R
import i.numan.news.ui_.MainActivity
import kotlinx.coroutines.*

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val SPLASH_DELAY = 3000L
    val coroutine_Splash = CoroutineScope(Dispatchers.Default)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).apply {
            findViewById<ConstraintLayout>(R.id.header).visibility = View.INVISIBLE
            findViewById<CoordinatorLayout>(R.id.coordinatorLayout).visibility = View.INVISIBLE
        }
        coroutine_Splash.launch {
            delay(SPLASH_DELAY)
            findNavController().navigate(R.id.action_splashFragment_to_breakingNewsFragment)

        }

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
}