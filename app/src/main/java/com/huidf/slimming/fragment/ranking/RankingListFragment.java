package com.huidf.slimming.fragment.ranking;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huidf.slimming.R;
import com.huidf.slimming.activity.toady_movement.TodayMovementActivity;
import com.huidf.slimming.activity.toady_movement.TodayMovementBaseActivity;
import com.huidf.slimming.base.BaseFragmentActivity;
import com.huidf.slimming.base.BaseFragmentForAnnotation;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.fragment.ranking.item.RankingListItemFragment;
import com.huidf.slimming.fragment.today_movement.add.TodayMovementAddFragment;
import com.huidf.slimming.fragment.today_movement.running.TodayMovementRunningFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.lang.ref.WeakReference;

import huitx.libztframework.view.FragmentSwitchTool;

@ContentView(R.layout.fragment_ranking)
public class RankingListFragment extends BaseFragmentForAnnotation implements OnClickListener, DialogInterface.OnDismissListener{

    @ViewInject(R.id.lin_tab_ranking_list) private LinearLayout lin_tab_today_movement;
    @ViewInject(R.id.rel_trl_sport) private RelativeLayout rel_trl_sport;
    @ViewInject(R.id.tv_trl_sport) private TextView tv_ttm_add;
    @ViewInject(R.id.iv_trl_sport) private ImageView iv_ttm_add;
    @ViewInject(R.id.rel_trl_weight) private RelativeLayout rel_trl_weight;
    @ViewInject(R.id.tv_trl_weight)private TextView tv_ttm_running;
    @ViewInject(R.id.iv_trl_weight) private ImageView iv_ttm_running;


    protected FragmentSwitchTool mFragmentSwitch;
    protected RankingListItemFragment weightFrag,sportFrag;

    public RankingListFragment() {
        super();
    }

    @Override
    protected void initHead() {
    }

    //
    @Override
    protected void initContent() {

    }

    @Override
    public void onResume() {
        super.onResume();
        initFragment();
    }

    private void initFragment() {
        sportFrag = new RankingListItemFragment();
        weightFrag = new RankingListItemFragment();
//        sportFrag = new RankingListItemFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type",1);//这里的values就是我们要传的值
//        sportFrag.setArguments(bundle);
//        weightFrag =  new RankingListItemFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt("type",2);//这里的values就是我们要传的值
//        weightFrag.setArguments(bundle1);

        mFragmentSwitch = new FragmentSwitchTool(getChildFragmentManager(), R.id.fl_ranking_list);
        mFragmentSwitch.setClickableViews(rel_trl_sport, rel_trl_weight);
        mFragmentSwitch.setBundles(bundle, bundle1);
        mFragmentSwitch.addSelectedViews(new View[]{tv_ttm_add, iv_ttm_add})
                .addSelectedViews(new View[]{tv_ttm_running, iv_ttm_running});
        mFragmentSwitch.setFragments(sportFrag.getClass(),weightFrag.getClass());

        mFragmentSwitch.changeTag(rel_trl_sport);
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
        mLayoutUtil.drawViewDefaultLinearLayout(rel_trl_sport, -1,-1,-1,-1, (int) PreferenceEntity.ScreenTop,-1);
        mLayoutUtil.drawViewDefaultLinearLayout(rel_trl_weight, -1,-1,-1,-1, (int) PreferenceEntity.ScreenTop,-1);
    }

    @Override
    protected void initLogic() { }

    @Override
    protected void pauseClose() { }

    @Override
    protected void destroyClose() {
    }

    @Override
    public void onClick(View view) { }

    @Override
    public void onDismiss(DialogInterface dialog) {
        LOG("onDismiss");
    }

}
