package com.sonicwave.speakercleaner.ui



import com.mufe.mvvm.library.BaseApplication
import com.sonicwave.speakercleaner.module.commonModule
import com.sonicwave.speakercleaner.module.viewModelModule

import org.koin.core.context.loadKoinModules


class CleanerApplication() : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        loadKoinModules(
            arrayListOf(
                commonModule,
                viewModelModule,
            )
        )
    }


}