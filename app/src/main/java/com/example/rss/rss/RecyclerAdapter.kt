package com.example.rss.rss

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.rss.R
import com.example.rss.model.RSSItem
import com.example.rss.ui.ArticleActivity

class RecyclerAdapter (
    private val itemList: List<RSSItem>,
    private val context : FragmentActivity?
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.rss_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: RSSItem = itemList[position]
        // img with glide
        var imgLink: String = item.imgLink
        if(!imgLink.isNullOrEmpty()) {
            context?.let {
                GlideApp.with(it)
                    .load(imgLink)
                    .into(holder.image)
            }
        }
        holder.title?.text = item.title
        holder.description?.text = item.description
        holder.bind(item)
    }

    override fun getItemCount(): Int = itemList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.item_image)
        val title: TextView? = view.findViewById(R.id.item_title)
        val description: TextView? = view.findViewById(R.id.item_desc)

        fun bind(rssItem: RSSItem){
            itemView.setOnClickListener{ view ->
                val intent: Intent = Intent(view.context, ArticleActivity::class.java)
                intent.putExtra("title", rssItem.title)
                intent.putExtra("link", rssItem.link)
                intent.putExtra("image", rssItem.imgLink)
                ContextCompat.startActivity(view.context, intent, null)
            }
        }
    }

}