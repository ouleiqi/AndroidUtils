package org.tcshare.utils;

import android.text.InputFilter.LengthFilter;
import android.text.Spanned;

/**
 * 限制输入的长度
 * @author tcshare.org
 *
 */
public class InputLenthFilter extends LengthFilter {
	private int max;
	private boolean spanAsOne;
	
	/**
	 * 默认标签当做多个字
	 * @param max
	 */
	public InputLenthFilter(int max) {
		this(max, false);
	}
	
	/**
	 * 
	 * @param max
	 * @param spanAsOne true: 标签当做一个字
	 */
	public InputLenthFilter(int max, boolean spanAsOne){
		super(max);
		this.max = max;
		this.spanAsOne = spanAsOne;
	}
	

	@Override
	public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
		int wordLen = 0;
		if(spanAsOne){
			SpanStringCountUtil countUtil = new SpanStringCountUtil(source);
			wordLen = countUtil.countWords();
		}else{
			wordLen = source.length();
		}
		
		if(wordLen > max){
			
			return null;
		}else{
			return super.filter(source, start, end, dest, dstart, dend);
		}
		
	}
	
	

}
