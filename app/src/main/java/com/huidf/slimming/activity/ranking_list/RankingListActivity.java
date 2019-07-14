package com.huidf.slimming.activity.ranking_list;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.huidf.slimming.R;
import com.huidf.slimming.adapter.ranking.RankingAdapter;
import com.huidf.slimming.base.BaseFragmentActivityForAnnotation;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.context.UrlConstant;
import com.huidf.slimming.entity.ranking.RankingEntity;
import com.huidf.slimming.view.swiperecyclerview.SpacesItemDecoration;
import com.huidf.slimming.view.swiperecyclerview.SwipeRecyclerView;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.LinkedList;
import java.util.List;

import huitx.libztframework.context.ContextConstant;
import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.PreferencesUtils;
import huitx.libztframework.utils.ToastUtils;
import huitx.libztframework.utils.WidgetSetting;

/**
 * @author ZhuTao
 * @date 2018/12/14
 * @params type, 1,运动，2，体重
 */

@ContentView(R.layout.activity_ranking)
public class RankingListActivity extends BaseFragmentActivityForAnnotation implements SwipeRecyclerView.OnSwipeRecyclerViewListener {

    private int mType;  //1,运动，2，体重
    private int flag;  //1日排行   2周排行   3月排行
    private int current = 1;
    private final int rowSize = 20;

    @ViewInject(R.id.swiperec_ranking_list)
    private SwipeRecyclerView mSwipeRecyclerView;


    public RankingListActivity()
    {
        super();
    }

    @Override
    protected void initHead()
    {
        //branch
        setStatusBarColor(false, true, mContext.getResources().getColor(R.color.weight_main_color));
        mBtnLeft.setBackgroundResource(R.drawable.btn_back_white);
        setTitleBackgroudColor(R.color.weight_main_color);
        setHideTitleLine();

        mType = getIntent().getIntExtra("type", -1);
        flag = getIntent().getIntExtra("flag", -1);
        setTittle((flag == 1 ? "日" : flag == 2 ? "周" : "月") + "排行", R.color.white);
        LOG("data(1,运动，2，体重) type:  " + mType + " ;flag:  " + flag);

    }

    @Override
    protected void initContent()
    {

    }

    @Override
    public void onResume()
    {
        super.onResume();
        onRefresh();
    }

    private final int GETSPORTRANK = 10001, GETWEIGHTRANK = 10002;

    public void getRankingList()
    {
        RequestParams params = PreferenceEntity.getLoginParams();
        params.addBodyParameter("current", current + "");
        params.addBodyParameter("rowSize", rowSize + "");
        params.addBodyParameter("flag", flag + "");
        if (mType == 1)
            mgetNetData.GetData(this, UrlConstant.API_SPORTSRANKALL, GETSPORTRANK, params);
        else mgetNetData.GetData(this, UrlConstant.API_LOSEWEIGHTRANKALL, GETWEIGHTRANK, params);
//        setLoading(true, "");
    }

    @Override
    public void paddingDatas(String mData, int type)
    {
        setLoading(false, "");
        Gson gson = new Gson();
        RankingEntity mTopicentity;
        try {
            mTopicentity = gson.fromJson(mData, RankingEntity.class);
        } catch (Exception e) {
            if (isLoadMore) current--;
            return;
        }
        if (mTopicentity.code == ContextConstant.RESPONSECODE_200) {
            mSwipeRecyclerView.onLoadFinish();
            if (mTopicentity.data.list != null && mTopicentity.data.list.size() >= 20)
                mSwipeRecyclerView.isCancelLoadNext(false);
            else mSwipeRecyclerView.isCancelLoadNext(true);

            if (type == GETSPORTRANK) {
                setSportOrWeightRank(mTopicentity.data, 1,isLoadMore);
            } else if (type == GETWEIGHTRANK) {
                setSportOrWeightRank(mTopicentity.data, 2,isLoadMore);
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
        super.error(msg, type);
        LOG(msg);
        if (isLoadMore && !isLoadMoreSuccess) current--;
    }

    private void setSportOrWeightRank(RankingEntity.Data mEntity, int moduleType,boolean isLoadMore)
    {
        LinkedList<RankingEntity.Data.RankingInfo> mLists = new LinkedList<>();
        mLists = formatData(mLists, mEntity.list, moduleType, 1, mEntity.rank1, mEntity.time1);
        if(isLoadMore){
            isLoadMoreSuccess = true;
            mAdapter.setLoadNextData(mLists);
        }
        else  mAdapter.setListData(mLists);
    }

    private LinkedList<RankingEntity.Data.RankingInfo> formatData(LinkedList<RankingEntity.Data.RankingInfo> mLists, List<RankingEntity.Data.RankingInfo> mList, int moduleType, int type, int rank, int data)
    {
        RankingEntity.Data.RankingInfo mData;
        if (mList != null && mList.size() > 0) {
            for (RankingEntity.Data.RankingInfo mDataEngity : mList) {
                mData = getUserRankSportInfo(mDataEngity, 2, type, moduleType == 1 ? mDataEngity.time : mDataEngity.percent);
                mLists.add(mData);
            }
        }
        return mLists;
    }

    private RankingEntity.Data.RankingInfo getUserRankSportInfo(RankingEntity.Data.RankingInfo mData, int targets, int type, int time)
    {
        mData.tragetType = mType;
        mData.targets = targets;    //首、普通值、尾 1 2 3
        mData.type = type;  // 1日排行   2周排行   3月排行
        mData.rank = mData.singleRank;
        mData.data = time;
        return mData;
    }

    @Override
    protected void initLocation()
    {

    }

    private RecyclerView recycManager;
    private SpacesItemDecoration decor;
    private RankingAdapter mAdapter;

    @Override
    protected void initLogic()
    {
        mSwipeRecyclerView.setOnSwipeRecyclerViewListener(this);
        mSwipeRecyclerView.isCancelLoadNext(false);
        mSwipeRecyclerView.isCancelRefresh(false);

        recycManager = mSwipeRecyclerView.getRecyclerView();
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(mContext);
        recycManager.setLayoutManager(gridLayoutManager);

//        int width = mLayoutUtil.getWidgetWidth(4.5f, true);
//        decor = new SpacesItemDecoration(width, width);
        decor = new SpacesItemDecoration(0, 1, mContext.getResources().getColor(R.color.line_bg));
        recycManager.addItemDecoration(decor);

        LinkedList<RankingEntity.Data.RankingInfo> mList = new LinkedList<>();
        mAdapter = new RankingAdapter(mContext, mList);
        recycManager.setAdapter(mAdapter);
        mAdapter.setOnMovementClickListener(new RankingAdapter.onViewAllClickListener() {
            @Override
            public void onViewAllClick(RankingEntity.Data.RankingInfo mEntity)  //查看全部
            {
                LOG("查看全部");
            }
        });
    }

    @Override
    public void onRefresh()
    {
        isLoadMore = false;
        current = 1;
        getRankingList();
    }

    private boolean isLoadMore;
    private boolean isLoadMoreSuccess = false;

    @Override
    public void onLoadNext()
    {
        isLoadMoreSuccess = false;
        isLoadMore = true;
        current++;
        getRankingList();
        LOG("onLoadNext");
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
