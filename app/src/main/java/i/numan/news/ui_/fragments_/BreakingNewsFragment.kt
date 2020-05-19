package i.numan.news.ui_.fragments_

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import i.numan.news.R
import i.numan.news.adapters_.NewsRecyclerViewAdapter
import i.numan.news.api.ConnectivityReceiver
import i.numan.news.ui_.MainActivity
import i.numan.news.ui_.viewmodel_.NewsViewModel
import i.numan.news.util_.Constants.Companion.QUERY_PAGE_SIZE
import i.numan.news.util_.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*


class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news){
    val TAG = "BreakingNewsFragment"
    lateinit var viewModel: NewsViewModel
    lateinit var newsRecyclerViewAdapter: NewsRecyclerViewAdapter

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    lateinit var openFabAnimation: Animation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingToolbar()
        viewModel = (activity as MainActivity).newsViewModel
        setupRecyclerView()

        handlingViewModelObserver()

    }

    private fun handlingViewModelObserver() {
        viewModel.breakingNews.observe(
            viewLifecycleOwner,
            Observer { response -> // breaking will be having either success or fail
                // response from view model
                when (response) {
                    // if we got success response
                    is Resource.Success -> {
                        hideProgressBar()
                        // check response if null or not to set it to recycler view adapter
                        response.data?.let { newsResponse ->
                            newsRecyclerViewAdapter.submitList(newsResponse.articles.toList())
                            // because list differ is not working properly with mutableList so I changed it to list again
                            val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                            isLastPage = viewModel.breakingNewsPage == totalPages
                            if (isLastPage){
                                pagination_progressbar.setPadding(0,0,0,0)
                            }
                        }
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        response.message?.let { message ->
                            Log.e(TAG, "Error Occurred Getting Data $message")
                        }
                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }

                }


            })
    }

    private fun settingToolbar() {
        openFabAnimation = AnimationUtils.loadAnimation(
            context,
            R.anim.fab_open
        )
        val v = activity?.findViewById<ConstraintLayout>(R.id.header)
        v?.animation = openFabAnimation
        v?.visibility = View.VISIBLE
        val label = activity?.findViewById<TextView>(R.id.lbl2)
        label?.text = "Breaking News"
    }

    private fun hideProgressBar() {
        pagination_progressbar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        pagination_progressbar.visibility = View.VISIBLE
        isLoading = true
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition =
                layoutManager.findFirstVisibleItemPosition() // during scrolling which item is first visible
            val visibleItemCount =
                layoutManager.childCount // how many items currently visible is recycler view
            val totalItemCount = layoutManager.itemCount // total no.of item counts

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible =
                totalItemCount >= QUERY_PAGE_SIZE // my response contains 20 Articles at a time

            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.getBreakingNews(countryCode = "us")
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setupRecyclerView() {
        newsRecyclerViewAdapter = NewsRecyclerViewAdapter()
        recycler_view.apply {
            adapter = newsRecyclerViewAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }

        newsRecyclerViewAdapter.setOnItemClickListener { article ->
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }
    }



}
