package com.sonicwave.speakercleaner.fragment


import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.common.util.concurrent.ListenableFuture
import com.mufe.mvvm.library.extension.checkPermissions
import com.mufe.mvvm.library.util.DpUtil
import com.sonicwave.speakercleaner.databinding.FragmentDbBinding
import com.sonicwave.speakercleaner.inter.MainHost
import com.sonicwave.speakercleaner.model.ActivityViewModel
import com.tencent.bugly.crashreport.CrashReport
import org.koin.android.ext.android.inject
import java.util.concurrent.ExecutionException


class DbFragment() : BaseFragment() {
    private lateinit var mBinding: FragmentDbBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    val value = MutableLiveData<Int>()
    val min = MutableLiveData<String>()
    val max = MutableLiveData<String>()
    val avg = MutableLiveData<String>()
    val isStart = MutableLiveData<Boolean>()
    val isCamera = MutableLiveData<Boolean>()
    private val dpUtil: DpUtil by inject()
    private val mVm: ActivityViewModel by inject()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentDbBinding.inflate(inflater)
        mBinding.lifecycleOwner = this
        mBinding.vm = this
        isStart.value = false
        isCamera.value = false
        min.value = "0.0"
        max.value = "0.0"
        avg.value = "0.0"
        mBinding.scaleWheelViewWeight.init(dpUtil)
        mBinding.scaleWheelViewWeight.initViewParam(0.0f, 120.0f, 10)
        mBinding.scaleWheelViewWeight.setParam(
            dpUtil.dp2px(requireContext(), 25f),
            dpUtil.dp2px(requireContext(), 30f),
            dpUtil.dp2px(requireContext(), 12f)
        )
        addNavLiveData<Boolean> {
            min.value = "0.0"
            max.value = "0.0"
            avg.value = "0.0"
            if(isCamera.value!!){
                parseStoragePermissions(true)
            }else{
                parseStoragePermission(true)
            }

        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mVm.db.observe(viewLifecycleOwner, {
            var minValue = min.value.orEmpty().toDouble()
            var maxValue = max.value.orEmpty().toDouble()
            if (it < minValue) {
                minValue = it
                min.value = String.format("%.1f", it)
            }
            if (it > maxValue) {
                maxValue = it
                max.value = String.format("%.1f", it)
            }
//            avg.value = String.format("%.1f", (maxValue + minValue) / 2)
            avg.value = String.format("%.1f", it)
            mBinding.scaleWheelViewWeight.setValue((it / 10).toFloat())
        })
    }

    fun start() {
        ( requireContext() as MainHost).showAd(0)

    }

    fun startCamera() {
        ( requireContext() as MainHost).showAd(0)
        isCamera.value = true
    }

    fun stop() {
        ( requireContext() as MainHost).showAd(1)
        if(isCamera.value!!){
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                cameraProvider.unbindAll()
            } catch (e: ExecutionException) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            } catch (e: InterruptedException) {
            }
        }
        isStart.value = false
        isCamera.value = false
        mVm.isGetVoiceRun.value = false
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private val mGrantStoragePermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            parseStoragePermission(false)
        }

    @RequiresApi(23)
    private fun isPermissionsGranted(permission: String): Boolean {
        return requireContext().checkPermissions(permission)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun parseStoragePermission(first: Boolean) {
        if (!isPermissionsGranted(Manifest.permission.RECORD_AUDIO)) {
            if (first) {
                mGrantStoragePermissionResult.launch(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                    )
                )
                return
            } else {
                if (!isPermissionsGranted(Manifest.permission.RECORD_AUDIO)) {
                    return
                }
            }
        }
        isStart.value = true
        mVm.startRecord()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private val mGrantStoragePermissionResult1 =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            parseStoragePermissions(false)
        }


    @RequiresApi(23)
    private fun isPermissionsGranted(): Boolean {
        return requireContext().checkPermissions(Manifest.permission.RECORD_AUDIO) && requireContext().checkPermissions(
            Manifest.permission.CAMERA
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun parseStoragePermissions(first: Boolean) {
        if (!isPermissionsGranted()) {
            if (first) {
                mGrantStoragePermissionResult1.launch(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA,
                    )
                )
                return
            } else {
                if (!isPermissionsGranted()) {
                    return
                }
            }
        }
        isStart.value = true
        isCamera.value = true
        startToCamera()
        mVm.startRecord()
    }


    private fun startToCamera() {
        // 请求 CameraProvider
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        //检查 CameraProvider 可用性，验证它能否在视图创建后成功初始化
        cameraProviderFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider)
            } catch (e: ExecutionException) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            } catch (e: InterruptedException) {
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    //选择相机并绑定生命周期和用例
    fun bindPreview(@NonNull cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder()
            .build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(mBinding.previewView.getSurfaceProvider())
        cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview)
    }

}