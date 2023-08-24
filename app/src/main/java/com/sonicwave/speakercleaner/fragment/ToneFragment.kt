package com.sonicwave.speakercleaner.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.nisrulz.zentone.ZenTone
import com.github.nisrulz.zentone.wave_generators.SineWaveGenerator
import com.sonicwave.speakercleaner.databinding.FragmentToneBinding
import com.sonicwave.speakercleaner.inter.MainHost
import com.sonicwave.speakercleaner.model.ActivityViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ToneFragment() : BaseFragment() {
    private lateinit var mBinding: FragmentToneBinding
    val value = MutableLiveData<Int>()
    val isStart= MutableLiveData<Boolean>()
    private val zenTone = ZenTone()
    private val mVm: ActivityViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentToneBinding.inflate(inflater)
        mBinding.lifecycleOwner = this
        mBinding.vm = this
        value.value=1000
        isStart.value=false
        addNavLiveData<Boolean> {
            startInfo(false)
        }
        return mBinding.root
    }

    fun startInfo(isShowAd:Boolean){

        when {
            zenTone.isPlaying -> {
                if(isShowAd){
                    ( requireContext() as MainHost).showAd(1)
                }
                zenTone.stop()
                isStart.value=false
            }
            else -> {
               if(isShowAd){
                   ( requireContext() as MainHost).showAd(0)
                   return
               }
                isStart.value=true
                zenTone.play(
                    frequency = value.value!!.toFloat(),
                    volume = 2,
                    waveByteArrayGenerator = SineWaveGenerator
                )
            }
        }
    }

    fun start(){
        startInfo(true)
    }

    fun reduce(){
        value.value=value.value!!-100
        mBinding.sine.frequency=(value.value!!/10).toFloat()
        mBinding.sine.start()
        if(isStart.value!!){
            zenTone.stop()
            mVm.viewModelScope.launch {
                delay(100)
                startInfo(false)
            }

        }
    }
    fun increase(){
        value.value=value.value!!+100
        mBinding.sine.frequency=(value.value!!/10).toFloat()
        mBinding.sine.start()
        if(isStart.value!!){
            zenTone.stop()
            mVm.viewModelScope.launch {
                delay(100)
                startInfo(false)
            }
        }
    }
}