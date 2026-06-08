package com.agermolin.playlistmaker.core.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.presentation.theme.YsDisplayMedium

@Composable
fun ConnectionErrorPlaceholder(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(R.drawable.nointernet),
            contentDescription = stringResource(R.string.connection_problem),
            modifier = Modifier.size(120.dp),
            tint = androidx.compose.ui.graphics.Color.Unspecified,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.connection_problem),
            textAlign = TextAlign.Center,
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                fontFamily = YsDisplayMedium,
            ),
            color = colorResource(R.color.text_color_primary),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.check_internet_connection),
            textAlign = TextAlign.Center,
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                fontFamily = YsDisplayMedium,
            ),
            color = colorResource(R.color.text_color_primary),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.button_background_color),
                contentColor = colorResource(R.color.button_text_color),
            ),
            modifier = Modifier.padding(15.dp),
        ) {
            Text(
                text = stringResource(R.string.retry_button),
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = YsDisplayMedium,
                ),
            )
        }
    }
}
