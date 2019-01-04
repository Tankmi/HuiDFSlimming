package com.huidf.slimming.fragment.ranking.item;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.huidf.slimming.R;
import com.huidf.slimming.activity.ranking_list.RankingListActivity;
import com.huidf.slimming.adapter.ranking.RankingAdapter;
import com.huidf.slimming.base.BaseFragmentForAnnotation;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.context.UrlConstant;
import com.huidf.slimming.entity.home.sport.SportHisEntity;
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

/**
 *
 * @author ZhuTao
 * @date 2018/12/14
 * @params  type, 1,运动，2，体重
*/

@ContentView(R.layout.fragment_ranking_item)
public class RankingListItemFragment extends BaseFragmentForAnnotation implements SwipeRecyclerView.OnSwipeRecyclerViewListener {

    private int mType;  //1,运动，2，体重

    private RecyclerView recycManager;
    private SpacesItemDecoration decor;
    private RankingAdapter mAdapter;
    private boolean isInit;

    @ViewInject(R.id.swiperec_ranking_list) private SwipeRecyclerView mSwipeRecyclerView;


    public RankingListItemFragment() {
        super();
    }

    @Override
    protected void initHead()
    {
        Bundle bundle = getArguments();
        mType = bundle.getInt("type",-1);
        LOG("data(1,运动，2，体重) type:  " + mType);
    }

    @Override
    protected void initContent() {  }

