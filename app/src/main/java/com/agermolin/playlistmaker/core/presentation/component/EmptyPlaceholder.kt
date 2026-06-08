package com.agermolin.playlistmaker.core.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.presentation.theme.YsDisplayMedium

@Composable
fun EmptyPlaceholder(
    message: String,
    modifier: Modifier = Modifier,
    topPadding: androidx.compose.ui.unit.Dp = 106.dp,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = topPadding)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(R.drawable.nothingfound),
            contentDescription = message,
            modifier = Modifier.size(120.dp),
            tint = androidx.compose.ui.graphics.Color.Unspecified,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = message,
            textAlign = TextAlign.Center,
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                fontFamily = YsDisplayMedium,
            ),
            color = colorResource(R.color.text_color_primary),
        )
    }
}
