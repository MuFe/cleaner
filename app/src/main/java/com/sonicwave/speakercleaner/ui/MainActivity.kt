package com.sonicwave.speakercleaner.ui



import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.sonicwave.speakercleaner.R
import com.sonicwave.speakercleaner.inter.MainHost
import com.sonicwave.speakercleaner.model.ActivityViewModel
import com.sonicwave.speakercleaner.util.PreferenceUtil
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.sonicwave.speakercleaner.databinding.ActivityMainBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity(), MainHost {
    private lateinit var mBinding: ActivityMainBinding
    val isHide = MutableLiveData<Boolean>()
    val index = MutableLiveData<Int>()
    private val mVm: ActivityViewModel by viewModel()
    private val mPreferenceUtil: PreferenceUtil by inject()
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        mBinding.lifecycleOwner = this
        mBinding.vm = this
        setContentView(mBinding.root)
        isHide.value = true
        mVm.initGoogle(this) {

        }
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)
        if (mPreferenceUtil.isFirst()) {
            navGraph.setStartDestination(R.id.navigation_sound)
        } else {
            navGraph.setStartDestination(R.id.navigation_sound)
        }
        navController.graph = navGraph
        getWindow().setStatusBarColor(resources.getColor(R.color.bg))
        navController.addOnDestinationChangedListener { _, destination, _ ->

            if (destination.label == getString(R.string.title_sound) || destination.label == getString(
                    R.string.title_db
                ) || destination.label == getString(R.string.title_tone) || destination.label == getString(
                    R.string.title_stereo
                )|| destination.label == getString(
                    R.string.title_setting
                )
            ) {
                isHide.value = false
            } else {
                isHide.value = true
            }
            when (destination.label) {
                getString(R.string.title_sound) -> {
                    index.postValue(0)
                }
                getString(R.string.title_db) -> {
                    index.postValue(1)
                }
                getString(R.string.title_tone) -> {
                    index.postValue(2)
                }
                getString(R.string.title_stereo) -> {
                    index.postValue(3)
                }
                getString(R.string.title_setting) -> {
                    index.postValue(4)
                }
            }
        }
    }

    fun clickRate() {
        mVm.delayStartRate {
            this.runOnUiThread {
                val manager = ReviewManagerFactory.create(this)
                val request = manager.requestReviewFlow()
                request.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // We got the ReviewInfo object
                        try {
                            val reviewInfo = task.result as ReviewInfo
                            val flow = manager.launchReviewFlow(this, reviewInfo)
                            flow.addOnCompleteListener { it ->
                                Log.e("TAG", "1111111")
                            }
                        } catch (e: Exception) {
                            Log.e("TAG", task.exception.toString())

                        }
                    } else {

                        // There was some problem, log or handle the error code.
                        Log.e("TAG", task.exception.toString())
                    }
                }
            }
        }

    }

    fun goSound() {
        navController.navigate(R.id.navigation_sound, bundleOf())
    }

    fun goDb() {
        navController.navigate(R.id.navigation_db, bundleOf())
    }


    fun goTone() {
        navController.navigate(R.id.navigation_tone, bundleOf())
    }

    fun goStereo() {
        navController.navigate(R.id.navigation_stereo, bundleOf())
    }

    fun goSetting() {
        navController.navigate(R.id.navigation_setting, bundleOf())
    }


    override fun resetNavToHome() {
        getWindow().getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        getWindow().setStatusBarColor(resources.getColor(R.color.bg));
        val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)
        navGraph.setStartDestination(R.id.navigation_sound)
        navController.graph = navGraph
//        clickRate()
    }

    override fun resetNavToLogin() {

    }


}