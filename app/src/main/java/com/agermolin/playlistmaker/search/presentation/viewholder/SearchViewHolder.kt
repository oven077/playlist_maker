package com.agermolin.playlistmaker.search.presentation.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.databinding.ItemSearchRecyclerBinding

class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemSearchRecyclerBinding.bind(itemView)

    fun bind(track: Track) = with(binding) {
        trackName.text = track.trackName
        artistName.text = track.artistName

        val minutes = track.trackTimeMillis / 60000
        val seconds = (track.trackTimeMillis % 60000) / 1000
        trackTime.text = String.format("%d:%02d", minutes, seconds)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .centerCrop()
            .placeholder(R.drawable.track_icon)
            .transform(RoundedCorners(2))
            .into(trackIcon)
    }
}

