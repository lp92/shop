package com.ms.shop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ms.shop.Contants;
import com.ms.shop.R;
import com.ms.shop.WareDetailActivity;
import com.ms.shop.adapter.BaseAdapter;
import com.ms.shop.adapter.HWAdatper;
import com.ms.shop.bean.Wares;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;


public class HotFragment extends BaseFragment {

    //TODO 删
    int i = 1;
    List<Wares> sss = new ArrayList<>();
    ////////////////
    private HWAdatper mAdatper;

    @ViewInject(R.id.recyclerview)
    private RecyclerView mRecyclerView;

    @ViewInject(R.id.refresh_view)
    private MaterialRefreshLayout mRefreshLaout;


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hot, container, false);
    }

    @Override
    public void init() {
        //TODO hot热卖接口

        if (i == 1) {
            Gson gson = new Gson();
            List<Wares> o = gson.fromJson(StringEscapeUtils.unescapeJava(Contants.API.WARES_HOT1), new TypeToken<List<Wares>>() {
            }.getType());
            sss = o;
            mAdatper = new HWAdatper(getContext(), sss);
            mAdatper.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Wares wares = mAdatper.getItem(position);
                    Intent intent = new Intent(getActivity(), WareDetailActivity.class);
                    intent.putExtra(Contants.WARE, wares);
                    startActivity(intent);
                }
            });
            mRecyclerView.setAdapter(mAdatper);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            mRefreshLaout.setLoadMore(true);
            mRefreshLaout.setMaterialRefreshListener(new MaterialRefreshListener() {
                @Override
                public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                    mRefreshLaout.setLoadMore(true);

                    Gson gson = new Gson();
                    List<Wares> o = gson.fromJson(StringEscapeUtils.unescapeJava(Contants.API.WARES_HOT1), new TypeToken<List<Wares>>() {
                    }.getType());
                    mAdatper.refreshData(o);

                    mRefreshLaout.finishRefresh();
                }

                @Override
                public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                    i += 1;
                    if (i > 3) {
                        Toast.makeText(getActivity(), "无更多数据", Toast.LENGTH_LONG).show();
                        mRefreshLaout.finishRefreshLoadMore();
                        mRefreshLaout.setLoadMore(false);
                    } else {
                        Gson gson = new Gson();
                        List<Wares> o = gson.fromJson(StringEscapeUtils.unescapeJava(Contants.API.WARES_HOT2), new TypeToken<List<Wares>>() {
                        }.getType());
                        mAdatper.addData(o);
                        mRefreshLaout.finishRefreshLoadMore();
                    }
                }
            });
        }


/*********************************/
//       Pager pager = Pager.newBuilder()
//                .setUrl(Contants.API.WARES_HOT)
//                .setLoadMore(true)
//                .setOnPageListener(this)
//                .setPageSize(20)
//                .setRefreshLayout(mRefreshLaout)
//                .build(getContext(), new TypeToken<Page<Wares>>() {
//                }.getType());
//        pager.request();
//        implements Pager.OnPageListener<Wares>
    }


//    @Override
//    public void load(List<Wares> datas, int totalPage, int totalCount) {
//
//        mAdatper = new HWAdatper(getContext(), datas);
//
//        mAdatper.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//                Wares wares = mAdatper.getItem(position);
//
//                Intent intent = new Intent(getActivity(), WareDetailActivity.class);
//
//                intent.putExtra(Contants.WARE, wares);
//                startActivity(intent);
//
//
//            }
//        });
//
//
//        mRecyclerView.setAdapter(mAdatper);
//
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
////        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL_LIST));
//    }
//
//    @Override
//    public void refresh(List<Wares> datas, int totalPage, int totalCount) {
//        mAdatper.refreshData(datas);
//        mRecyclerView.scrollToPosition(0);
//    }
//
//    @Override
//    public void loadMore(List<Wares> datas, int totalPage, int totalCount) {
//        mAdatper.loadMoreData(datas);
//        mRecyclerView.scrollToPosition(mAdatper.getDatas().size());
//    }
}
