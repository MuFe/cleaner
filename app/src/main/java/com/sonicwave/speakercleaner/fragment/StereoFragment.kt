package com.sonicwave.speakercleaner.fragment

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.sonicwave.speakercleaner.R
import com.sonicwave.speakercleaner.databinding.FragmentSoundBinding
import com.sonicwave.speakercleaner.databinding.FragmentStereoBinding
import com.sonicwave.speakercleaner.inter.MainHost


class StereoFragment() : BaseFragment() {
    private lateinit var mBinding: FragmentStereoBinding
    val check= MutableLiveData<Boolean>()
    val isStart= MutableLiveData<Boolean>()
    lateinit var bassPlayer:MediaPlayer
    lateinit var stereoPlayer:MediaPlayer
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentStereoBinding.inflate(inflater)
        mBinding.lifecycleOwner = this
        mBinding.vm = this
        check.value=true
        isStart.value=false
        bassPlayer=MediaPlayer.create(requireContext(), R.raw.bass)
        stereoPlayer=MediaPlayer.create(requireContext(), R.raw.stereo)
        return mBinding.root
    }

    fun start(){
        if(isStart.value!!){
            ( requireContext() as MainHost).showAd(1)
            if(stereoPlayer.isPlaying){
                stereoPlayer.pause()
            }
            if(bassPlayer.isPlaying){
                bassPlayer.pause()
            }
            isStart.value=false
        }else{
            ( requireContext() as MainHost).showAd(0)
            if(check.value!!){
                stereoPlayer.start()
            }else{
                bassPlayer.start()
            }
            isStart.value=true
        }
    }

    fun selectCheckValue(){
        check.value=!check.value!!
    }

}