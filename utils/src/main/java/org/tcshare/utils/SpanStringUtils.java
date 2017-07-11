package org.tcshare.utils;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

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
}
