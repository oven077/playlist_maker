package com.agermolin.playlistmaker.settings.presentation.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.presentation.theme.PlaylistMakerTheme
import com.agermolin.playlistmaker.settings.domain.interactor.IGetDarkThemeInteractor
import com.agermolin.playlistmaker.settings.domain.interactor.ISetDarkThemeInteractor
import com.agermolin.playlistmaker.settings.presentation.screen.SettingsScreen
import org.koin.android.ext.android.inject

class SettingsFragment : Fragment() {

    private val getDarkThemeInteractor: IGetDarkThemeInteractor by inject()
    private val setDarkThemeInteractor: ISetDarkThemeInteractor by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            var isDarkThemeEnabled by remember {
                mutableStateOf(getDarkThemeInteractor.execute())
            }

            PlaylistMakerTheme(darkTheme = isDarkThemeEnabled) {
                SettingsScreen(
                    isDarkThemeEnabled = isDarkThemeEnabled,
                    onThemeChange = { isChecked ->
                        isDarkThemeEnabled = isChecked
                        val mode = if (isChecked) {
                            AppCompatDelegate.MODE_NIGHT_YES
                        } else {
                            AppCompatDelegate.MODE_NIGHT_NO
                        }
                        AppCompatDelegate.setDefaultNightMode(mode)
                        setDarkThemeInteractor.execute(isChecked)
                    },
                    onShareClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
                            type = "text/plain"
                        }
                        startActivity(Intent.createChooser(intent, null))
                    },
                    onSupportClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:")
                            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_address)))
                            putExtra(Intent.EXTRA_TEXT, getString(R.string.email_message))
                            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_theme))
                        }
                        startActivity(Intent.createChooser(intent, null))
                    },
                    onUserAgreementClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(getString(R.string.support_user_agreement))
                        }
                        startActivity(Intent.createChooser(intent, null))
                    },
                )
            }
        }
    }
}
