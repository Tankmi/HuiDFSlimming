package com.huidf.slimming.fragment.home.food_sport_scheme;

import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.huidf.slimming.R;
import com.huidf.slimming.activity.home.foodsport_scheme.FoodCalorieActivity;
import com.huidf.slimming.activity.user.perfect_info.PerfectInfoActivity;
import com.huidf.slimming.adapter.home.food_sport_scheme.FoodSchemeAdapter;
import com.huidf.slimming.base.BaseFragmentForAnnotation;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.context.UrlConstant;
import com.huidf.slimming.entity.home.food_sport_scheme.FoodSchemeEntity;
import com.huidf.slimming.view.swiperecyclerview.SpacesItemDecoration;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import java.util.ArrayList;
import huitx.libztframework.context.ContextConstant;
import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.ToastUtils;

/**
 * 饮食方案
 * @author ZhuTao
 * @date 2018/12/21
 * @params
*/

@ContentView(R.layout.fragment_food_scheme)
public class FoodSchemeFragment extends BaseFragmentForAnnotation {

    @ViewInject(R.id.btn_food_scheme) private Button btn_food_scheme;
    public FoodSchemeFragment() {
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
        getFoodScheme();
    }

    @Event(value = {R.id.btn_food_scheme})
    private void getEvent(View view)
    {//必须用private进行修饰,否则无效

        switch (view.getId()) {
            case R.id.btn_food_scheme:  //查看食物能量浏览表
                Intent intent = new Intent(getActivity(), FoodCalorieActivity.class);
                startActivity(intent);
                break;
        }
    }

    private boolean isInit;
    @Override
    public void onResume() {
        super.onResume();
        if(!isInit) getFoodScheme();
    }

    private final int GETFOODSCHEME = 10001;
    public void getFoodScheme() {
//        setLoading(true, "");
        RequestParams params = PreferenceEntity.getLoginParams();
        mgetNetData.GetData(this, UrlConstant.API_FOODPLAN, GETFOODSCHEME, params);
    }

    @Override
    public void paddingDatas(String mData, int type) {
        setLoading(false, "");
        Gson gson = new Gson();
        FoodSchemeEntity mTopicentity;
        try {
            mTopicentity = gson.fromJson(mData, FoodSchemeEntity.class);
        } catch (Exception e) {
            return;
        }
        if (mTopicentity.code == ContextConstant.RESPONSECODE_200) {
            if (type == GETFOODSCHEME) {
                isInit = true;
                mAdapter.setListData(mTopicentity.data.list);
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


    @ViewInject(R.id.rv_food_scheme) private RecyclerView mRecyclerView;
    protected SpacesItemDecoration decor;
    private FoodSchemeAdapter mAdapter;
    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks;


    private void initRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);

        mRecyclerView.setLayoutManager(linearLayoutManager);

        decor = new SpacesItemDecoration(mLayoutUtil.getWidgetWidth(20), mLayoutUtil.getWidgetHeight(18),mContext.getResources().getColor(R.color.transparency));
        mRecyclerView.addItemDecoration(decor);

        mAdapter = new FoodSchemeAdapter(mContext,new ArrayList<FoodSchemeEntity.Data.FoodScheme>());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void pauseClose() { }

    @Override
    protected void destroyClose() {
    }


}
