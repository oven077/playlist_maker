package com.agermolin.playlistmaker.main.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
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

        binding.bottomNavigation.setBackgroundColor(
            ContextCompat.getColor(this, R.color.bottom_nav_bar_background),
        )

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        val topLevelDestinations = setOf(
            R.id.searchFragment,
            R.id.libraryFragment,
            R.id.settingsFragment,
        )
        fun applyNavHostBottomConstraint(destinationId: Int) {
            val root = binding.root as ConstraintLayout
            val constraintSet = ConstraintSet()
            constraintSet.clone(root)
            constraintSet.clear(R.id.nav_host_fragment, ConstraintSet.BOTTOM)
            if (destinationId in topLevelDestinations) {
                constraintSet.connect(
                    R.id.nav_host_fragment,
                    ConstraintSet.BOTTOM,
                    R.id.bottom_navigation,
                    ConstraintSet.TOP,
                )
            } else {
                constraintSet.connect(
                    R.id.nav_host_fragment,
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM,
                )
            }
            constraintSet.applyTo(root)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.isVisible = destination.id in topLevelDestinations
            applyNavHostBottomConstraint(destination.id)
            binding.bottomNavigation.menu.findItem(destination.id)?.isChecked = true
        }

        navController.currentDestination?.let { applyNavHostBottomConstraint(it.id) }
    }
}

