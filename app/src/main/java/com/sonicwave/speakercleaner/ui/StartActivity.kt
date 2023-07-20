package com.sonicwave.speakercleaner.ui



import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sonicwave.speakercleaner.databinding.ActivityStartBinding
import com.jaeger.library.StatusBarUtil
import com.sonicwave.speakercleaner.model.ActivityViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class StartActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityStartBinding
    private val mVm: ActivityViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityStartBinding.inflate(layoutInflater)
        mBinding.lifecycleOwner = this
        mBinding.vm = this
        StatusBarUtil.setTranslucentForImageViewInFragment(this,0,mBinding.top)

        setContentView(mBinding.root)
        mVm.delayStart {
            startActivity(Intent(this@StartActivity, MainActivity::class.java))
            finish()
        }
    }



}