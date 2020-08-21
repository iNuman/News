package i.numan.news.ui_.fragments_

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import i.numan.news.R
import i.numan.news.dataclass_.Article
import i.numan.news.ui_.MainActivity
import i.numan.news.ui_.viewmodel_.NewsViewModel
import kotlinx.android.synthetic.main.fragment_article.*


@AndroidEntryPoint
class ArticleFragment : Fragment(R.layout.fragment_article) {

    // TODO: FADE IN ANIMATION TO FAB BUTTON FILL AFTER FROM MATERIAL DESIGN IMPLEMENTATION OF MINE
   val viewModel: NewsViewModel by viewModels()
    val args: ArticleFragmentArgs by navArgs()
    private var isDataLoaded = false

    lateinit var openFabAnimation: Animation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handlingToolbar()
        openFabAnimation = AnimationUtils.loadAnimation(context, R.anim.fab_open)
        val article = args.article
        loadingDataInWebView(article)
        handlingFabButton(view, article)
    }

    private fun handlingFabButton(view: View, article: Article) {
        fab.animation = openFabAnimation
        fab.setOnClickListener {
            viewModel.saveNews(article)
            fab.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF0D2F"))
            Snackbar.make(view, "Article saved successfully", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun handlingToolbar() {
        val v = activity?.findViewById<ConstraintLayout>(R.id.header)
        v?.visibility = View.GONE
    }

    private fun loadingDataInWebView(article: Article) {
        webView.apply {
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(webView: WebView, article: String?): Boolean {
                    loadUrl(article)
                    isDataLoaded = true
                    return false
                }

                override fun onPageStarted(
                    view: WebView?,
                    url: String?,
                    favicon: Bitmap?
                ) {
                    super.onPageStarted(view, url, favicon)
                    isDataLoaded = false
                    showProgressBar(progress = progress)

                }

                override fun onLoadResource(view: WebView?, url: String?) {
                    if (!isDataLoaded) showProgressBar(progress)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    if (progress == 100) {
                        isDataLoaded = true
                        hideProgressBar()
                    }

                }
            }
            if (progress == 100) {
                isDataLoaded = true
                hideProgressBar()
                loadUrl(article.url)
            }


        }
    }

    private fun hideProgressBar() {
        webViewProgressbar.visibility = View.INVISIBLE
    }

    private fun showProgressBar(progress: Int) {
        webViewProgressbar.apply {
            visibility = View.VISIBLE
            setProgress(progress)
        }
    }
}
