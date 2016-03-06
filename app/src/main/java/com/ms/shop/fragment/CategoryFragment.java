package com.ms.shop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ms.shop.Contants;
import com.ms.shop.R;
import com.ms.shop.WareDetailActivity;
import com.ms.shop.adapter.BaseAdapter;
import com.ms.shop.adapter.CategoryAdapter;
import com.ms.shop.adapter.WaresAdapter;
import com.ms.shop.adapter.decoration.DividerItemDecoration;
import com.ms.shop.bean.Banner;
import com.ms.shop.bean.Category;
import com.ms.shop.bean.Page;
import com.ms.shop.bean.Wares;
import com.ms.shop.http.BaseCallback;
import com.ms.shop.http.OkHttpHelper;
import com.ms.shop.http.SpotsCallBack;
import com.ms.shop.utils.ToastUtils;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.List;


public class CategoryFragment extends BaseFragment {


    @ViewInject(R.id.recyclerview_category)
    private RecyclerView mRecyclerView;


    @ViewInject(R.id.recyclerview_wares)
    private RecyclerView mRecyclerviewWares;

    @ViewInject(R.id.refresh_layout)
    private MaterialRefreshLayout mRefreshLaout;

    @ViewInject(R.id.slider)
    private SliderLayout mSliderLayout;

    private CategoryAdapter mCategoryAdapter;
    private WaresAdapter mWaresAdatper;


    private OkHttpHelper mHttpHelper = OkHttpHelper.getInstance();


    private int currPage = 1;
    private int totalPage = 1;
    private int pageSize = 10;
    private long category_id = 0;


    private static final int STATE_NORMAL = 0;
    private static final int STATE_REFREH = 1;
    private static final int STATE_MORE = 2;

    private int state = STATE_NORMAL;


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void init() {
        //TODO 类别页面广告轮播
        Gson gson = new Gson();
        List<Banner> o = gson.fromJson(StringEscapeUtils.unescapeJava(Contants.API.mcBANNER), new TypeToken<List<Banner>>() {
        }.getType());
        showSliderViews(o);
        //TODO 下拉刷新
        List<Wares> ox = gson.fromJson(StringEscapeUtils.unescapeJava(Contants.API.mClassList), new TypeToken<List<Wares>>() {
        }.getType());
        mWaresAdatper = new WaresAdapter(getContext(), ox);
        mWaresAdatper.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Wares wares = mWaresAdatper.getItem(position);

                Intent intent = new Intent(getActivity(), WareDetailActivity.class);

                intent.putExtra(Contants.WARE, wares);
                startActivity(intent);

            }
        });

        mRecyclerviewWares.setAdapter(mWaresAdatper);

        mRecyclerviewWares.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerviewWares.setItemAnimator(new DefaultItemAnimator());
        mRefreshLaout.setLoadMore(true);
        mRefreshLaout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                currPage = 1;
                state = STATE_REFREH;
                requestWares(category_id);
                //TODO 商品分类对应的商品列表
                Gson gson = new Gson();
                List<Wares> ox = gson.fromJson(StringEscapeUtils.unescapeJava(Contants.API.mClassList), new TypeToken<List<Wares>>() {
                }.getType());
                mWaresAdatper.refreshData(ox);

            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                ToastUtils.show(getActivity(), "没有更多数据");
                mRefreshLaout.finishRefreshLoadMore();
            }
        });
