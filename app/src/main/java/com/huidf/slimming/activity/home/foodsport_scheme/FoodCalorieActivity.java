package com.huidf.slimming.activity.home.foodsport_scheme;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huidf.slimming.R;
import com.huidf.slimming.adapter.home.food_sport_scheme.FoodCalorieAdapter;
import com.huidf.slimming.base.BaseFragmentActivityForAnnotation;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.context.UrlConstant;
import com.huidf.slimming.entity.home.food_sport_scheme.FoodCalorieEntity;
import com.huidf.slimming.view.swiperecyclerview.SpacesItemDecoration;
import com.huidf.slimming.view.swiperecyclerview.SwipeRecyclerView;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import huitx.libztframework.context.ContextConstant;
import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.ToastUtils;

/**
 * 食物卡路里检索
 *
 * @author ZhuTao
 * @date 2018/12/21
 * @params
 */

@ContentView(R.layout.activity_food_calorie)
public class FoodCalorieActivity extends BaseFragmentActivityForAnnotation implements SwipeRecyclerView.OnSwipeRecyclerViewListener {

    @ViewInject(R.id.et_food_calorie_search)
    EditText mSearchView;
    @ViewInject(R.id.tv_food_calorie_search)
    TextView tv_food_calorie_search;

    public FoodCalorieActivity()
    {
        super();
    }

    @Override
    protected void initHead()
    {
        setStatusBarColor(false, true, mContext.getResources().getColor(R.color.white));
        setTittle("食物能量浏览表");
    }

    /** 搜索条目时显示，其他情况不显示 */
    private boolean isShowLoading;

    @Event(value = {R.id.tv_food_calorie_search})
    private void getEvent(View view)
    {//必须用private进行修饰,否则无效
        switch (view.getId()) {
            case R.id.tv_food_calorie_search:  //搜索
                search();
                break;
        }
    }

    private void search()
    {
//        if (mSearchView.getText().toString().length() <= 0) {
//            onRefresh();
//            this.mSearchText="";
//        } else {
            this.mSearchText = mSearchView.getText().toString();
            isShowLoading = true;
            onRefresh();
//        }
    }

    @Override
    protected void initContent()
    {
        initRecycler();
    }

    @Override
    protected void initLogic()
    {
        onRefresh();

        mSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        search();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    private final int GETSPORTSCHEME = 10003;
    private String mSearchText;
    private int current = 1;
    private final int rowSize = 20;

    public void getFoodCalorie()
    {
        RequestParams params = PreferenceEntity.getLoginParams();
        params.addBodyParameter("name", mSearchText);
        params.addBodyParameter("current", current + "");
        params.addBodyParameter("rowSize", rowSize + "");
        mgetNetData.GetData(this, UrlConstant.API_ENERGYLIST, GETSPORTSCHEME, params);
        if (isShowLoading) setLoading(true, "");
        isShowLoading = false;
    }


    @Override
    public void paddingDatas(String mData, int type)
    {
        setLoading(false, "");
        Gson gson = new Gson();
        FoodCalorieEntity mTopicentity;
        try {
            mTopicentity = gson.fromJson(mData, FoodCalorieEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
            if (isLoadMore) current--;
            return;
        }
        if (mTopicentity.code == ContextConstant.RESPONSECODE_200) {
            mSwipeRecyclerView.onLoadFinish();
            if (mTopicentity.data.list != null && mTopicentity.data.list.size() >= 20)
                mSwipeRecyclerView.isCancelLoadNext(false);
            else mSwipeRecyclerView.isCancelLoadNext(true);
            if (type == GETSPORTSCHEME) {
                if(isLoadMore){
                    isLoadMoreSuccess = true;
                    mAdapter.setLoadNextData(mTopicentity.data.list);
                }
                else mAdapter.setListData(mTopicentity.data.list);
            }
        } else if (mTopicentity.code == ContextConstant.RESPONSECODE_310) {    //登录信息过时跳转到登录页
            reLoading();
        } else {
            ToastUtils.showToast(NewWidgetSetting.filtrationStringbuffer(mTopicentity.msg, "接口信息异常！"));
        }
    }

    @Override
    public void error(String msg, int type)
    {
        super.error(msg,type);
        LOG(msg);
        mSwipeRecyclerView.onLoadFinish();
        if (isLoadMore && !isLoadMoreSuccess) current--;
    }


    @Override
    protected void initLocation()
    {

    }

    @ViewInject(R.id.swiperec_food_calorie)
    private SwipeRecyclerView mSwipeRecyclerView;
    private RecyclerView recycManager;
    protected SpacesItemDecoration decor;
    private FoodCalorieAdapter mAdapter;
    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks;


    private void initRecycler()
    {
        mSwipeRecyclerView.setOnSwipeRecyclerViewListener(this);
        mSwipeRecyclerView.isCancelRefresh(false);
        mSwipeRecyclerView.isCancelLoadNext(true);

        recycManager = mSwipeRecyclerView.getRecyclerView();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recycManager.setLayoutManager(linearLayoutManager);

        decor = new SpacesItemDecoration(0, 1, mContext.getResources().getColor(R.color.line_bg));
        recycManager.addItemDecoration(decor);

        mAdapter = new FoodCalorieAdapter(mContext, new ArrayList<FoodCalorieEntity.Data.FoodCalorie>());
        recycManager.setAdapter(mAdapter);
    }


    @Override
    public void onRefresh()
    {
        LOG("onRefresh");
        isLoadMore = false;
        current = 1;
        getFoodCalorie();
    }

    private boolean isLoadMore;
    private boolean isLoadMoreSuccess = false;

    @Override
    public void onLoadNext()
    {
        isLoadMore = true;
        isLoadMoreSuccess = false;
        current++;
        LOG("onLoadNext");
        getFoodCalorie();
    }

    @Override
    protected void pauseClose()
    {
    }

    @Override
    protected void destroyClose()
    {
    }


}
