package com.school_of_company.gwangsan

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GwangsanApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}