    @Override
    protected void initLogic() {
        initRecycler();
        if(!isInit)onRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private final int GETSPORTRANK = 10001, GETWEIGHTRANK = 10002;
    public void getRankingList() {
        RequestParams params = PreferenceEntity.getLoginParams();
        if(mType == 1)  mgetNetData.GetData(this, UrlConstant.API_SPORTRANK, GETSPORTRANK, params);
        else  mgetNetData.GetData(this, UrlConstant.API_LOSEWEIGHTRANK, GETWEIGHTRANK, params);
        if(!isInit)setLoading(true, "");
    }

    @Override
    public void paddingDatas(String mData, int type) {
        setLoading(false, "");
        Gson gson = new Gson();
        RankingEntity mTopicentity;
        try {
            mTopicentity = gson.fromJson(mData, RankingEntity.class);
        } catch (Exception e) {
            return;
        }
        if (mTopicentity.code == ContextConstant.RESPONSECODE_200) {
            isInit = true;
            if (type == GETSPORTRANK) {
                setSportOrWeightRank(mTopicentity.data, 1);
            } else if (type == GETWEIGHTRANK) {
                setSportOrWeightRank(mTopicentity.data, 2);
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

//    private void setWeightRank(RankingEntity.Data mEntity){
//        LinkedList<RankingEntity.Data.RankingInfo> mLists = new LinkedList<>();
//        mLists = formatData(mLists, mEntity.list2,2, 2, mEntity.rank2, mEntity.percent2 );
//        mLists = formatData(mLists, mEntity.list3,2,3, mEntity.rank3, mEntity.percent3 );
//
//        mAdapter.setListData(mLists);
//    }

    private void setSportOrWeightRank(RankingEntity.Data mEntity, int moduleType){
        LinkedList<RankingEntity.Data.RankingInfo> mLists = new LinkedList<>();
        mLists = formatData(mLists, mEntity.list1, moduleType, 1, mEntity.rank1, mEntity.time1 );
        mLists = formatData(mLists, mEntity.list2, moduleType, 2, mEntity.rank2, moduleType == 1? mEntity.time2: mEntity.percent2 );
        mLists = formatData(mLists, mEntity.list3, moduleType, 3, mEntity.rank3, moduleType == 1? mEntity.time3: mEntity.percent3 );

        mAdapter.setListData(mLists);
    }

    /**
     *
     * @param mLists
     * @param mListEntity
     * @param moduleType    1，运动，2，体重
     * @param type 1日排行   2周排行   3月排行
     * @param rank 默认表头值 排名
     * @param data 默认表头值 值
     * @return
     */
    private  LinkedList<RankingEntity.Data.RankingInfo> formatData(LinkedList<RankingEntity.Data.RankingInfo> mLists, List<RankingEntity.Data.RankingInfo> mListEntity, int moduleType, int type, int rank, int data){
        RankingEntity.Data.RankingInfo mData;
        if(mListEntity != null && mListEntity.size()>0){
            mData = getUserRankSportInfo(1,type, 0, 0);
            mLists.add(mData);
//            if (rank != 0){
                mData = getUserRankSportInfo(2,type, rank, data);
                mData.customerName = PreferencesUtils.getString(mContext, PreferenceEntity.KEY_USER_NICK);
                mData.customerHead = PreferencesUtils.getString(mContext, PreferenceEntity.KEY_USER_HEADER);
                mLists.add(mData);
//            }
            for (RankingEntity.Data.RankingInfo mDataEngity: mListEntity ) {
                mData = getUserRankSportInfo(mDataEngity,2,type, moduleType == 1?mDataEngity.time:mDataEngity.percent);
                mLists.add(mData);
            }
            if(mListEntity.size()>=5){
                mData = getUserRankSportInfo(3,type, 0, 0);
                mLists.add(mData);
            }
        }
        return mLists;
    }

    private RankingEntity.Data.RankingInfo getUserRankSportInfo(int targets,int type, int rank, int time){
        RankingEntity.Data.RankingInfo mData = new RankingEntity.Data.RankingInfo();
        mData.tragetType = mType;
        mData.targets = targets;    //首、普通值、尾 1 2 3
        mData.type = type;  // 1日排行   2周排行   3月排行
        mData.rank = rank;
        mData.data = time;
        return mData;
    }

    private RankingEntity.Data.RankingInfo getUserRankSportInfo( RankingEntity.Data.RankingInfo mData, int targets,int type, int time){
        mData.tragetType = mType;
        mData.targets = targets;    //首、普通值、尾 1 2 3
        mData.type = type;  // 1日排行   2周排行   3月排行
        mData.rank = mData.singleRank;
        mData.data = time;
        return mData;
    }

    @Override
    protected void initLocation() {

    }

    private void initRecycler(){
        LOG("initRecyclerinitRecycler  initRecycler");
        mSwipeRecyclerView.setOnSwipeRecyclerViewListener(this);
        mSwipeRecyclerView.isCancelLoadNext(true);
//        mSwipeRecyclerView.isCancelRefresh(true);

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(mContext);

        recycManager = mSwipeRecyclerView.getRecyclerView();
        recycManager.setLayoutManager(gridLayoutManager);

//        int width = mLayoutUtil.getWidgetWidth(4.5f, true);
//        decor = new SpacesItemDecoration(width, width);
        decor = new SpacesItemDecoration(0, 1,mContext.getResources().getColor(R.color.line_bg));
        recycManager.addItemDecoration(decor);

        LinkedList<RankingEntity.Data.RankingInfo> mList = new LinkedList<>();
        mAdapter = new RankingAdapter(mContext, mList);
        recycManager.setAdapter(mAdapter);
        mAdapter.setOnMovementClickListener(new RankingAdapter.onViewAllClickListener() {
            @Override
            public void onViewAllClick(RankingEntity.Data.RankingInfo mEntity)  //查看全部
            {
//                LOG("查看全部" + mType + ";type: " + mEntity.type);
                Intent intent = new Intent(getActivity(), RankingListActivity.class);
                intent.putExtra("type",mType);
                intent.putExtra("flag",mEntity.type);
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh()
    {
        LOG("onRefresh");
        getRankingList();
    }

    @Override
    public void onLoadNext()
    {
        LOG("onLoadNext");
    }

    @Override
    protected void pauseClose() { }

    @Override
    protected void destroyClose() {
    }


}
