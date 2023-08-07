package com.sonicwave.speakercleaner.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.github.nisrulz.zentone.ZenTone
import com.github.nisrulz.zentone.wave_generators.SineWaveGenerator
import com.sonicwave.speakercleaner.R
import com.sonicwave.speakercleaner.databinding.FragmentSoundBinding
import com.sonicwave.speakercleaner.databinding.FragmentToneBinding
import com.sonicwave.speakercleaner.inter.MainHost
import kotlinx.coroutines.CoroutineScope


class ToneFragment() : BaseFragment() {
    private lateinit var mBinding: FragmentToneBinding
    val value = MutableLiveData<Int>()
    val isStart= MutableLiveData<Boolean>()
    private val zenTone = ZenTone()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentToneBinding.inflate(inflater)
        mBinding.lifecycleOwner = this
        mBinding.vm = this
        value.value=1000
        isStart.value=false
        return mBinding.root
    }

    fun startInfo(isShowAd:Boolean){
        mBinding.sine.start()
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
        zenTone.stop()
        startInfo(false)
    }
    fun increase(){
        value.value=value.value!!+100
        mBinding.sine.frequency=(value.value!!/10).toFloat()
        zenTone.stop()
        startInfo(false)
    }
}