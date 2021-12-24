package com.example.animeshark

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AnimeQueryItemAdapter(val items: MutableList<QueryResult>) :
    RecyclerView.Adapter<AnimeQueryItemAdapter.AnimeQueryItemViewHolder>() {

    // Describes an item view and its place within the RecyclerView
    class AnimeQueryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val flowerTextView: TextView = itemView.findViewById(R.id.anime_query_item)
        private val queryImg: ImageView = itemView.findViewById(R.id.anime_query_img)
        fun title(word: String) {
            flowerTextView.text = word
        }
        fun img(uri: String) {
            Picasso.get().load(uri).into(queryImg);
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeQueryItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.anime_query_item, parent, false)

        return AnimeQueryItemViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return items.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: AnimeQueryItemViewHolder, position: Int) {
        holder.title(items[position].title)
        holder.img(items[position].img)
    }

}