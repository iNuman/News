package i.numan.news.ui_.fragments_

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import i.numan.news.R
import i.numan.news.adapters_.NewsRecyclerViewAdapter
import i.numan.news.dataclass_.Article
import i.numan.news.ui_.MainActivity
import i.numan.news.ui_.viewmodel_.NewsViewModel
import kotlinx.android.synthetic.main.fragment_saved_news.*
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.android.synthetic.main.fragment_search_news.recycler_view

@AndroidEntryPoint
class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {

    val viewModel: NewsViewModel by viewModels()
    lateinit var newsRecyclerViewAdapter: NewsRecyclerViewAdapter

    lateinit var openFabAnimation: Animation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       settingToolbarAndBottomNavigation()

        (activity as MainActivity).apply {
            findViewById<ConstraintLayout>(R.id.header).visibility = View.VISIBLE
            findViewById<CoordinatorLayout>(R.id.coordinatorLayout).visibility = View.VISIBLE
        }


        setupRecyclerView()
        settingOnSwipeToDelete(view)
        gettingSavedNews()
    }

    private fun settingToolbarAndBottomNavigation() {
        openFabAnimation = AnimationUtils.loadAnimation(
            context,
            R.anim.fab_open
        )

        val label = activity?.findViewById<TextView>(R.id.lbl2)
        label?.text = "SavedArticles"
        (activity as MainActivity).apply {
            findViewById<ConstraintLayout>(R.id.header).animation = openFabAnimation
            findViewById<CoordinatorLayout>(R.id.coordinatorLayout).animation = openFabAnimation
        }
    }
    private fun gettingSavedNews() {
        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articles ->
            newsRecyclerViewAdapter.submitList(list = articles)

        })
    }

    private fun setupRecyclerView() {
        newsRecyclerViewAdapter = NewsRecyclerViewAdapter()
        recycler_view.apply {
            adapter = newsRecyclerViewAdapter
            layoutManager = LinearLayoutManager(activity)
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

    private fun settingOnSwipeToDelete(view: View) {
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsRecyclerViewAdapter.differ.currentList[position]
                viewModel.deleteArticle(article = article)
                Snackbar.make(view, "Article Deleted successfully", Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo") {
                        viewModel.saveNews(article = article)
                    }
                    show()
                }
            }
        }
        ItemTouchHelper(itemTouchHelper).apply {
            attachToRecyclerView(recycler_view)
        }
    }

}
