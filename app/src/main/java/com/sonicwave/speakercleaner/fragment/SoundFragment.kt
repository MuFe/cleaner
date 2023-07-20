package com.sonicwave.speakercleaner.fragment

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import androidx.lifecycle.MutableLiveData
import com.sonicwave.speakercleaner.R
import com.sonicwave.speakercleaner.databinding.FragmentSoundBinding


class SoundFragment() : BaseFragment() {
    private lateinit var mBinding: FragmentSoundBinding
    val isStart= MutableLiveData<Boolean>()
    lateinit var player:MediaPlayer
    lateinit var scaleAnimation:ScaleAnimation
    lateinit var scaleAnimation2:ScaleAnimation
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSoundBinding.inflate(inflater)
        mBinding.lifecycleOwner = this
        mBinding.vm = this
        isStart.value=false
        player=MediaPlayer.create(requireContext(), R.raw.cleaner_sound)
        //缩放动画，以中心从1.4倍放大到1.8倍
         scaleAnimation = ScaleAnimation(0.8f, 1.2f, 0.8f, 1.2f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.duration=800
//        缩放动画，以中心从1.4倍放大到1.8倍
         scaleAnimation2 =  ScaleAnimation(1.2f, 0.8f, 1.2f, 0.8f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation2.duration=800

        scaleAnimation.setAnimationListener(object :AnimationListener{
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                if(isStart.value!!){
                    mBinding.icon.startAnimation(scaleAnimation2)
                }

            }

            override fun onAnimationRepeat(p0: Animation?) {

            }
        })
        scaleAnimation2.setAnimationListener(object :AnimationListener{
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                if(isStart.value!!){
                    mBinding.icon.startAnimation(scaleAnimation)
                }

            }

            override fun onAnimationRepeat(p0: Animation?) {

            }
        })
        return mBinding.root
    }

    fun start(){
        if(player.isPlaying){
            player.pause()
            mBinding.bg1.stop()
            mBinding.icon.clearAnimation()
            mBinding.icon.clearAnimation()
            isStart.value=false
        }else{
            isStart.value=true
            player.start()
            mBinding.bg1.start()
            mBinding.icon.startAnimation(scaleAnimation)
        }
    }
}