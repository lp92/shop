package com.ms.shop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ms.shop.Contants;
import com.ms.shop.R;
import com.ms.shop.WareListActivity;
import com.ms.shop.adapter.HomeCatgoryAdapter;
import com.ms.shop.adapter.decoration.CardViewtemDecortion;
import com.ms.shop.bean.Banner;
import com.ms.shop.bean.Campaign;
import com.ms.shop.bean.HomeCampaign;
import com.ms.shop.http.OkHttpHelper;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.List;


public class HomeFragment extends BaseFragment {


    @ViewInject(R.id.slider)
    private SliderLayout mSliderLayout;


    @ViewInject(R.id.recyclerview)
    private RecyclerView mRecyclerView;

    private HomeCatgoryAdapter mAdatper;


    private static final String TAG = "HomeFragment";


    private Gson mGson = new Gson();

    private List<Banner> mBanner;


    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void init() {

        requestImages();

        initRecyclerView();
    }


    private void requestImages() {
        //TODO 首页图片轮播接口
        Gson gson = new Gson();
        List<Banner> o = gson.fromJson(StringEscapeUtils.unescapeJava(Contants.API.mBANNER), new TypeToken<List<Banner>>() {
        }.getType());
        mBanner = o;
        initSlider();
        /***********************************************/
//        httpHelper.get(Contants.API.BANNER, new SpotsCallBack<List<Banner>>(getContext()) {
//
//
//            @Override
//            public void onSuccess(Response response, List<Banner> banners) {
//
//                mBanner = banners;
//                initSlider();
//            }
//
//            @Override
//            public void onError(Response response, int code, Exception e) {
//
//            }
//        });


    }


    private void initRecyclerView() {
        // TODO 首页商品展示
        Gson gson = new Gson();
        List<HomeCampaign> o = gson.fromJson(StringEscapeUtils.unescapeJava(Contants.API.mCAMPAIGN_HOME), new TypeToken<List<HomeCampaign>>() {
        }.getType());
        initData(o);

        /************************************/
//        httpHelper.get(Contants.API.CAMPAIGN_HOME, new BaseCallback<List<HomeCampaign>>() {
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
//            public void onSuccess(Response response, List<HomeCampaign> homeCampaigns) {
//
//                initData(homeCampaigns);
//            }
//
//
//            @Override
//            public void onError(Response response, int code, Exception e) {
//
//            }
//        });

    }


    private void initData(List<HomeCampaign> homeCampaigns) {


        mAdatper = new HomeCatgoryAdapter(homeCampaigns, getActivity());

        mAdatper.setOnCampaignClickListener(new HomeCatgoryAdapter.OnCampaignClickListener() {
            @Override
            public void onClick(View view, Campaign campaign) {


                Intent intent = new Intent(getActivity(), WareListActivity.class);
                intent.putExtra(Contants.COMPAINGAIN_ID, campaign.getId());

                startActivity(intent);


            }
        });

        mRecyclerView.setAdapter(mAdatper);

        mRecyclerView.addItemDecoration(new CardViewtemDecortion());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
    }


    private void initSlider() {


        if (mBanner != null) {

            for (Banner banner : mBanner) {


                TextSliderView textSliderView = new TextSliderView(this.getActivity());
                textSliderView.image(banner.getImgUrl());
                textSliderView.description(banner.getName());
                textSliderView.setScaleType(BaseSliderView.ScaleType.Fit);
                mSliderLayout.addSlider(textSliderView);

            }
        }


        mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);

        mSliderLayout.setCustomAnimation(new DescriptionAnimation());
        mSliderLayout.setPresetTransformer(SliderLayout.Transformer.RotateUp);
        mSliderLayout.setDuration(3000);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        mSliderLayout.stopAutoCycle();
    }
}
