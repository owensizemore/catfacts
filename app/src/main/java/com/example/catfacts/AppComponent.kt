package com.example.catfacts

import dagger.Component

@Component(modules = [ApiModule::class])
interface AppComponent {
    fun inject(searchFragment: SearchFragment)
}