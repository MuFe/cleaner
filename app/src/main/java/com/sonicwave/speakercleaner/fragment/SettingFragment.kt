package com.sonicwave.speakercleaner.fragment


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.github.nisrulz.zentone.ZenTone
import com.github.nisrulz.zentone.wave_generators.SineWaveGenerator
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.sonicwave.speakercleaner.R
import com.sonicwave.speakercleaner.databinding.FragmentSettingBinding
import com.sonicwave.speakercleaner.databinding.FragmentSoundBinding
import com.sonicwave.speakercleaner.databinding.FragmentToneBinding
import kotlinx.coroutines.CoroutineScope


class SettingFragment() : BaseFragment() {
    private lateinit var mBinding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSettingBinding.inflate(inflater)
        mBinding.lifecycleOwner = this
        mBinding.vm = this
        return mBinding.root
    }

    fun clickShare(){
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.sonicwave.speakercleaner")

// Android 10 开始，可以通过 Intent.EXTRA_TITLE 添加描述信息，ClipData 添加缩略图

// Android 10 开始，可以通过 Intent.EXTRA_TITLE 添加描述信息，ClipData 添加缩略图
        sendIntent.putExtra(Intent.EXTRA_TITLE, "Share it to your friends now! https://play.google.com/store/apps/details?id=com.sonicwave.speakercleaner")

        sendIntent.type = "text/plain"

        val shareIntent = Intent.createChooser(sendIntent, "Share it to your friends now! https://play.google.com/store/apps/details?id=com.sonicwave.speakercleaner")
        startActivity(shareIntent)
    }

    fun clickPrivacy(){
        goUrl("https://sites.google.com/view/sonicwavecleaner-privacypolicy/home")
    }

    fun clickRate(){
        val manager = ReviewManagerFactory.create(requireContext())
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We got the ReviewInfo object
                try {
                    val reviewInfo = task.result as ReviewInfo
                    val flow = manager.launchReviewFlow(requireActivity(), reviewInfo)
                    flow.addOnCompleteListener { it ->
                        Log.e("TAG","1111111")
                    }
                } catch (e: Exception) {
                    Log.e("TAG",task.exception.toString())
                }
            } else {
                // There was some problem, log or handle the error code.
                Log.e("TAG",task.exception.toString())
            }
        }
    }
    fun goUrl(url:String){
        val intent = Intent();
        intent.setAction("android.intent.action.VIEW");
        val content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }
    fun clickAbout(){
        goUrl("https://sites.google.com/view/sonicwave-cleaner-termsofuse/home")
    }
    fun clickFeedback(){
        val email = arrayOf("xuechuang127318@gmail.com")
        val intent = Intent(Intent.ACTION_SEND)

        intent.type = "message/rfc822" // 设置邮件格式


        intent.putExtra(Intent.EXTRA_EMAIL, email) // 接收人


        startActivity(Intent.createChooser(intent, "请选择邮件类应用"))
    }
}