package com.sonicwave.speakercleaner.model


import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mufe.mvvm.library.base.BaseModel
import com.sonicwave.speakercleaner.ui.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ActivityViewModel(

) : BaseModel() {
    val SAMPLE_RATE_IN_HZ = 8000
    val BUFFER_SIZE = AudioRecord.getMinBufferSize(
        SAMPLE_RATE_IN_HZ,
        AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT
    )
    val isGetVoiceRun= MutableLiveData<Boolean>()
    val db= MutableLiveData<Double>()
    init{
        isGetVoiceRun.value=false
        db.value=0.0
    }
    fun initGoogle(activity: MainActivity, listener: () -> Unit){
        viewModelScope.launch {
            delay(100)
        }
    }
    fun delayStart(listener:()->Unit){
        viewModelScope.launch {
            delay(1000)
            listener()
        }
    }
    fun delayStartRate(listener:()->Unit){
        viewModelScope.launch {
            delay(3000)
            listener()
        }
    }
    fun startRecord(){
        if (isGetVoiceRun.value!!) {
            isGetVoiceRun.value=false
            return
        }
        isGetVoiceRun.value = true
        viewModelScope.launch {
            val mAudioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE
            )
            mAudioRecord.startRecording()
            val buffer = ShortArray(BUFFER_SIZE)
            while (isGetVoiceRun.value!!) {
                //r是实际读取的数据长度，一般而言r会小于buffersize
                val r = mAudioRecord.read(buffer, 0, BUFFER_SIZE)
                var v: Long = 0
                // 将 buffer 内容取出，进行平方和运算
                for (i in buffer.indices) {
                    v += (buffer[i] * buffer[i]).toLong()
                }

                // 平方和除以数据总长度，得到音量大小。
                val mean = v / r.toDouble()
                if(mean==0.0){
                    db.postValue(0.0)
                }else{
                    db.postValue(10 * Math.log10(mean))
                }

                // 大概一秒十次
                delay(100)
            }
            mAudioRecord.stop()
            mAudioRecord.release()
        }
    }
    sealed class ViewModelEvent {

    }



}