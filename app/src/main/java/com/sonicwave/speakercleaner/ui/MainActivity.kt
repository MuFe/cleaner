package com.sonicwave.speakercleaner.ui



import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.ads.*
import com.sonicwave.speakercleaner.R
import com.sonicwave.speakercleaner.inter.MainHost
import com.sonicwave.speakercleaner.model.ActivityViewModel
import com.sonicwave.speakercleaner.util.PreferenceUtil
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.sonicwave.speakercleaner.databinding.ActivityMainBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.mufe.mvvm.library.util.DpUtil

class MainActivity : AppCompatActivity(), MainHost {
    private lateinit var mBinding: ActivityMainBinding
    val isHide = MutableLiveData<Boolean>()
    val index = MutableLiveData<Int>()
    val maskIndex = MutableLiveData<Int>()
    private val mVm: ActivityViewModel by viewModel()
    private val mPreferenceUtil: PreferenceUtil by inject()
    private val util: DpUtil by inject()
    private lateinit var navController: NavController
    private  var rewardedAd: RewardedAd?=null
    private var interstitialAd: InterstitialAd? = null
    // 插屏广告加载状态的回调
    private val interstitialAdLoadCallback = object : InterstitialAdLoadCallback() {
        override fun onAdLoaded(ad: InterstitialAd) {
            super.onAdLoaded(ad)
            Log.e("TAG","111111222")
            // 加载成功
            interstitialAd = ad
            // 设置广告事件回调
            interstitialAd?.fullScreenContentCallback = interstitialAdCallback
            // 显示插屏广告

        }
        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            super.onAdFailedToLoad(loadAdError)
            // 加载失败
            Log.e("TAG",loadAdError.message)
        }
    }
    // 插屏广告相关事件回调
    private val interstitialAdCallback = object : FullScreenContentCallback() {
        override fun onAdImpression() {
            super.onAdImpression()
            // 被记录为展示成功时调用
        }
        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            // 显示时调用
        }

        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            // 隐藏时调用，此时销毁当前的插屏广告对象，重新加载插屏广告
            interstitialAd=null
            loadInterstitialAd()
        }
        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            super.onAdFailedToShowFullScreenContent(adError)
            // 展示失败时调用，此时销毁当前的插屏广告对象，重新加载插屏广告
            interstitialAd=null
            loadInterstitialAd()
        }
    }
    private val rewardedAdLoadCallback = object : RewardedAdLoadCallback() {
        override fun onAdLoaded(ad: RewardedAd) {
            super.onAdLoaded(ad)
            Log.e("TAG","111111")
            // 加载成功
            rewardedAd = ad
            // 设置广告事件回调
            rewardedAd?.fullScreenContentCallback = rewardedVideoAdCallback
            // 展示广告
        }
        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            super.onAdFailedToLoad(loadAdError)
            // 加载失败
        }
    }
    private val rewardedVideoAdCallback = object : FullScreenContentCallback() {
        override fun onAdImpression() {
            super.onAdImpression()
            // 被记录为展示成功时调用
        }
        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            // 显示时调用
        }

        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            // 隐藏时调用，此时销毁当前的激励视频广告对象，重新加载激励视频广告
            rewardedAd=null
            val navController = Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment)
            if( navController.currentBackStackEntry?.savedStateHandle!=null){
                navController.currentBackStackEntry?.savedStateHandle!!.set("data",true)
            }
            loadRewardedVideoAd()
        }
        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            super.onAdFailedToShowFullScreenContent(adError)
            // 展示失败时调用，此时销毁当前的激励视频广告对象，重新加载激励视频广
            rewardedAd=null
            loadRewardedVideoAd()
        }
    }
    private val rewardedVideoAdEarnedCallback = OnUserEarnedRewardListener {

    }
    private var bannerAdView: AdView? = null
    private val bannerListener = object : AdListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
            // 广告加载成功
        }
        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            super.onAdFailedToLoad(loadAdError)
            // 广告加载失败
        }
        override fun onAdImpression() {
            super.onAdImpression()
            // 被记录为展示成功时调用
        }
        override fun onAdClicked() {
            super.onAdClicked()
            // 被点击时调用
        }
        override fun onAdOpened() {
            super.onAdOpened()
            // 广告落地页打开时调用
        }
        override fun onAdClosed() {
            super.onAdClosed()
            // 广告落地页关闭时调用
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        mBinding.lifecycleOwner = this
        mBinding.vm = this
        setContentView(mBinding.root)
        isHide.value = true
        maskIndex.value=-1
        MobileAds.initialize(this) {
            loadRewardedVideoAd()
            loadInterstitialAd()
            createBannerAdView()
        }
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
                    if(mPreferenceUtil.getMask(0)){
                        maskIndex.value=0
                    }
                    index.postValue(0)
                }
                getString(R.string.title_db) -> {
                    if(mPreferenceUtil.getMask(1)){
                        maskIndex.value=1
                    }
                    index.postValue(1)
                }
                getString(R.string.title_tone) -> {
                    if(mPreferenceUtil.getMask(2)){
                        maskIndex.value=2
                    }
                    index.postValue(2)
                }
                getString(R.string.title_stereo) -> {
                    maskIndex.value=-1
                    index.postValue(3)
                }
                getString(R.string.title_setting) -> {
                    maskIndex.value=-1
                    index.postValue(4)
                }
            }
        }
    }

    fun clickMask(){
        mPreferenceUtil.setMask(index.value!!)
        maskIndex.value=-1
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
    private fun loadRewardedVideoAd() {
        // adUnitId为Admob后台创建的激励视频广告的id
//        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917", AdRequest.Builder().build(), rewardedAdLoadCallback)
        RewardedAd.load(this, "ca-app-pub-1654154663629104/1340397288", AdRequest.Builder().build(), rewardedAdLoadCallback)
    }


    private fun loadInterstitialAd() {
        // adUnitId为Admob后台创建的插屏广告的id
//        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", AdRequest.Builder().build(), interstitialAdLoadCallback)

        InterstitialAd.load(this, "ca-app-pub-1654154663629104/1902723816", AdRequest.Builder().build(), interstitialAdLoadCallback)
    }
    private fun createBannerAdView() {
        // 获取页面的根布局
        val rootView = mBinding.ad
        bannerAdView = AdView(this)
        bannerAdView?.run {
            // 设置Banner的尺寸

            setAdSize(AdSize( AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT))
            // adUnitId为Admob后台创建的Banner广告的id
//            adUnitId = "ca-app-pub-3940256099942544/6300978111"
            adUnitId = "ca-app-pub-1654154663629104/7370672915"
            // 设置广告事件回调
            adListener = bannerListener
//            val bannerViewLayoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            // 设置显示在页面的底部中间
//            bannerViewLayoutParams.gravity =  Gravity.CENTER_HORIZONTAL
//            layoutParams = bannerViewLayoutParams

            // 把 Banner Ad 添加到根布局
            rootView.addView(this)
            //加载广告
            loadAd(AdRequest.Builder().build())
        }
    }
    fun showRewardedAd(){
        if(rewardedAd==null){
            val navController = Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment)
            if( navController.currentBackStackEntry?.savedStateHandle!=null){
                navController.currentBackStackEntry?.savedStateHandle!!.set("data",true)
            }
        }else{
            rewardedAd!!.show(this, rewardedVideoAdEarnedCallback)
        }

    }

    fun showInterstitialAd(){
        interstitialAd?.show(this)
    }

    override fun showAd(type: Int) {
        if(type==0){
            showRewardedAd()
            Log.e("TAG","11111")
        }else if(type==1){
            showInterstitialAd()
            Log.e("TAG","111112")
        }
    }

}