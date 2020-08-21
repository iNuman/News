package i.numan.news.ui_.fragments_

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import i.numan.news.R
import i.numan.news.adapters_.NewsRecyclerViewAdapter
import i.numan.news.ui_.MainActivity
import i.numan.news.ui_.viewmodel_.NewsViewModel
import i.numan.news.util_.Constants
import i.numan.news.util_.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import i.numan.news.util_.Resource
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/*
* Since our article data class is not primitive data type like
* int or float so we'll mark the class as serializable
* which means that we're telling kotlin that we want to pass the article
* class trough some fragments using navigation args
* and kotlin will do serialization for us
 */
@AndroidEntryPoint
class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    private val TAG = "SearchNewsFragment"
    val viewModel: NewsViewModel by viewModels()
    lateinit var newsRecyclerViewAdapter: NewsRecyclerViewAdapter

    var isLoading = false
    var isLastPage = false
    var isScrolling = false
    lateinit var openFabAnimation: Animation
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingToolbarAndBottomNavigation()

        setupRecyclerView()
        settingTextChangeListener()
        settingViewLifeCycleOwner()


    }

    private fun settingViewLifeCycleOwner() {
        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { data ->
                        newsRecyclerViewAdapter.submitList(data.articles.toList())
                        val totalPages = data.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages

                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { errorMessage ->
                        findNavController().navigate(R.id.action_searchNewsFragment_to_savedNewsFragment)
                        Toast.makeText(context, "$errorMessage", Toast.LENGTH_LONG).show()

                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }

            }
        })

    }

    private fun settingTextChangeListener() {
        var job: Job? = null
        etSearch.addTextChangedListener { editable ->
            job?.cancel() // if job not cancelled
            job = MainScope().launch { // add a little delay to make request
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchBreakingNews(editable.toString())
                    }
                }


            }
        }
    }

    private fun settingToolbarAndBottomNavigation() {
        openFabAnimation = AnimationUtils.loadAnimation(
            context,
            R.anim.fab_open
        )

        val label = activity?.findViewById<TextView>(R.id.lbl2)
        label?.text = "Search Articles"
        (activity as MainActivity).apply {
            findViewById<ConstraintLayout>(R.id.header).animation = openFabAnimation
            findViewById<CoordinatorLayout>(R.id.coordinatorLayout).animation = openFabAnimation
        }

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
                totalItemCount >= Constants.QUERY_PAGE_SIZE // my response contains 20 Articles at a time

            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.searchBreakingNews(etSearch.text.toString())
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
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
        newsRecyclerViewAdapter.setOnItemClickListener { article ->
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }
    }

}
