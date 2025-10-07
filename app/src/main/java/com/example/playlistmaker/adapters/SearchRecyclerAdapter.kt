package com.example.playlistmaker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.viewHolders.SearchViewHolder

class SearchRecyclerAdapter(
    private val items: ArrayList<Track>,
    private val onTrackClickListener: (Track) -> Unit
) : RecyclerView.Adapter<SearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_recycler, parent, false)
        return SearchViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val track = items[position]
        holder.bind(track)

        // Добавляем обработчик клика
        holder.itemView.setOnClickListener {
            onTrackClickListener(track)
        }
    }
}