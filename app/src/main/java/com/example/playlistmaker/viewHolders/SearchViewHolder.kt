package com.example.playlistmaker.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemSearchRecyclerBinding
import com.example.playlistmaker.model.Track
import java.text.SimpleDateFormat
import java.util.Locale


class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemSearchRecyclerBinding.bind(itemView)

    fun bind(track: Track) = with(binding) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .centerCrop()
            .placeholder(R.drawable.track_icon)
            .transform(RoundedCorners(2))
            .into(trackIcon)

    }
}

