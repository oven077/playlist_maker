package com.example.playlistmaker.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemSearchRecyclerBinding
import com.example.playlistmaker.model.Track

class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemSearchRecyclerBinding.bind(itemView)

    fun bind(track: Track) = with(binding) {
        trackName.text = track.trackName
        artistName.text = track.artistName

        // Безопасное форматирование времени
        try {
            val timeMillis = track.trackTimeMillis.toIntOrNull() ?: 0
            trackTime.text = String.format("%d:%02d", timeMillis / 60000, (timeMillis % 60000) / 1000)
        } catch (e: Exception) {
            trackTime.text = "0:00"
        }

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .centerCrop()
            .placeholder(R.drawable.track_icon)
            .transform(RoundedCorners(2))
            .into(trackIcon)
    }
}