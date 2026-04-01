package com.agermolin.playlistmaker.library.presentation.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.databinding.ItemPlaylistGridBinding
import com.agermolin.playlistmaker.library.domain.model.Playlist
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.File

class PlaylistsAdapter(
    private val onPlaylistClick: (Playlist) -> Unit,
) : ListAdapter<Playlist, PlaylistsAdapter.PlaylistGridViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistGridViewHolder {
        val binding = ItemPlaylistGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return PlaylistGridViewHolder(binding, onPlaylistClick)
    }

    override fun onBindViewHolder(holder: PlaylistGridViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: PlaylistGridViewHolder) {
        holder.clearCoverRequest()
        super.onViewRecycled(holder)
    }

    class PlaylistGridViewHolder(
        private val binding: ItemPlaylistGridBinding,
        private val onPlaylistClick: (Playlist) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.root.setOnClickListener { onPlaylistClick(playlist) }
            Glide.with(binding.playlistItemCover).clear(binding.playlistItemCover)
            binding.playlistItemCover.setImageDrawable(null)

            binding.playlistItemTitle.text = playlist.name
            binding.playlistItemTrackCount.text = itemView.context.resources.getQuantityString(
                R.plurals.playlist_track_count,
                playlist.trackCount,
                playlist.trackCount,
            )

            binding.playlistItemCover.setPadding(0, 0, 0, 0)
            val path = playlist.coverImagePath
            val file = path?.let { File(it) }
            if (file != null && file.exists() && file.length() > 0L) {
                loadCoverFromFile(file)
            } else {
                applyCoverPlaceholder()
            }
        }

        private fun loadCoverFromFile(file: File) {
            binding.playlistItemCover.background = null
            binding.playlistItemCover.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(binding.playlistItemCover)
                .load(file)
                .centerCrop()
                .listener(
                    object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable?>,
                            isFirstResource: Boolean,
                        ): Boolean {
                            applyCoverPlaceholder()
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable?>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean,
                        ): Boolean = false
                    },
                )
                .into(binding.playlistItemCover)
        }

        private fun applyCoverPlaceholder() {
            Glide.with(binding.playlistItemCover).clear(binding.playlistItemCover)
            binding.playlistItemCover.setImageDrawable(null)
            binding.playlistItemCover.background = null
            binding.playlistItemCover.setPadding(0, 0, 0, 0)
            binding.playlistItemCover.scaleType = ImageView.ScaleType.CENTER_INSIDE
            binding.playlistItemCover.setImageResource(R.drawable.playlist_grid_cover_placeholder)
        }

        fun clearCoverRequest() {
            Glide.with(binding.playlistItemCover).clear(binding.playlistItemCover)
        }
    }

    private object Diff : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean =
            oldItem == newItem
    }
}
