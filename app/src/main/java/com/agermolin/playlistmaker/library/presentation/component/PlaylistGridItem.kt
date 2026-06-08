package com.agermolin.playlistmaker.library.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.presentation.theme.YsDisplayRegular
import com.agermolin.playlistmaker.core.presentation.theme.YsTextRegular
import com.agermolin.playlistmaker.library.domain.model.Playlist
import java.io.File

@Composable
fun PlaylistGridItem(
    playlist: Playlist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val coverFile = playlist.coverImagePath?.let { path ->
        File(path).takeIf { it.exists() && it.length() > 0L }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(coverFile)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.playlist_cover_preview_description),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = if (coverFile != null) ContentScale.Crop else ContentScale.Inside,
            placeholder = painterResource(R.drawable.track_icon),
            error = painterResource(R.drawable.track_icon),
        )

        Text(
            text = playlist.name,
            modifier = Modifier.padding(top = 8.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(
                fontFamily = YsDisplayRegular,
            ),
            color = colorResource(R.color.playlist_grid_title_text),
        )

        Text(
            text = pluralStringResource(
                R.plurals.playlist_track_count,
                playlist.trackCount,
                playlist.trackCount,
            ),
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
            style = androidx.compose.material3.MaterialTheme.typography.labelMedium.copy(
                fontFamily = YsTextRegular,
            ),
            color = colorResource(R.color.playlist_grid_secondary_text),
        )
    }
}
