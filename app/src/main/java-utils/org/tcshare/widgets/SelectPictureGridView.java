package org.tcshare.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

import org.tcshare.adapter.ChoosePhotoListAdapter;

import java.util.List;


/**
 * Created by yuxiaohei on 2015/8/25.
 */
public class SelectPictureGridView extends GridView {
    public SelectPictureGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    public SelectPictureGridView(Context context) {
        super(context);
        init(context);
    }

    public SelectPictureGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    private void init(Context context) {
        setAdapter(new ChoosePhotoListAdapter(context));
    }

    public List<String> getPicList(){
        ChoosePhotoListAdapter adapter = (ChoosePhotoListAdapter) getAdapter();
        return adapter.getPicList();
    }
    public void setMaxSelectSize(int maxSize){
        ChoosePhotoListAdapter adapter = (ChoosePhotoListAdapter) getAdapter();
        adapter.maxSize = maxSize;
    }
    public void setListener(ChoosePhotoListAdapter.LeforeAddListener beforeAddListener, ChoosePhotoListAdapter.LeforeDelListener beforeDelListener){
        ChoosePhotoListAdapter adapter = (ChoosePhotoListAdapter) getAdapter();
        adapter.setListener(beforeAddListener, beforeDelListener);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
/*
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);*/
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void clear() {
        ChoosePhotoListAdapter adapter = (ChoosePhotoListAdapter) getAdapter();
        adapter.clear();
    }
}
