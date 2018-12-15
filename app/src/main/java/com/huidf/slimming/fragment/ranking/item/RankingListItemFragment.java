package com.huidf.slimming.fragment.ranking.item;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huidf.slimming.R;
import com.huidf.slimming.adapter.sel_photo.RecycleViewSelectPhotoAdapter;
import com.huidf.slimming.base.BaseFragmentForAnnotation;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.fragment.today_movement.add.TodayMovementAddFragment;
import com.huidf.slimming.fragment.today_movement.running.TodayMovementRunningFragment;
import com.huidf.slimming.view.swiperecyclerview.SpacesItemDecoration;
import com.huidf.slimming.view.swiperecyclerview.SwipeRecyclerView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import huitx.libztframework.view.FragmentSwitchTool;

/**
 *
 * @author ZhuTao
 * @date 2018/12/14
 * @params  type, 1,运动，2，体重
*/

@ContentView(R.layout.fragment_ranking_item)
public class RankingListItemFragment extends BaseFragmentForAnnotation implements SwipeRecyclerView.OnSwipeRecyclerViewListener {

    private int mType;

    @ViewInject(R.id.swiperec_ranking_list) private SwipeRecyclerView mSwipeRecyclerView;


    public RankingListItemFragment() {
        super();
    }

    @Override
    protected void initHead() {

    }

    @Override
    public void onRefresh()
    {

    }

    @Override
    public void onLoadNext()
    {

    }

    @Override
    protected void initContent() {

    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void paddingDatas(String mData, int type) {

    }

    @Override
    public void error(String msg, int type) {
        LOG(msg);
    }

    @Override
    protected void initLocation() {

    }

    private RecyclerView recycManager;
    private SpacesItemDecoration decor;

    @Override
    protected void initLogic() {
        mSwipeRecyclerView.setOnSwipeRecyclerViewListener(this);
        mSwipeRecyclerView.isCancelLoadNext(true);
        mSwipeRecyclerView.isCancelRefresh(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);

        recycManager = mSwipeRecyclerView.getRecyclerView();
        recycManager.setLayoutManager(gridLayoutManager);

        int width = mLayoutUtil.getWidgetWidth(4.5f, true);
        decor = new SpacesItemDecoration(width, width);
        recycManager.addItemDecoration(decor);
//        mAdapter = new RecycleViewSelectPhotoAdapter(mContext, photoInfoList,1, true);
//        recycManager.setAdapter(mAdapter);
    }

    @Override
    protected void pauseClose() { }

    @Override
    protected void destroyClose() {
    }


}
