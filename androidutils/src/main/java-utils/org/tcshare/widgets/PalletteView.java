package org.tcshare.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;


/**
 * 颜料板
 */
public class PalletteView implements  AdapterView.OnItemClickListener {

    private final Context ctx;
    private ColorPickFinish listener;
    private GridView gridView;
    // 背景颜色
    private int bgColor = Color.WHITE;

    // 行列不能被整除， 会导致出现滚动条
    private String[] colors = new String[]{
            "#000000", "#7F7F7F", "#880015", "#ED1C24", "#FF7F27",
            "#FFFFFF", "#C3C3C3", "#B97A57", "#FFAEC9", "#FFC90E",
            "#FFF200", "#22B14C", "#00A2E8", "#3F48CC", "#A349A4",
            "#EFE4B0", "#B5E61D", "#99D9EA", "#7092BE", "#C8BFE7",
    };
    /**
     * 默认五列显示
     */
    private static final int numColumns = 5;
    /**
     * 色板的宽度，单位：dp
     */
    private static final int imgWidth = 36;
    private static final int imgHeight = 36;
    /**
     * 色板水平的间隙，单位：dp
     */
    private static final int horizontalSpacing = 8;
    /**
     * 色板垂直的间隙，单位：dp
     */
    private static final int verticalSpacing = 8;
    /**
     * 内边距
     */
    private static final float padding = 5;
    private PopupWindow pw;
    private ColorAdapter adapter;

    public PalletteView(Context context) {
        this.ctx = context;
    }

    public PalletteView(Context context, ColorPickFinish listener) {
        this.ctx = context;
        this.listener = listener;
    }

    public void setListener(ColorPickFinish listener) {
        this.listener = listener;
    }

    public void togglePallete(View anchor){
        if(pw == null) {
            View v = getContentView(ctx);
            int width = (int)dip2px(ctx, numColumns * imgWidth + (numColumns - 1) * horizontalSpacing);
            int height = (int)dip2px(ctx, (float) colors.length / numColumns * (verticalSpacing + imgHeight));
            pw = new PopupWindow(v, width, height, true);
            pw.setBackgroundDrawable(new BitmapDrawable());
            pw.setOutsideTouchable(true);
        }
        if(pw.isShowing()) {
            pw.dismiss();
        }else{
            pw.showAsDropDown(anchor);
        }
    }


    private View getContentView(Context ctx){
        if (gridView == null) {
            int pd = (int)dip2px(ctx, padding);

            gridView = new GridView(ctx);
            adapter = new ColorAdapter(colors, ctx);

            gridView.setBackgroundColor(bgColor);
            gridView.setPadding(pd, pd,pd,pd);
            gridView.setAdapter(adapter);
            gridView.setNumColumns(numColumns);
            gridView.setHorizontalSpacing((int) dip2px(ctx, horizontalSpacing));
            gridView.setVerticalSpacing(verticalSpacing);
            gridView.setOnItemClickListener(this);
        }
        return gridView;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(listener != null){
            listener.onSelected(adapter.getItem(position));
        }
        pw.dismiss();
    }

    public interface ColorPickFinish{
        void onSelected(int color);
    }

    /**
     * 颜色适配器
     */
    private class ColorAdapter extends BaseAdapter {

        private final String[] colors;
        private final Context ctx;

        public ColorAdapter(String[] colors, Context ctx) {
            if (colors != null) {
                this.colors = colors;
            } else {
                this.colors = new String[]{};
            }
            this.ctx = ctx;
        }
        @Override
        public int getCount() {
            return colors.length;
        }

        @Override
        public Integer getItem(int position) {
            return Color.parseColor(colors[position]);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new ImageView(ctx);
                int width = (int) dip2px(ctx, imgWidth);
                int height = (int) dip2px(ctx, imgHeight);
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(width, height);
                convertView.setLayoutParams(lp);
            }
            try {
                convertView.setBackgroundColor(getItem(position));
            } catch (Exception e) {
                 e.printStackTrace();
            }
            return convertView;
        }
    }

    public static float dip2px(Context ctx, float dip){
        final float scale = ctx.getResources().getDisplayMetrics().density;

        return dip * scale;
    }

    /**
     * 像素转dp
     * @param ctx
     * @param px
     * @return
     */
    public static float px2dip(Context ctx, float px){
        final float scale = ctx.getResources().getDisplayMetrics().density;

        return px / scale;
    }
}
