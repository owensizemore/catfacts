package com.example.catfacts

import android.app.Application
class CatApplication : Application(), AppComponentContainer {
    private lateinit var appComponent: AppComponent
    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .apiModule(ApiModule())
            .build()
    }

    override fun injector(): AppComponent {
        return appComponent
    }
}