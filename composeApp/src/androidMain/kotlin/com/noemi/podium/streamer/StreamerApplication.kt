package com.noemi.podium.streamer

import android.app.Application
import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import di.appModule
import di.serviceModule
import di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class StreamerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@StreamerApplication)
            logger(
                KermitKoinLogger(Logger.withTag("koin")),
            )
            modules(
                platformModule(),
                serviceModule(),
                appModule()
            )
        }
    }
}