package com.example.animeshark

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AnimeQueryItemAdapter(val items: MutableList<QueryResult>, val onClick: (QueryResult) -> Unit) :
    RecyclerView.Adapter<AnimeQueryItemAdapter.AnimeQueryItemViewHolder>() {

    // Describes an item view and its place within the RecyclerView
    class AnimeQueryItemViewHolder(itemView: View, val onClick: (QueryResult) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val animeTextView: TextView = itemView.findViewById(R.id.anime_query_item)
        private val queryImg: ImageView = itemView.findViewById(R.id.anime_query_img)
        private var currentAnime: QueryResult? = null

        init {
            itemView.setOnClickListener {
                currentAnime?.let {
                    onClick(it)
                }
            }
        }

        fun title(word: String) {
            animeTextView.text = word
        }
        fun img(uri: String) {
            Picasso.get().load(uri).into(queryImg);
        }
        fun anime(anime: QueryResult) {
            currentAnime = anime
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeQueryItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.anime_query_item, parent, false)

        return AnimeQueryItemViewHolder(view, onClick)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return items.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: AnimeQueryItemViewHolder, position: Int) {
        holder.title(items[position].title)
        holder.img(items[position].img)
        holder.anime(items[position])
    }

}