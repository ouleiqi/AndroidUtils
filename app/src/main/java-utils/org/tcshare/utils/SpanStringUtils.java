package org.tcshare.utils;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

/**
 * Created by FallRain on 2017/5/5.
 */

public class SpanStringUtils {

    public static CharSequence firstLineBold(String str) {
        int firstLineEnd = str.indexOf("\n");
        SpannableString ss = new SpannableString(str);
        if (firstLineEnd != -1) {
            ss.setSpan(new StyleSpan(Typeface.BOLD), 0, firstLineEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ss;
    }

    public static CharSequence changeForegroundColor(CharSequence str, int start, int end, int color) {

        SpannableString ss = new SpannableString(str);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        ss.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }
    public static CharSequence changeBackgroundColor(CharSequence str, int start, int end, int color) {

        SpannableString ss = new SpannableString(str);
        BackgroundColorSpan colorSpan = new BackgroundColorSpan(color);
        ss.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }
    public static void changeForegroundColor(TextView tv, int start, int end, int color) {
        if(tv.getText().length() < end) return;
        CharSequence cs = changeForegroundColor(tv.getText(), start, end, color);
        tv.setText(cs);
    }
}
