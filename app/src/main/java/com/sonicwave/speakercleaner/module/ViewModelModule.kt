package com.sonicwave.speakercleaner.module






import com.sonicwave.speakercleaner.model.ActivityViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { ActivityViewModel() }


}

