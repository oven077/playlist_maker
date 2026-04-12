package com.agermolin.playlistmaker.search.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.search.presentation.viewholder.SearchViewHolder

class SearchRecyclerAdapter(
    private val items: ArrayList<Track>,
    private val onTrackClick: (Track) -> Unit,
    private val onTrackLongClick: ((Track) -> Unit)? = null,
) : RecyclerView.Adapter<SearchViewHolder>() {

    var tracks: ArrayList<Track>
        get() = items
        set(value) {
            items.clear()
            items.addAll(value)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_recycler, parent, false)
        return SearchViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val track = items[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {
            onTrackClick(track)
        }

        holder.itemView.setOnLongClickListener {
            onTrackLongClick?.invoke(track)
            onTrackLongClick != null
        }
    }
}

