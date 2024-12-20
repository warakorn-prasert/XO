package com.korn.portfolio.xo

import android.app.Application
import com.korn.portfolio.xo.repo.XODatabase
import com.korn.portfolio.xo.repo.XORepository

class XOApplication : Application() {
    private val db by lazy { XODatabase.getDatabase(this) }
    lateinit var repo: XORepository
        private set

    override fun onCreate() {
        super.onCreate()
        repo = XORepository(db.gameDao())
    }
}