package com.telemed.demo

import android.app.Application
import com.telemed.demo.di.AppContainer

class TeleMedApp : Application() {
    val container: AppContainer by lazy { AppContainer() }
}

