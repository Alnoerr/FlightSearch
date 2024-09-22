package dev.alnoer.flightsearch

import android.app.Application
import dev.alnoer.flightsearch.data.AppContainer
import dev.alnoer.flightsearch.data.DefaultAppContainer

class FlightSearchApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}