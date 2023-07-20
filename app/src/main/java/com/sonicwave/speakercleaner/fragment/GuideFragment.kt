package com.sonicwave.speakercleaner.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.sonicwave.speakercleaner.inter.MainHost
import com.sonicwave.speakercleaner.databinding.FragmentGuideBinding
import com.jaeger.library.StatusBarUtil


class GuideFragment() : BaseFragment() {
    private lateinit var mBinding: FragmentGuideBinding
    val index= MutableLiveData<Int>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentGuideBinding.inflate(inflater)
        mBinding.lifecycleOwner = this
        mBinding.vm = this
        index.value=0
        StatusBarUtil.setTranslucentForImageViewInFragment(requireActivity(),0,mBinding.top)
        return mBinding.root
    }

    fun click(){
        if(index.value==2){
            mPreferenceUtil.setFirst()
            (requireActivity() as MainHost).resetNavToHome()
        }else{
            index.value=index.value!!+1
        }
    }
}