package com.example.bitfitpart2

import android.app.Application

class BitFitPart2Application : Application() {
    val db by lazy { AppDatabase.getInstance(this) }
}

