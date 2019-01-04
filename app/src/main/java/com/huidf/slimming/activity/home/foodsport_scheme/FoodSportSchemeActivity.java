package com.huidf.slimming.activity.home.foodsport_scheme;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huidf.slimming.R;
import com.huidf.slimming.base.BaseFragmentActivityForAnnotation;
import com.huidf.slimming.context.HtmlUrlConstant;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.context.UrlConstant;
import com.huidf.slimming.fragment.home.food_sport_scheme.FoodSchemeFragment;
import com.huidf.slimming.fragment.home.food_sport_scheme.SportSchemeFragment;
import com.huidf.slimming.fragment.ranking.item.RankingListItemFragment;
import com.huidf.slimming.web.activity.WebViewActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import huitx.libztframework.view.FragmentSwitchTool;

@ContentView(R.layout.activity_food_sport_scheme)
public class FoodSportSchemeActivity extends BaseFragmentActivityForAnnotation implements OnClickListener, DialogInterface.OnDismissListener{

    @ViewInject(R.id.rel_food_sport_scheme_main) private RelativeLayout rel_food_sport_scheme_main;
    @ViewInject(R.id.lin_food_sport_scheme_title) private LinearLayout lin_food_sport_scheme_title;
    @ViewInject(R.id.btn_fss_back) private Button btn_fss_back;
    @ViewInject(R.id.rel_fss_food) private RelativeLayout rel_fss_food;
    @ViewInject(R.id.tv_fssf) private TextView tv_fssf;
    @ViewInject(R.id.iv_fssf) private ImageView iv_fssf;
    @ViewInject(R.id.rel_fss_sport) private RelativeLayout rel_fss_sport;
    @ViewInject(R.id.tv_fsss)private TextView tv_fsss;
    @ViewInject(R.id.iv_fsss) private ImageView iv_fsss;
    @ViewInject(R.id.btn_fss_hint) private Button btn_fss_hint;


    protected FragmentSwitchTool mFragmentSwitch;
    protected FoodSchemeFragment foodFrag;
    protected SportSchemeFragment sportFrag;

    public FoodSportSchemeActivity() {
        super();
    }

    @Override
    protected void initHead() {
        setStatusBarColor(true, true, mContext.getResources().getColor(R.color.white));
        mTitleView.setVisibility(View.VISIBLE);
        tv_fssf.setText("饮食方案");
        tv_fsss.setText("运动方案");
    }

    //
    @Override
    protected void initContent() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if(!inInitFragment)initFragment();
    }


    @Event(value={R.id.btn_fss_hint, R.id.btn_fss_back})
    private void getEvent(View view)
    {
        switch(view.getId()) {
            case R.id.btn_fss_back:  //返回键
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),  0);
                this.onBackPressed();
                break;
            case R.id.btn_fss_hint:  //提示
                LOG("提示");
                Intent intent = new Intent(FoodSportSchemeActivity.this, WebViewActivity.class);
                intent.putExtra("url", HtmlUrlConstant.HTML_SPORTSCHEME);
                intent.putExtra("title_name", "HIIT");
                intent.putExtra("is_refresh", true);
                startActivity(intent);
                break;
        }
    }

    private boolean inInitFragment;
    private void initFragment() {
        inInitFragment = true;
        foodFrag = new FoodSchemeFragment();
        sportFrag = new SportSchemeFragment();

//        Bundle bundle = new Bundle();
//        bundle.putInt("type",1);//这里的values就是我们要传的值
//        Bundle bundle1 = new Bundle();
//        bundle1.putInt("type",2);//这里的values就是我们要传的值

        mFragmentSwitch = new FragmentSwitchTool(getSupportFragmentManager(), R.id.fl_fss);
        mFragmentSwitch.setClickableViews(rel_fss_food, rel_fss_sport);
//        mFragmentSwitch.setBundles(bundle, bundle1);
        mFragmentSwitch.addSelectedViews(new View[]{tv_fssf, iv_fssf})
                .addSelectedViews(new View[]{tv_fsss, iv_fsss});
        mFragmentSwitch.setFragments(foodFrag.getClass(),sportFrag.getClass());

        mFragmentSwitch.setOnFSTItemClickListener(new FragmentSwitchTool.onFSTItemClickListener() {
            @Override
            public void onFSTItemClick(View v)
            {
                if(v == rel_fss_sport){
                    btn_fss_hint.setEnabled(true);
                }else {
                    btn_fss_hint.setEnabled(false);
                }
            }
        });

        mFragmentSwitch.changeTag(rel_fss_food);
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
          mLayoutUtil.drawViewDefaultLinearLayout(btn_fss_back, 120, 56, -1, 0, 0, 0);
          mLayoutUtil.drawViewRBLinearLayout(btn_fss_hint, 50, 50, 0, -1, 0, 0);
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
