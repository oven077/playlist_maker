package com.agermolin.playlistmaker.player.presentation.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.databinding.ItemPlaylistPickerRowBinding
import com.agermolin.playlistmaker.library.domain.model.Playlist
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.File

class PlayerPlaylistPickerAdapter(
    private val onPlaylistClick: (Playlist) -> Unit,
) : ListAdapter<Playlist, PlayerPlaylistPickerAdapter.RowViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val binding = ItemPlaylistPickerRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return RowViewHolder(binding, onPlaylistClick)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: RowViewHolder) {
        holder.clearCoverRequest()
        super.onViewRecycled(holder)
    }

    class RowViewHolder(
        private val binding: ItemPlaylistPickerRowBinding,
        private val onPlaylistClick: (Playlist) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.root.setOnClickListener { onPlaylistClick(playlist) }

            binding.playlistPickerTitle.text = playlist.name
            binding.playlistPickerTrackCount.text = itemView.context.resources.getQuantityString(
                R.plurals.playlist_track_count,
                playlist.trackCount,
                playlist.trackCount,
            )

            Glide.with(binding.playlistPickerCover).clear(binding.playlistPickerCover)
            binding.playlistPickerCover.setImageDrawable(null)

            val path = playlist.coverImagePath
            val file = path?.let { File(it) }
            if (file != null && file.exists() && file.length() > 0L) {
                loadCoverFromFile(file)
            } else {
                applyCoverPlaceholder()
            }
        }

        private fun loadCoverFromFile(file: File) {
            binding.playlistPickerCover.background = null
            binding.playlistPickerCover.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(binding.playlistPickerCover)
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
                .into(binding.playlistPickerCover)
        }

        private fun applyCoverPlaceholder() {
            Glide.with(binding.playlistPickerCover).clear(binding.playlistPickerCover)
            binding.playlistPickerCover.setImageDrawable(null)
            binding.playlistPickerCover.background = null
            binding.playlistPickerCover.scaleType = ImageView.ScaleType.CENTER_INSIDE
            binding.playlistPickerCover.setImageResource(R.drawable.playlist_grid_cover_placeholder)
        }

        fun clearCoverRequest() {
            Glide.with(binding.playlistPickerCover).clear(binding.playlistPickerCover)
        }
    }

    private object Diff : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean =
            oldItem == newItem
    }
}