//TODO 左侧分类列表
        List<Category> oo = gson.fromJson(StringEscapeUtils.unescapeJava(Contants.API.mCATEGORY_LIST), new TypeToken<List<Category>>() {
        }.getType());
        mCategoryAdapter = new CategoryAdapter(getContext(), oo);

        mCategoryAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Gson gson = new Gson();
                List<Wares> ox = gson.fromJson(StringEscapeUtils.unescapeJava(Contants.API.mClassList), new TypeToken<List<Wares>>() {
                }.getType());
                mWaresAdatper.refreshData(ox);

            }
        });

        mRecyclerView.setAdapter(mCategoryAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        /****************/
        // requestCategoryData();
        //  requestBannerData();
        // initRefreshLayout();
    }


    private void initRefreshLayout() {

        mRefreshLaout.setLoadMore(true);
        mRefreshLaout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {

                refreshData();

            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {

                if (currPage <= totalPage)
                    loadMoreData();
                else {
//                    Toast.makeText()
                    mRefreshLaout.finishRefreshLoadMore();
                }
            }
        });
    }


    private void refreshData() {

        currPage = 1;

        state = STATE_REFREH;
        requestWares(category_id);

    }

    private void loadMoreData() {

        currPage = ++currPage;
        state = STATE_MORE;
        requestWares(category_id);

    }


    private void requestCategoryData() {

//
//        mHttpHelper.get(Contants.API.CATEGORY_LIST, new SpotsCallBack<List<Category>>(getContext()) {
//
//
//            @Override
//            public void onSuccess(Response response, List<Category> categories) {
//
//                showCategoryData(categories);
//
//                if (categories != null && categories.size() > 0)
//                    category_id = categories.get(0).getId();
//                requestWares(category_id);
//            }
//
//            @Override
//            public void onError(Response response, int code, Exception e) {
//
//            }
//        });
//
  }

    private void showCategoryData(List<Category> categories) {


        mCategoryAdapter = new CategoryAdapter(getContext(), categories);

        mCategoryAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Category category = mCategoryAdapter.getItem(position);

                category_id = category.getId();
                currPage = 1;
                state = STATE_NORMAL;

                requestWares(category_id);


            }
        });

        mRecyclerView.setAdapter(mCategoryAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));


    }


    private void requestBannerData() {
//
//
//        String url = Contants.API.BANNER + "?type=1";
//
//        mHttpHelper.get(url, new SpotsCallBack<List<Banner>>(getContext()) {
//
//
//            @Override
//            public void onSuccess(Response response, List<Banner> banners) {
//
//                showSliderViews(banners);
//            }
//
//            @Override
//            public void onError(Response response, int code, Exception e) {
//
//            }
//        });

    }


    private void showSliderViews(List<Banner> banners) {


        if (banners != null) {

            for (Banner banner : banners) {


                DefaultSliderView sliderView = new DefaultSliderView(this.getActivity());
                sliderView.image(banner.getImgUrl());
                sliderView.description(banner.getName());
                sliderView.setScaleType(BaseSliderView.ScaleType.Fit);
                mSliderLayout.addSlider(sliderView);

            }
        }


        mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);

        mSliderLayout.setCustomAnimation(new DescriptionAnimation());
        mSliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
        mSliderLayout.setDuration(3000);


    }


    private void requestWares(long categoryId) {

//        String url = Contants.API.WARES_LIST + "?categoryId=" + categoryId + "&curPage=" + currPage + "&pageSize=" + pageSize;
//
//        mHttpHelper.get(url, new BaseCallback<Page<Wares>>() {
//            @Override
//            public void onBeforeRequest(Request request) {
//
//            }
//
//            @Override
//            public void onFailure(Request request, Exception e) {
//
//            }
//
//            @Override
//            public void onResponse(Response response) {
//
//            }
//
//            @Override
//            public void onSuccess(Response response, Page<Wares> waresPage) {
//
//
//               currPage = waresPage.getCurrentPage();
//             totalPage = waresPage.getTotalPage();
//
//                showWaresData(waresPage.getList());
//
//
//            }
//
//
//            @Override
//            public void onError(Response response, int code, Exception e) {
//
//            }
//        });

    }


    private void showWaresData(List<Wares> wares) {


        switch (state) {

            case STATE_NORMAL:

                if (mWaresAdatper == null) {
                    mWaresAdatper = new WaresAdapter(getContext(), wares);
                    mWaresAdatper.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Wares wares = mWaresAdatper.getItem(position);

                            Intent intent = new Intent(getActivity(), WareDetailActivity.class);

                            intent.putExtra(Contants.WARE, wares);
                            startActivity(intent);

                        }
                    });

                    mRecyclerviewWares.setAdapter(mWaresAdatper);

                    mRecyclerviewWares.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    mRecyclerviewWares.setItemAnimator(new DefaultItemAnimator());
//                    mRecyclerviewWares.addItemDecoration(new DividerGridItemDecoration(getContext()));
                } else {
                    mWaresAdatper.clear();
                    mWaresAdatper.addData(wares);
                }


                break;

            case STATE_REFREH:
                mWaresAdatper.clear();
                mWaresAdatper.addData(wares);

                mRecyclerviewWares.scrollToPosition(0);
                mRefreshLaout.finishRefresh();
                break;

            case STATE_MORE:
                mWaresAdatper.addData(mWaresAdatper.getDatas().size(), wares);
                mRecyclerviewWares.scrollToPosition(mWaresAdatper.getDatas().size());
                mRefreshLaout.finishRefreshLoadMore();
                break;


        }


    }


}



