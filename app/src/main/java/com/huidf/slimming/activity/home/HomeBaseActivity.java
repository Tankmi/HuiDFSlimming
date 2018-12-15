package com.huidf.slimming.activity.home;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
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
import com.huidf.slimming.base.BaseFragmentActivity;
import com.huidf.slimming.context.ApplicationData;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.fragment.home.HomeFragment;
import com.huidf.slimming.fragment.personal_center.PersonalCenterFragment;
import com.huidf.slimming.fragment.ranking.RankingListFragment;
import com.huidf.slimming.util.VersionTools;

import huitx.libztframework.net.GetNetData;
import huitx.libztframework.utils.PreferencesUtils;
import huitx.libztframework.utils.ToastUtils;
import huitx.libztframework.view.FragmentSwitchTool;


/**
 * @author : Zhutao
 * @version 创建时间：@2016年11月23日
 * @Description: 首页
 * @params：
 */
public class HomeBaseActivity extends BaseFragmentActivity implements OnClickListener, DialogInterface.OnDismissListener{

    protected HomeFragment homeFragment;
    protected RankingListFragment rankingFragment;
//    protected DynamicWebViewFragment dynamicFoodFragment;
//    protected MarketWebViewFragment marketFragment;
    protected PersonalCenterFragment settingsFragment;
    protected FragmentSwitchTool mFragmentSwitch;

    private final int VERSION_UPDATE = 100;
    /**
     * 记录页面退出时间
     */
    private long exitTime = 0;

    private RelativeLayout rel_home_main;
    private LinearLayout lin_tab_home;
    private LinearLayout lin_tab_home_choiceness;
    private ImageView iv_tab_home_choiceness;
    private TextView tv_tab_home_choiceness;
    private LinearLayout lin_tab_home_dynamic;
    private ImageView iv_tab_home_dynamic;
    private TextView tv_tab_home_dynamic;
    private LinearLayout lin_tab_home_market;
    private ImageView iv_tab_home_market;
    private TextView tv_tab_home_market;
    private LinearLayout lin_tab_home4;
    private ImageView iv_tab_home4;
    private TextView tv_tab_home4;
    private LinearLayout lin_tab_home_settings;
    private ImageView iv_tab_home_settings;
    private TextView tv_tab_home_settings;

    public HomeBaseActivity(int layoutId) {
        super(layoutId);
    }

    //
    @Override
    protected void initContent() {
        rel_home_main = findViewByIds(R.id.rel_home_main);
        lin_tab_home = findViewByIds(R.id.lin_tab_home);
        lin_tab_home_choiceness = findViewByIds(R.id.lin_tab_home_choiceness);
        iv_tab_home_choiceness = findViewByIds(R.id.iv_tab_home_choiceness);
        tv_tab_home_choiceness = findViewByIds(R.id.tv_tab_home_choiceness);
        lin_tab_home_dynamic = findViewByIds(R.id.lin_tab_home_dynamic);
        iv_tab_home_dynamic = findViewByIds(R.id.iv_tab_home_dynamic);
        tv_tab_home_dynamic = findViewByIds(R.id.tv_tab_home_dynamic);
        lin_tab_home_market = findViewByIds(R.id.lin_tab_home_market);
        iv_tab_home_market = findViewByIds(R.id.iv_tab_home_market);
        tv_tab_home_market = findViewByIds(R.id.tv_tab_home_market);
        lin_tab_home4 = findViewByIds(R.id.lin_tab_home4);
        iv_tab_home4 = findViewByIds(R.id.iv_tab_home4);
        tv_tab_home4 = findViewByIds(R.id.tv_tab_home4);
        lin_tab_home_settings = findViewByIds(R.id.lin_tab_home_settings);
        iv_tab_home_settings = findViewByIds(R.id.iv_tab_home_settings);
        tv_tab_home_settings = findViewByIds(R.id.tv_tab_home_settings);

        initFragment();
    }

