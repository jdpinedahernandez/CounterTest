package com.cornershop.counterstest.counter.di

import android.app.Application
import android.content.Context
import com.cornershop.counterstest.BuildConfig
import com.cornershop.counterstest.counter.data.CounterInfrastructure
import com.cornershop.counterstest.counter.data.CounterService
import com.cornershop.counterstest.counter.data.RemoteDataSource
import com.cornershop.counterstest.counter.data.TheCountersDb
import com.cornershop.counterstest.counter.data.TheCountersDbDataSource
import com.cornershop.counterstest.counter.domain.CountersInteractor
import com.cornershop.counterstest.counter.presentation.CountersActivity
import com.cornershop.counterstest.counter.presentation.CreateCounterActivity
import com.cornershop.counterstest.counter.viewmodel.CountersViewModel
import com.cornershop.counterstest.counter.viewmodel.CreateCounterViewModel
import com.cornershop.counterstest.utils.data.DataStore
import com.cornershop.counterstest.utils.data.NetworkHandler
import com.cornershop.counterstest.welcome.data.WelcomeInfrastructure
import com.cornershop.counterstest.welcome.data.WelcomeService
import com.cornershop.counterstest.welcome.presentation.WelcomeActivity
import com.cornershop.counterstest.welcome.viewmodel.WelcomeViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun Application.initDI() {
    startKoin {
        androidLogger()
        androidContext(this@initDI)
        modules(listOf(appModule, dataModule, scopesModule))
    }
}


private val appModule = module {
    single { NetworkHandler(androidApplication()) }
    single(named("baseUrl")) { BuildConfig.API_URL }
    single { TheCountersDb(get(named("baseUrl"))) }
    factory<WelcomeService> { WelcomeInfrastructure(get()) }
    single<CoroutineDispatcher> { Dispatchers.Main }
    factory<CounterService> { CounterInfrastructure(get(), get()) }
    factory<RemoteDataSource> { TheCountersDbDataSource(get(), get()) }
}

val dataModule = module {
    factory {
        DataStore(
            androidApplication().getSharedPreferences(
                "CountersTest",
                Context.MODE_PRIVATE
            )
        )
    }
    factory { CounterInfrastructure(get(), get()) }
}

private val scopesModule = module {
    scope(named<WelcomeActivity>()) {
        viewModel { WelcomeViewModel(get(), get()) }
        scoped { WelcomeInfrastructure(get()) }
    }

    scope(named<CountersActivity>()) {
        viewModel { CountersViewModel(get(), get()) }
        scoped { CountersInteractor(get()) }
    }

    scope(named<CreateCounterActivity>()) {
        viewModel { CreateCounterViewModel(get(), get()) }
        scoped { CountersInteractor(get()) }
    }
}