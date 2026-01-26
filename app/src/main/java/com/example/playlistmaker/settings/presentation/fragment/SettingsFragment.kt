package com.example.playlistmaker.settings.presentation.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.domain.interactor.IGetDarkThemeInteractor
import com.example.playlistmaker.settings.domain.interactor.ISetDarkThemeInteractor
import org.koin.android.ext.android.inject

class SettingsFragment : Fragment() {

    private val getDarkThemeInteractor: IGetDarkThemeInteractor by inject()
    private val setDarkThemeInteractor: ISetDarkThemeInteractor by inject()

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Top-level tab: no back arrow
        binding.settingsToolbar.navigationIcon = null

        val isDarkThemeEnabled = getDarkThemeInteractor.execute()

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        binding.switchDarkTheme.isChecked = isDarkThemeEnabled
        binding.switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            val mode = if (isChecked) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(mode)
            setDarkThemeInteractor.execute(isChecked)
        }

        binding.buttonSharing.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, null))
        }

        binding.buttonSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_address)))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_message))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_theme))
            startActivity(Intent.createChooser(intent, null))
        }

        binding.buttonUserAgreement.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.support_user_agreement))
            startActivity(Intent.createChooser(intent, null))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): SettingsFragment = SettingsFragment()
    }
}