    private void initFragment() {
        homeFragment = new HomeFragment();
//        dynamicFoodFragment = new DynamicWebViewFragment();
//        marketFragment = new MarketWebViewFragment();
        rankingFragment = new RankingListFragment();
        settingsFragment = new PersonalCenterFragment();

        mFragmentSwitch = new FragmentSwitchTool(getSupportFragmentManager(), R.id.fl_home);
        mFragmentSwitch.setClickableViews(lin_tab_home_choiceness, lin_tab_home_dynamic, lin_tab_home_market, lin_tab_home4, lin_tab_home_settings);
        mFragmentSwitch.addSelectedViews(new View[]{iv_tab_home_choiceness})
                .addSelectedViews(new View[]{iv_tab_home_dynamic})
                .addSelectedViews(new View[]{iv_tab_home_market})
                .addSelectedViews(new View[]{iv_tab_home4})
                .addSelectedViews(new View[]{iv_tab_home_settings});
        mFragmentSwitch.setFragments(homeFragment.getClass(), homeFragment.getClass(),
                homeFragment.getClass(), rankingFragment.getClass(), settingsFragment.getClass());

//        mFragmentSwitch.changeTag(lin_tab_home_choiceness);
        mFragmentSwitch.changeTag(lin_tab_home4);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * 获取服务端缓存的版本号，判断是否需要更新 0
     */
    protected void getVersion() {
        int version = VersionTools.getVersion(mContext);
        int newVersion = PreferencesUtils.getInt(mContext, PreferenceEntity.KEY_APP_UPDATE_VERSION);
        LOG("当前系统版本：" + version + "; 系统版本：" + newVersion);
        if (version < newVersion) {
            String url = PreferencesUtils.getString(mContext, PreferenceEntity.KEY_APP_UPDATE_URL);
            if (!url.equals("")) initDialogView(1, url);
        }

    }

    private void updateVersion(String url) {
        if (mgetNetData == null) mgetNetData = new GetNetData();
        if (url.equals("")) return;
        mgetNetData.downloadData(this, url, VERSION_UPDATE, "huidaifu.apk");
    }

    @Override
    public void paddingDatas(String mData, int type) {
        if (type == VERSION_UPDATE) {  //下载新版本
            Message msg = new Message();
            msg.obj = mData;
            msg.what = VERSION_UPDATE;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void error(String msg, int type) {
        LOG(msg);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: // 获取版本号

                    break;
                case VERSION_UPDATE: // 更新版本下载进度
                    String data = (String) msg.obj;
                    if (data.contains("okss")) {
                        if (dialog != null) {
                            DialogUIUtils.dismiss(dialog);
                        }
                        VersionTools.update(HomeBaseActivity.this, data.substring(4));
                    } else {
                        int pro = Integer.parseInt(data);
                        progress.setProgress(pro);
                        tv_dua_progress.setText(pro + "%");
                        LOG("进度：" + progress.getProgress() + ";最大值：" + progress.getMax());
                    }
                    break;
            }
        }
    };

    @Override
    protected void initLocation() {
        mLayoutUtil.setIsFullScreen(true);
        lin_tab_home.setMinimumHeight(mLayoutUtil.getWidgetHeight(100));
        mLayoutUtil.drawViewRBLinearLayout(iv_tab_home_choiceness, 58, 58, 0, 0, 0, 0);
        mLayoutUtil.drawViewRBLinearLayout(iv_tab_home_dynamic, 58, 58, 0, 0, 0, 0);
        mLayoutUtil.drawViewRBLinearLayout(iv_tab_home_market, 58, 58, 0, 0, 0, 0);
        mLayoutUtil.drawViewRBLinearLayout(iv_tab_home4, 58, 58, 0, 0, 0, 0);
        mLayoutUtil.drawViewRBLinearLayout(iv_tab_home_settings, 58, 58, 0, 0, 0, 0);
    }

    private View mDialogView;
    protected Dialog dialog;
    private LinearLayout lin_dialog_update_affirm;
    private ImageView iv_dialog_update_affirm;
    private TextView tv_dialog_update_affirm;
    private ProgressBar progress;
    private TextView tv_dua_progress;
    private LinearLayout lin_dia_update_bottom;
    private LinearLayout lin_dia_update_sel;
    private Button btn_dia_update_cancel;
    private Button btn_dia_update_affirm;

    /**
     * 更新提示
     * 1,提示更新，2，更新
     */
    protected void initDialogView(int type, final String url) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (mDialogView == null) {
            mDialogView = View.inflate(this, R.layout.dialog_update_affirm, null);
            lin_dialog_update_affirm = findViewByIds(mDialogView, R.id.lin_dialog_update_affirm);
            iv_dialog_update_affirm = findViewByIds(mDialogView, R.id.iv_dialog_update_affirm);
            tv_dialog_update_affirm = findViewByIds(mDialogView, R.id.tv_dialog_update_affirm);
            progress = findViewByIds(mDialogView, R.id.progress);
            tv_dua_progress = findViewByIds(mDialogView, R.id.tv_dua_progress);
            lin_dia_update_bottom = findViewByIds(mDialogView, R.id.lin_dia_update_bottom);
            lin_dia_update_sel = findViewByIds(mDialogView, R.id.lin_dia_update_sel);
            btn_dia_update_cancel = findViewByIds(mDialogView, R.id.btn_dia_update_cancel);
            btn_dia_update_affirm = findViewByIds(mDialogView, R.id.btn_dia_update_affirm);

            lin_dialog_update_affirm.setMinimumHeight(mLayoutUtil.getWidgetHeight(232));
            mLayoutUtil.drawViewRBLinearLayouts(lin_dialog_update_affirm, 0.7f, 0, 0, 0, 0, 0);
            mLayoutUtil.drawViewRBLinearLayout(iv_dialog_update_affirm, 150, 150, 0, 0, 54, 0);

            btn_dia_update_cancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        DialogUIUtils.dismiss(dialog);
                    }
                }
            });
            btn_dia_update_affirm.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {

                        initDialogView(2, "");
                        updateVersion(url);
//                        DialogUIUtils.dismiss(dialog);
                    }
                }
            });
        }
        if (type == 1) {
            tv_dialog_update_affirm.setText("慧大夫减肥瘦身有新的版本升级");
            iv_dialog_update_affirm.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            tv_dua_progress.setVisibility(View.GONE);
            lin_dia_update_bottom.setVisibility(View.VISIBLE);
        } else {
            tv_dialog_update_affirm.setText("正在更新中");
            tv_dua_progress.setText("0%");
            iv_dialog_update_affirm.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            tv_dua_progress.setVisibility(View.VISIBLE);
            lin_dia_update_bottom.setVisibility(View.GONE);
        }


        if (dialog == null)
            dialog = DialogUIUtils.showCustomAlert(this, mDialogView, Gravity.CENTER, true, false).show();
        else dialog.show();

        dialog.setOnDismissListener(this);

    }

    @Override
    protected void initHead() {
    }

    @Override
    protected void initLogic() {
    }

    @Override
    protected void pauseClose() {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2500) {
                ToastUtils.showToast("再按一次后退键退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                ApplicationData.getInstance().exit();
//				android.os.Process.killProcess(android.os.Process.myPid());
                finish();
            }
            return true;
        }
        return super.onKeyDown(event.getKeyCode(), event);
    }

    @Override
    protected void destroyClose() {
    }

    @Override
    public void onClick(View view) {
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        LOG("onDismiss");
    }



//    @Override
//    public void setSelectedFragment(WebViewBaseFragment selectedFragment) {
//
//    }

    WebView mWebView;

    public void inputData() {   //缓存id数据
//        mWebView = findViewByIds(R.id.web_home);
//        MyWebView.getInstance().getWebView(mWebView);
//
//        //当用户点击一个连接的时候不跳转到手机的浏览器上！
//        mWebView.setWebViewClient(mWebViewClient);
//        String userId = PreferencesUtils.getString(mContext, PreferenceEntity.KEY_USER_ID, "");
//        String phone = PreferencesUtils.getString(mContext, PreferenceEntity.KEY_USER_ACCOUNT, "");
//        String imei = PreferencesUtils.getString(mContext, PreferenceEntity.KEY_USER_IMEI, "");
//        if (userId != null && !userId.equals("")) {
//            mWebView.loadUrl(HtmlUrlConstant.HTML_POSTUSERDATA + userId + "&phone=" + phone + "&imei=" + imei);
//            PreferenceEntity.isSyncUserDatas = true;
//        }
    }

    WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            LOG("加载url结束:" + url);
        }
    };
}
