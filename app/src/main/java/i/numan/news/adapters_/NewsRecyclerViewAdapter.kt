package i.numan.news.adapters_

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import i.numan.news.R
import i.numan.news.dataclass_.Article
import kotlinx.android.synthetic.main.single_list_item.view.*

class NewsRecyclerViewAdapter : RecyclerView.Adapter<NewsRecyclerViewAdapter.ArticlesListViewHolder>() {

    // since Diff Util is working on main thread causes some issue while dealing with large amount of data
    // so to avoid this we'll use AsyncListDiffer
    private val diffCallback = object : DiffUtil.ItemCallback<Article>() {

        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }

    /*
    * List will be pass here from the function I had made below
     */
     val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticlesListViewHolder {
        return ArticlesListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.single_list_item, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ArticlesListViewHolder, position: Int) {

        holder.bind(differ.currentList[position], holder.itemView)

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Article>) {
        differ.submitList(list)
    }


    inner class ArticlesListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(item: Article?, view: View) {
            item?.let {
                view.apply {
                    Glide.with(context)
                        .load(item.urlToImage)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_image)
                        .into(ivArticleImage)

                    tvSource.text = item.source?.name
                    tvTitle.text = item.title
                    tvDescription.text = item.description
                    tvPublishedAt.text = item.publishedAt

                    setOnClickListener {
                        onItemClickListener?.let { it(item) }
                    }
                }

            }

        }
    }
    private var onItemClickListener: ((Article) -> Unit)? = null
    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
//    interface Interaction {
//        fun setOnClickListener(position: Int, item: Article)
//    }


}