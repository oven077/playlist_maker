package com.agermolin.playlistmaker.settings.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.presentation.component.PlaylistMakerTopBar
import com.agermolin.playlistmaker.settings.presentation.component.SettingsMenuRow
import com.agermolin.playlistmaker.settings.presentation.component.SettingsSwitchRow

@Composable
fun SettingsScreen(
    isDarkThemeEnabled: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onShareClick: () -> Unit,
    onSupportClick: () -> Unit,
    onUserAgreementClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.screen_background)),
    ) {
        PlaylistMakerTopBar(title = stringResource(R.string.settings))

        SettingsSwitchRow(
            title = stringResource(R.string.dark_theme),
            checked = isDarkThemeEnabled,
            onCheckedChange = onThemeChange,
        )

        SettingsMenuRow(
            title = stringResource(R.string.share),
            onClick = onShareClick,
            trailingIconRes = R.drawable.share,
        )

        SettingsMenuRow(
            title = stringResource(R.string.support),
            onClick = onSupportClick,
            trailingIconRes = R.drawable.support,
        )

        SettingsMenuRow(
            title = stringResource(R.string.user_agreement),
            onClick = onUserAgreementClick,
            trailingIconRes = R.drawable.arrow_forward,
        )
    }
}
