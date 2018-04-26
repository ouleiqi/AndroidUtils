package org.tcshare.app.amodule.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tcshare.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuxiaohei on 2018/4/8.
 */

public class RVListAdapter<T> extends RecyclerView.Adapter<RVListAdapter.ViewHolder> {
    private List<T> datas = new ArrayList<>();

    @NonNull
    @Override
    public RVListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rvlist, null, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVListAdapter.ViewHolder holder, int position) {
        holder.textView.setText(String.valueOf(datas.get(position)));
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void setDatas(List<T> datas){
        this.datas.clear();
        addDatas(datas);
    }
    public void addDatas(List<T> datas){
        this.datas.addAll(datas);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}
