package com.example.animeshark

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EpisodesAdapter(val items: MutableList<Episode>, val onClick: (Episode) -> Unit) :
    RecyclerView.Adapter<EpisodesAdapter.EpisodeViewHolder>() {

    // Describes an item view and its place within the RecyclerView
    class EpisodeViewHolder(itemView: View, val onClick: (Episode) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val episodeTextView: TextView = itemView.findViewById(R.id.anime_episode)
        private var currentEpisode: Episode? = null

        init {
            itemView.setOnClickListener {
                currentEpisode?.let {
                    onClick(it)
                }
            }
        }

        fun bind(episode: Episode) {
            currentEpisode = episode
            episodeTextView.text = episode.number
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.anime_episode, parent, false)

        return EpisodeViewHolder(view, onClick)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return items.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        holder.bind(items[position])
    }

}