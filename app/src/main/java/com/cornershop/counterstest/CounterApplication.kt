package com.cornershop.counterstest

import android.app.Application
import com.cornershop.counterstest.counter.di.initDI
import timber.log.Timber

class CounterApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initDI()
        setupTimber()
    }

    private fun setupTimber() {
        Timber.plant(Timber.DebugTree())
    }
}