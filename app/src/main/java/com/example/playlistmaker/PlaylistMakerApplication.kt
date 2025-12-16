package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.core.di.dataModule
import com.example.playlistmaker.core.di.domainModule
import com.example.playlistmaker.core.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PlaylistMakerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@PlaylistMakerApplication)
            modules(
                dataModule,
                domainModule,
                presentationModule
            )
        }
    }
}
