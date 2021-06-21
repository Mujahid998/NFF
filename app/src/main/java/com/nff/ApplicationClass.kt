package com.nff

import android.app.Application

class ApplicationClass: Application() {

    companion object{
        var PANTRY_PRICE=12
    }


    override fun onCreate() {
        super.onCreate()
    }
}