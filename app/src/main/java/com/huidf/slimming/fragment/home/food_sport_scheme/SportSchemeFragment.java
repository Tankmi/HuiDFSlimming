package com.huidf.slimming.fragment.home.food_sport_scheme;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.huidf.slimming.R;
import com.huidf.slimming.adapter.home.food_sport_scheme.FoodSchemeAdapter;
import com.huidf.slimming.adapter.home.food_sport_scheme.SportSchemeAdapter;
import com.huidf.slimming.base.BaseFragmentForAnnotation;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.context.UrlConstant;
import com.huidf.slimming.entity.home.food_sport_scheme.FoodSchemeEntity;
import com.huidf.slimming.entity.home.food_sport_scheme.SportSchemeEntity;
import com.huidf.slimming.view.swiperecyclerview.SpacesItemDecoration;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import huitx.libztframework.context.ContextConstant;
import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.ToastUtils;

/**
 * 运动方案
 * @author ZhuTao
 * @date 2018/12/21
 * @params
*/

@ContentView(R.layout.fragment_sport_scheme)
public class SportSchemeFragment extends BaseFragmentForAnnotation {


    public SportSchemeFragment() {
        super();
    }

    @Override
    protected void initHead() {

    }

    @Override
    protected void initContent() {
        initRecycler();
    }

    @Override
    protected void initLogic() {
        
    }

    private boolean isInit;
    @Override
    public void onResume() {
        super.onResume();
        if(!isInit)initList();
    }

    private final int GETSPORTSCHEME = 10002;
    public void getSportScheme() {
        RequestParams params = PreferenceEntity.getLoginParams();
        mgetNetData.GetData(this, UrlConstant.API_SPORTPLAN, GETSPORTSCHEME, params);
    }


    @Override
    public void paddingDatas(String mData, int type) {
        setLoading(false, "");
        Gson gson = new Gson();
        SportSchemeEntity mTopicentity;
        try {
            mTopicentity = gson.fromJson(mData, SportSchemeEntity.class);
        } catch (Exception e) {
            return;
        }
        if (mTopicentity.code == ContextConstant.RESPONSECODE_200) {
            if (type == GETSPORTSCHEME) {
                isInit = true;
                editData(mTopicentity.data);
            }
        } else if (mTopicentity.code == ContextConstant.RESPONSECODE_310) {    //登录信息过时跳转到登录页
            reLoading();
        } else {
            ToastUtils.showToast(NewWidgetSetting.filtrationStringbuffer(mTopicentity.msg, "接口信息异常！"));
        }
    }

    @Override
    public void error(String msg, int type) {
        LOG(msg);
    }


    @Override
    protected void initLocation() {

    }

    private void editData(SportSchemeEntity.Data mDataEntity){
        List<SportSchemeEntity.Data.SportScheme> mLits = new ArrayList<>();
        for(SportSchemeEntity.Data.SportScheme mDatas: mDataEntity.list){
            SportSchemeEntity.Data.SportScheme mData = mDatas;
            mData.type = 1;
            mData.image = mListImg.get(mDatas.planId-1);
            mLits.add(mData);
        }
        if(mLits.size() > 0){
            SportSchemeEntity.Data.SportScheme mData = new SportSchemeEntity.Data.SportScheme();
            mData.type = 2;
            mData.key4 = NewWidgetSetting.filtrationStringbuffer(mDataEntity.key4,"--");
            mData.key5 = NewWidgetSetting.filtrationStringbuffer(mDataEntity.key5,"--");
            mData.restTime = "注：若BMI大于28，不建议进行前" + (mLits.size()) + "项运动（HIIT训练）";
            mLits.add(mData);
        }

        mAdapter.setListData(mLits);
    }

    List<Integer> mListImg;
    private void initList(){
        mListImg = new ArrayList<>();
        mListImg.add(R.drawable.hiit1);
        mListImg.add(R.drawable.hiit2);
        mListImg.add(R.drawable.hiit3);
        mListImg.add(R.drawable.hiit4);
        mListImg.add(R.drawable.hiit5);
        mListImg.add(R.drawable.hiit6);
        mListImg.add(R.drawable.hiit7);
        mListImg.add(R.drawable.hiit8);
        mListImg.add(R.drawable.hiit9);
        mListImg.add(R.drawable.hiit10);

         getSportScheme();
    }

    @ViewInject(R.id.rv_sport_scheme) private RecyclerView mRecyclerView;
    protected SpacesItemDecoration decor;
    private SportSchemeAdapter mAdapter;
    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks;


    private void initRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);

        mRecyclerView.setLayoutManager(linearLayoutManager);

        decor = new SpacesItemDecoration(mLayoutUtil.getWidgetWidth(20), mLayoutUtil.getWidgetHeight(12),mContext.getResources().getColor(R.color.transparency));
        mRecyclerView.addItemDecoration(decor);

        mAdapter = new SportSchemeAdapter(mContext,new ArrayList<SportSchemeEntity.Data.SportScheme>());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void pauseClose() { }

    @Override
    protected void destroyClose() {
    }


}
