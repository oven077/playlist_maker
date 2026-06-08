package com.agermolin.playlistmaker.core.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.core.presentation.theme.YsDisplayRegular
import com.agermolin.playlistmaker.core.presentation.theme.YsTextRegular

@Composable
fun TrackListItem(
    track: Track,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val minutes = track.trackTimeMillis / 60000
    val seconds = (track.trackTimeMillis % 60000) / 1000
    val trackTime = String.format("%02d:%02d", minutes, seconds)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = track.artworkUrl100,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(2.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.track_icon),
            error = painterResource(R.drawable.track_icon),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
        ) {
            Text(
                text = track.trackName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = YsDisplayRegular,
                ),
                color = colorResource(R.color.search_result_track_text_color),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = track.artistName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                    style = androidx.compose.material3.MaterialTheme.typography.labelMedium.copy(
                        fontFamily = YsTextRegular,
                    ),
                    color = colorResource(R.color.search_result_artist_text_color),
                )
                Icon(
                    painter = painterResource(R.drawable.ellipse),
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 4.dp),
                    tint = colorResource(R.color.search_result_artist_text_color),
                )
                Text(
                    text = trackTime,
                    style = androidx.compose.material3.MaterialTheme.typography.labelMedium.copy(
                        fontFamily = YsTextRegular,
                    ),
                    color = colorResource(R.color.search_result_artist_text_color),
                )
            }
        }

        Icon(
            painter = painterResource(R.drawable.arrow_forward),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            tint = colorResource(R.color.settings_icon_color),
        )
    }
}
