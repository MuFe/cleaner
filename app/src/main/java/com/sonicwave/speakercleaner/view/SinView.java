package com.sonicwave.speakercleaner.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.sonicwave.speakercleaner.R;


public class SinView extends View {

    private int height;
    private int width;

    private Path path;
    private Paint paint;

    private float waveHeight;

    private int lineColor;//线的颜色
    private int backColor;//背景色
    private float amplitude;//振幅
    public float frequency;//频率

    private float startAngle = (float) (Math.PI / 4);

    private int i = 0;

    public SinView(Context context) {
        this(context, null);
    }

    public SinView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public SinView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.SinView);
        amplitude = ta.getDimension(R.styleable.SinView_amplitude, 100);
        frequency = ta.getFloat(R.styleable.SinView_frequency, 100);
        lineColor = ta.getColor(R.styleable.SinView_lineColor, Color.GREEN);
        backColor = ta.getColor(R.styleable.SinView_backColor, Color.WHITE);

        paint = new Paint();
        paint.setColor(lineColor);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        path = new Path();

        ta.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        if (height == 0) {
            height = getMeasuredHeight();
            waveHeight = Math.min(height, amplitude) - 20;
            Log.e("KFJKFJ", "高度" + waveHeight + "getMeasuredHeight()" + getMeasuredHeight() + "amplitude" + amplitude);
        }
        if (width == 0) {
            width = getMeasuredWidth();
        }
    }

    public void start() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                i = 0;
                path.reset();
                startAngle += (Math.PI / 4);
                postInvalidate();
            }
        }, 200);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        for (i = 0; i < width; i += 5) {
            float y = (float) (waveHeight / 2 + waveHeight / 2 * Math.sin(i * (2 * Math.PI / frequency) + startAngle)) + 10;
            if (i == 0) {
                //设置path的起点
                path.moveTo(0, y);
            } else {
                //连线
                path.lineTo(i, y);
            }

        }

        canvas.drawPath(path, paint);
    }
}
