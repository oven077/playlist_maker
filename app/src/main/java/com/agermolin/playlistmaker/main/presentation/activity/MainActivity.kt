package com.agermolin.playlistmaker.main.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.databinding.ActivityMainBinding
import com.agermolin.playlistmaker.settings.domain.interactor.IGetDarkThemeInteractor
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val getDarkThemeInteractor: IGetDarkThemeInteractor by inject()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val isDarkThemeEnabled = getDarkThemeInteractor.execute()
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        val topLevelDestinations = setOf(
            R.id.searchFragment,
            R.id.libraryFragment,
            R.id.settingsFragment,
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.isVisible = destination.id in topLevelDestinations
            // Ensure bottom nav highlights current destination (even on app start)
            binding.bottomNavigation.menu.findItem(destination.id)?.isChecked = true
        }
    }
}

