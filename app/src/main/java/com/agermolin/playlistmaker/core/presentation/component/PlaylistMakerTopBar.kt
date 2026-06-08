package com.agermolin.playlistmaker.core.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.presentation.theme.YsDisplayMedium

@Composable
fun PlaylistMakerTopBar(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 14.dp, bottom = 24.dp),
        style = androidx.compose.material3.MaterialTheme.typography.headlineMedium.copy(
            fontFamily = YsDisplayMedium,
        ),
        color = colorResource(R.color.toolbar_color),
    )
}
