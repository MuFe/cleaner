package com.sonicwave.speakercleaner.view


import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.sonicwave.speakercleaner.R


class SoundBgView : View {
    var radius: Int = 0
    var maxRadius: Int = 0
    var distance: Int = 0
    var alphaDistance: Int = 0
    var milliseconds: Int = 0
    var spreadRadius = mutableListOf<Int>()
    var alphas = mutableListOf<Int>()
    lateinit var centerPaint: Paint
    lateinit var spreadPaint: Paint
    var isStart=false
    constructor(context: Context?) : super(context) {

    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initConfig(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initConfig(attrs, defStyleAttr)
    }

    fun initConfig(attrs: AttributeSet?, defStyleAttr: Int) {
        val a: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.SpreadView, defStyleAttr, 0)

        radius = a.getInt(R.styleable.SpreadView_spread_radius, radius)

        maxRadius = a.getInt(R.styleable.SpreadView_spread_max_radius, maxRadius)

        val centerColor: Int = a.getColor(
            R.styleable.SpreadView_spread_center_color,
            ContextCompat.getColor(context, R.color.black)
        )

        val spreadColor: Int = a.getColor(
            R.styleable.SpreadView_spread_spread_color,
            ContextCompat.getColor(context, R.color.white)
        )

        distance = a.getInt(R.styleable.SpreadView_spread_distance, distance)
        alphaDistance = a.getInt(R.styleable.SpreadView_spread_alpha_distance, alphaDistance)
        milliseconds = a.getInt(R.styleable.SpreadView_spread_delay_milliseconds, 35)

        a.recycle()

        centerPaint = Paint()

        centerPaint.setColor(centerColor)
        centerPaint.setAlpha(125)
        centerPaint.setAntiAlias(true)

//最开始不透明且扩散距离为0


//最开始不透明且扩散距离为0


        spreadPaint = Paint()

        spreadPaint.setAntiAlias(true)

        spreadPaint.setAlpha(60)

        spreadPaint.setColor(spreadColor)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if(isStart){
            val centerX: Float = (measuredWidth / 2).toFloat()
            // 设置圆心Y轴位置
            val centerY: Float = (measuredHeight / 2).toFloat()
            for (i in 0 until spreadRadius.size) {
                var alpha: Int = alphas.get(i)
                spreadPaint.setAlpha(alpha)
                val width: Int = spreadRadius.get(i)
//绘制扩散的圆
                canvas.drawCircle(
                    centerX.toFloat(),
                    centerY.toFloat(), (radius + width).toFloat(), spreadPaint
                )

//每次扩散圆半径递增，圆透明度递减
                if (alpha > 0 && width < 500) {
                    alpha = if (alpha - alphaDistance > 0) (alpha - alphaDistance).toInt() else 1
                    alphas.set(i, alpha)
                    spreadRadius.set(i, width + distance)
                }
            }

//当最外层扩散圆半径达到最大半径时添加新扩散圆
            if (spreadRadius.get(spreadRadius.size - 1) > maxRadius) {
                spreadRadius.add(0)
                alphas.add(125)
            }

//超过8个扩散圆，删除最先绘制的圆，即最外层的圆
            if (spreadRadius.size >= 3) {
                alphas.removeAt(0)
                spreadRadius.removeAt(0)
            }


//延迟更新，达到扩散视觉差效果
            postInvalidateDelayed(milliseconds.toLong())
        }
    }

    fun start(){
        isStart=true
        alphas.add(125)
        spreadRadius.add(0)
        postInvalidateDelayed(milliseconds.toLong())
    }

    fun stop(){
        isStart=false
        alphas.clear()
        spreadRadius.clear()
    }

}