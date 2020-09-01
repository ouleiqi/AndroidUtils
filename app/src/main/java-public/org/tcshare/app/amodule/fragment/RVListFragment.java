package org.tcshare.app.amodule.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tcshare.app.R;
import org.tcshare.app.amodule.adapter.RVListAdapter;
import org.tcshare.widgets.ItemDecorations;

import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Created by yuxiaohei on 2018/4/8.
 */

public class RVListFragment extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private RVListAdapter rvListAdapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rv_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeColors(Color.RED,Color.CYAN,Color.BLACK,Color.GREEN);
        recyclerView.addItemDecoration(ItemDecorations.vertical(getContext()).create());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rvListAdapter = new RVListAdapter<String>();
        recyclerView.setAdapter(rvListAdapter);
        swipeRefresh.setOnRefreshListener(onRefreshListener);

        return view;
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            swipeRefresh.setRefreshing(true);
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rvListAdapter.addDatas(new ArrayList(){
                        {
                            for(int i = 0; i < 10; i++) {
                                add("aaaaa" + new Random().nextInt());
                            }
                        }
                    });
                    swipeRefresh.setRefreshing(false);
                }
            }, 500);

        }

    };

}
