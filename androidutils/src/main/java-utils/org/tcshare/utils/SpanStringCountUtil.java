package org.tcshare.utils;

import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;

/**
 * SpannableStringBuilder辅助类， 统计Span个数
 * 注意：大量标签字时有性能问题！
 * @author tcshare.org
 *
 */
public class SpanStringCountUtil extends SpannableStringBuilder {

	public SpanStringCountUtil(SpannableStringBuilder text) {
		super(text);
	}


	public SpanStringCountUtil(CharSequence source) {
		super(source);
	}


	/**
	 * 统计Span的文字数， 每个Spannable当做一个字
	 * @return
	 */
	public int countWords() {
		CharacterStyle[] spans = getSpans(0, length(), CharacterStyle.class);
		int result = 0;
		SpannableStringBuilder ssbCopy = new SpannableStringBuilder(this);
		if (spans == null) { // 没有Span

			return ssbCopy.length();
		}

		for (int i = 0; i < spans.length; i++) {
			int spanStart = -1;
			while ((spanStart = ssbCopy.getSpanStart(spans[i])) != -1) {
				result++;
				int spanEnd = ssbCopy.getSpanEnd(spans[i]);
				ssbCopy.delete(spanStart, spanEnd);
			}
		}

		return result + ssbCopy.length();

	}

}
