package com.example.myimage

import android.app.Application
import com.google.firebase.FirebaseApp

class MyImageApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
