package org.tcshare.widgets;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.tcshare.app.R;

/**
 * Created by yuxiaohei on 2018/3/13.
 */

public class BottomListDialog {

    /**
     * 简单的底部列表弹出框
     */
    public static void showSimpleDialog(Context ctx, final OnItemClickListener listener, String[]... groups) {
        showSimpleDialog(ctx, listener, R.layout.dialog_bottom_item, R.id.text, groups);
    }
    /**
     * 简单的底部列表弹出框, 修改item divider颜色
     */
    public static void showSimpleDialog(Context ctx, final OnItemClickListener listener,int itemDividerColor, String[]... groups) {
        int transColor = ctx.getResources().getColor(android.R.color.transparent);
        showDialog(ctx, R.layout.dialog_bottom_container, R.layout.dialog_bottom_group, R.layout.dialog_bottom_item, R.id.text, 12, 1, transColor, itemDividerColor, listener, groups);
    }

    /**
     * 简单的底部列表弹出框
     * @param ctx
     * @param listener
     * @param itemLayout      item布局文件
     * @param itemTextId      item 中显示字体的TextView id
     * @param groups          分组list
     */
    public static void showSimpleDialog(Context ctx, final OnItemClickListener listener,int itemLayout, int itemTextId, String[]... groups) {
        int transColor = ctx.getResources().getColor(android.R.color.transparent);
        showDialog(ctx, R.layout.dialog_bottom_container, R.layout.dialog_bottom_group, itemLayout, itemTextId, 12, 1, transColor,Color.LTGRAY , listener, groups);
    }

    /**
     * 分组底部弹出的对话框
     *
     * @param containerLayout 最外层的布局文件
     * @param groupLayout     分组布局文件
     * @param itemLayout      item布局文件
     * @param itemTextId      item 中显示字体的TextView id
     */
    public static void showDialog(Context ctx, int containerLayout, int groupLayout, int itemLayout, int itemTextId, int groupDivderHeight,
            int itemDividerHeight,int groupDividerColor, int itemDividerColor,  final OnItemClickListener listener, String[]... groups) {
         final BottomSheetDialog dialog = new BottomSheetDialog(ctx, R.style.BottomListSheetDialogTheme);
        ViewGroup dialogBg = (ViewGroup) LayoutInflater.from(ctx).inflate(containerLayout, null, false);
        //TODO 如果有需要自定义布局， 在dialogBg里findView 设置onClickListener

        int oneDP = (int) dip2px(ctx, 1);
        int pos = 0;
        for (int i = 0; i < groups.length; i++) {
            ViewGroup groupBG = (ViewGroup) LayoutInflater.from(ctx) .inflate(groupLayout, null, false);
            for (int k = 0; k < groups[i].length; k++) {
                View itemView = LayoutInflater.from(ctx) .inflate(itemLayout, null, false);
                TextView itemTextView = (TextView) itemView.findViewById(itemTextId);
                itemTextView.setText(groups[i][k]);
                recursiveSetOnClickListener(itemView, pos++, listener, dialog); // 递归的给所有Item设置点击事件
                View itemDivider = new View(ctx);
                itemDivider.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemDividerHeight * oneDP));
                itemDivider.setBackgroundColor(itemDividerColor);
                groupBG.addView(itemView);
                groupBG.addView(itemDivider);
            }
            groupBG.removeViewAt(groupBG.getChildCount() - 1);
            View groupDivider = new View(ctx);
            groupDivider.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,   groupDivderHeight * oneDP));
            groupDivider.setBackgroundColor(groupDividerColor);
            dialogBg.addView(groupBG);
            dialogBg.addView(groupDivider);
        }
        dialogBg.removeViewAt(dialogBg.getChildCount() - 1);
        dialog.setContentView(dialogBg);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();

    }

    private static float dip2px(Context ctx, float dip){
        final float scale = ctx.getResources().getDisplayMetrics().density;

        return dip * scale;
    }
    private static void recursiveSetOnClickListener(View view, final int pos, final OnItemClickListener listener, final BottomSheetDialog dialog) {
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = vg.getChildCount() - 1; i >= 0; i--) {
                recursiveSetOnClickListener(view, pos, listener, dialog);
            }
        } else {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v, pos, dialog);
                }
            });
        }

    }

    public interface OnItemClickListener {
        void onClick(View view, int pos, BottomSheetDialog dialog);
    }
}
