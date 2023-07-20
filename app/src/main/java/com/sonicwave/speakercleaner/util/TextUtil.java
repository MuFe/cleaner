package com.sonicwave.speakercleaner.util;

import android.graphics.Paint;
import android.text.TextPaint;

/**
 * Created by chenjingmian
 */
public class TextUtil {


    /**
     * @return 返回指定笔的文字高度
     */
    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }


}
