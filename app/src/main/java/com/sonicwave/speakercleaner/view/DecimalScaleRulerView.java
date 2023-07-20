package com.sonicwave.speakercleaner.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.mufe.mvvm.library.util.DpUtil;
import com.sonicwave.speakercleaner.util.TextUtil;


/**
 * Created by zoubo on 16/2/26.
 * 小数点横向滚动刻度尺
 */

public class DecimalScaleRulerView extends View {



    private int mWidth;


    private float mValue = 50;
    private float mMinValue = 0;
    private int mItemSpacing;
    private int mPerSpanValue = 1;
    private int mLineHeight;
    private int mTextMarginTop;
    private float mTextHeight;

    private Paint mTextPaint; // 绘制文本的画笔
    private Paint mLinePaint;

    private int mTotalLine;
    private float mOffset; // 默认尺起始点在屏幕中心, offset是指尺起始点的偏移值

    public DecimalScaleRulerView(Context context) {
        this(context, null);
    }

    public DecimalScaleRulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public DecimalScaleRulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(DpUtil dpUitl)  {
        mItemSpacing = dpUitl.dp2px(getContext(),14);
        mLineHeight = dpUitl.dp2px(getContext(),42);
        mTextMarginTop = dpUitl.dp2px(getContext(),11);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(dpUitl.sp2px(getContext(),16));
        mTextPaint.setColor(Color.parseColor("#ffffff"));
        mTextHeight = TextUtil.getFontHeight(mTextPaint);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth( dpUitl.dp2px(getContext(),2));
        mLinePaint.setColor(Color.parseColor("#ffffff"));
    }

    public void setParam(int lineHeight, int textMarginTop, int textSize) {
        mLineHeight = lineHeight;
        mTextMarginTop = textMarginTop;
        mTextPaint.setTextSize(textSize);
    }

    public void initViewParam( float minValue, float maxValue, int spanValue) {
        this.mMinValue = minValue;
        this.mPerSpanValue = spanValue;
        this.mTotalLine = (int) (maxValue  - minValue ) / spanValue + 1;
        if(mWidth>0){
            mItemSpacing = mWidth/mTotalLine;
        }
        mOffset = 10 ;
        invalidate();
        setVisibility(VISIBLE);
    }

    public void setValue(float defaultValue){
        this.mValue = defaultValue;
        invalidate();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            mWidth = w;
            if(mTotalLine>0){
                mItemSpacing = mWidth/mTotalLine;
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float left, height,startY;
        height = mLineHeight;
        startY = mLineHeight/2;
        String value;
        for (int i = 0; i < mTotalLine; i++) {
            left =  mOffset + i * mItemSpacing;
            if ( left > mWidth) {
                continue;
            }

            canvas.drawLine(left, startY, left, height+startY, mLinePaint);
            value = String.valueOf((int) (mMinValue + i * mPerSpanValue ));
            canvas.drawText(value, left - mTextPaint.measureText(value) / 2,
                    startY+height + mTextMarginTop + mTextHeight, mTextPaint);
        }
        mLinePaint.setColor(Color.parseColor("#4df0f2"));
        canvas.drawLine(mOffset+mValue*mItemSpacing, 0, mOffset+mValue*mItemSpacing, height+2*startY, mLinePaint);
        mLinePaint.setColor(Color.parseColor("#ffffff"));
    }



}
