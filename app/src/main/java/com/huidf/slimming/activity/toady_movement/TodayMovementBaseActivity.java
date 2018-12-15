package com.huidf.slimming.activity.toady_movement;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dou361.dialogui.DialogUIUtils;
import com.huidf.slimming.R;
import com.huidf.slimming.activity.toady_movement.run.RunBaseActivity;
import com.huidf.slimming.base.BaseFragmentActivity;
import com.huidf.slimming.context.ApplicationData;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.fragment.home.HomeFragment;
import com.huidf.slimming.fragment.personal_center.PersonalCenterFragment;
import com.huidf.slimming.fragment.today_movement.add.TodayMovementAddFragment;
import com.huidf.slimming.fragment.today_movement.running.TodayMovementRunningFragment;
import com.huidf.slimming.util.VersionTools;

import java.lang.ref.WeakReference;

import huitx.libztframework.net.GetNetData;
import huitx.libztframework.utils.PreferencesUtils;
import huitx.libztframework.utils.ToastUtils;
import huitx.libztframework.view.FragmentSwitchTool;


public class TodayMovementBaseActivity extends BaseFragmentActivity implements OnClickListener, DialogInterface.OnDismissListener{
    private final int VERSION_UPDATE = 100;


    protected FragmentSwitchTool mFragmentSwitch;
    protected TodayMovementAddFragment addFragment;
    protected TodayMovementRunningFragment mapsFragment;
    private LinearLayout lin_tab_today_movement;
    private RelativeLayout rel_ttm_add;
    private TextView tv_ttm_add;
    private ImageView iv_ttm_add;
    private RelativeLayout rel_ttm_running;
    private ImageView iv_ttm_running;
    private TextView tv_ttm_running;

    public TodayMovementBaseActivity(int layoutId) {
        super(layoutId);
    }

    //
    @Override
    protected void initContent() {
        lin_tab_today_movement = findViewByIds(R.id.lin_tab_today_movement);
        rel_ttm_add = findViewByIds(R.id.rel_ttm_add);
        iv_ttm_add = findViewByIds(R.id.iv_ttm_add);
        tv_ttm_add = findViewByIds(R.id.tv_ttm_add);
        rel_ttm_running = findViewByIds(R.id.rel_ttm_running);
        iv_ttm_running = findViewByIds(R.id.iv_ttm_running);
        tv_ttm_running = findViewByIds(R.id.tv_ttm_running);

        initFragment();
    }

    private void initFragment() {
        addFragment = new TodayMovementAddFragment();
        mapsFragment = new TodayMovementRunningFragment();

        mFragmentSwitch = new FragmentSwitchTool(getSupportFragmentManager(), R.id.fl_today_movement);
        mFragmentSwitch.setClickableViews(rel_ttm_add, rel_ttm_running);
        mFragmentSwitch.addSelectedViews(new View[]{tv_ttm_add, iv_ttm_add})
                .addSelectedViews(new View[]{tv_ttm_running, iv_ttm_running});
        mFragmentSwitch.setFragments(addFragment.getClass(),mapsFragment.getClass());

//        mFragmentSwitch.changeTag(rel_ttm_add);
        mFragmentSwitch.changeTag(rel_ttm_running);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void paddingDatas(String mData, int type) {

    }

    @Override
    public void error(String msg, int type) {
        LOG(msg);
    }


    protected MyHandler mHandler;

    protected class MyHandler extends Handler{
        protected final int ANIMATION_START = 100;  //启动倒计时背景渲染动画

        private final WeakReference<Activity> mActivity;

        protected MyHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = mActivity.get();
            if (activity != null){
                switch (msg.what) {
                    case ANIMATION_START:

                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected void initLocation() {
        mLayoutUtil.setIsFullScreen(true);
//        lin_tab_today_movement.setMinimumHeight(mLayoutUtil.getWidgetHeight(100));
    }

    @Override
    protected void initHead() { }

    @Override
    protected void initLogic() { }

    @Override
    protected void pauseClose() { }

    @Override
    protected void destroyClose() { }

    @Override
    public void onClick(View view) { }

    @Override
    public void onDismiss(DialogInterface dialog) {
        LOG("onDismiss");
    }

}
