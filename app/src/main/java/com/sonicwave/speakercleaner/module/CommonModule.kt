package com.sonicwave.speakercleaner.module



import com.sonicwave.speakercleaner.util.*
import org.koin.dsl.module

val commonModule = module {
    single { PreferenceUtil(get()) }
}
