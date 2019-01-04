package com.huidf.slimming.activity.home.sleep;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dou361.dialogui.DialogUIUtils;
import com.google.gson.Gson;
import com.huidf.slimming.R;
import com.huidf.slimming.base.BaseFragmentActivityForAnnotation;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.context.UrlConstant;
import com.huidf.slimming.entity.home.sleep.SleepHistoryEntity;
import com.huidf.slimming.entity.home.sport.SportHisEntity;
import com.huidf.slimming.entity.home.sport.SportHistogramEntity;
import com.huidf.slimming.view.home.sleep.SleepTable;
import com.huidf.slimming.view.home.sport.SportHistogramTable;
import com.huidf.slimming.view.home.sport.SportHistogramTable2;
import com.huidf.slimming.view.home.weight.YCoordForWeight;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import huitx.libztframework.context.ContextConstant;
import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.ToastUtils;

public class SleepHisBaseActivity extends BaseFragmentActivityForAnnotation implements OnClickListener, OnCheckedChangeListener, OnItemClickListener {

//    @ViewInject(R.id.vs_sleep_table) protected ViewStub vs_sleep_table;
//    protected ImageView iv_home_sleep_tab_hint;
//     protected SleepTable view_sleep_table;
//    protected LinearLayout lin_sleep_data;
//    protected TextView tv_sleep_data01;
//    protected TextView tv_sleep_data02;
//      protected TextView tv_sleep_data03;

    @ViewInject(R.id.iv_home_sleep_tab_hint) protected ImageView iv_home_sleep_tab_hint;
    @ViewInject(R.id.view_sleep_table) protected SleepTable view_sleep_table;
    @ViewInject(R.id.lin_sleep_data) protected LinearLayout lin_sleep_data;
    @ViewInject(R.id.tv_sleep_data01) protected TextView tv_sleep_data01;
    @ViewInject(R.id.tv_sleep_data02) protected TextView tv_sleep_data02;
    @ViewInject(R.id.tv_sleep_data03) protected TextView tv_sleep_data03;

    @ViewInject(R.id.view_ycoord_sleep_histogram) protected YCoordForWeight view_ycoord_sleep_histogram;
    @ViewInject(R.id.hs_sleep_histogram) protected HorizontalScrollView hs_sleep_histogram;
    @ViewInject(R.id.view_sleep_histogram)  protected SportHistogramTable2 view_sleep_histogram;
    @ViewInject(R.id.lin_sleep_nodata) protected LinearLayout lin_sleep_nodata;
    @ViewInject(R.id.iv_sleep_nodata) protected ImageView iv_sleep_nodata;

    public SleepHisBaseActivity()
    {
        super();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
//        setInfoText(tv_sleep_data01, 105, "时", "分" ,"深睡");
//        setInfoText(tv_sleep_data02, 105, "时", "分" ,"浅睡");
//        setInfoText(tv_sleep_data03, 105, "时", "分" ,"清醒");
//        setHistogramData(view_sleep_histogram, null);
    }

    public void getSleepHistory()
    {
        RequestParams params = PreferenceEntity.getLoginParams();
         mgetNetData.GetData(this, UrlConstant.API_GETALLSLEEP, mHandler.GETSLEEPINFO, params);
        setLoading(true, "");
    }

    @Override
    public void paddingDatas(String mData, int type)
    {
        setLoading(false, "");
        Gson gson = new Gson();
        SleepHistoryEntity mTopicentity;
        try {
            mTopicentity = gson.fromJson(mData, SleepHistoryEntity.class);
        } catch (Exception e) {
            return;
        }
        if (mTopicentity.code == ContextConstant.RESPONSECODE_200) {
            if (type == mHandler.GETSLEEPINFO) {
                setNowData(mTopicentity.data);
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
        setLoading(false, "");
    }

    @SuppressLint("SetTextI18n")
    protected void setNowData(SleepHistoryEntity.Data dataEntity)
    {
        view_sleep_table.setData(dataEntity.list1);
        int deepSleep = NewWidgetSetting.filtrationStringbuffer(dataEntity.deepSleep,0);
        int shallowSleep = NewWidgetSetting.filtrationStringbuffer(dataEntity.shallowSleep,0);
        int noSleepCount = NewWidgetSetting.filtrationStringbuffer(dataEntity.noSleepCount,0);
        setInfoText(tv_sleep_data01, deepSleep, "时", "分" ,"深睡");
        setInfoText(tv_sleep_data02, shallowSleep, "时", "分" ,"浅睡");
        setInfoText(tv_sleep_data03, noSleepCount, "时", "分" ,"清醒");
        setHistogramData(view_sleep_histogram, dataEntity.list2);
    }


    private void setHistogramData(SportHistogramTable2 tableView, List<SleepHistoryEntity.Data.SleepQuality> lists)
    {
        List<SportHistogramEntity> datas = new ArrayList<>();
        for (SleepHistoryEntity.Data.SleepQuality data : lists) {
            SportHistogramEntity mEntity = new SportHistogramEntity();
            mEntity.date = "" + tranTimes.convert(data.createTime, "M.d");
            mEntity.value = Float.parseFloat(data.quality);
            datas.add(mEntity);
        }
        mLayoutUtil.drawViewDefaultLayout(tableView, tableView.setData(datas), -1, -1, -1, -1, -1);
    }

    private void setInfoText(TextView text, int num, String hint1, String hint2, String hint3)
    {   int hour,minute;
        hour = num/60;
        minute = num - hour*60;
        text.setText("");
        NewWidgetSetting.setIdenticalLineTvColor(text, mContext.getResources()
                .getColor(R.color.weight_main_color), 2.0f, hour+"", false);
        NewWidgetSetting.setIdenticalLineTvColor(text, -999, 1, hint1, false);
        NewWidgetSetting.setIdenticalLineTvColor(text, mContext.getResources()
                .getColor(R.color.weight_main_color), 2.0f, minute+"", false);
        NewWidgetSetting.setIdenticalLineTvColor(text, -999, 1, hint2, false);
        NewWidgetSetting.setIdenticalLineTvColor(text, -999, 1, hint3, true);
    }


    protected MyHandler mHandler;

    protected class MyHandler extends Handler {
        protected final int GETSLEEPINFO = 10001;

        // SoftReference<Activity> 也可以使用软应用 只有在内存不足的时候才会被回收
        private final WeakReference<Activity> mActivity;

        protected MyHandler(Activity activity)
        {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg)
        {
            Activity activity = mActivity.get();
            if (activity != null) {
                //做操作
                switch (msg.what) {
                    case GETSLEEPINFO: // 获取睡眠信息
                        getSleepHistory();
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected void initContent()
    {
//        initSleepView();
        view_sleep_histogram.initYcoorData("50,60,70,80,90,100");
    }

    @Override
    protected void initLocation()
    {
        mLayoutUtil.drawViewRBLinearLayout(view_sleep_table,0,225,0,0,18,0);
        mLayoutUtil.drawViewRBLinearLayout(view_ycoord_sleep_histogram, 80, 350, -1, 0, -1, 0);
        mLayoutUtil.drawViewRBLayout(view_sleep_histogram, 0, 350, 0, 0, 0, 0);
    }

    private void initSleepView()
    {
//        vs_sleep_table.inflate();
//        iv_home_sleep_tab_hint = findViewByIds(vs_sleep_table, R.id.iv_home_sleep_tab_hint);
//        view_sleep_table = findViewByIds(vs_sleep_table, R.id.view_sleep_table);
//        lin_sleep_data = findViewByIds(vs_sleep_table, R.id.lin_sleep_data);
//        tv_sleep_data01 = findViewByIds(vs_sleep_table, R.id.tv_sleep_data01);
//        tv_sleep_data02 = findViewByIds(vs_sleep_table, R.id.tv_sleep_data02);
//        tv_sleep_data03 = findViewByIds(vs_sleep_table, R.id.tv_sleep_data03);
//        mLayoutUtil.drawViewRBLinearLayout(view_sleep_table,0,225,0,0,18,0);
    }


    @Override
    protected void initLogic()
    {
    }

    @Override
    public void onClick(View view)
    {
    }

    @Override
    protected void initHead()
    {
    }

    @Override
    protected void pauseClose()
    {
    }

    @Override
    protected void destroyClose()
    {
    }

    @Override
    public void onCheckedChanged(RadioGroup arg0, int arg1)
    {
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
    {
    }


    private View mDialogView;
    protected Dialog dialog;
    private RelativeLayout rel_dialog_sporthis_hint;
    private Button btn_dia_sporthis_hint;

    protected void showHintDialogView()
    {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (mDialogView == null) {
            mDialogView = View.inflate(this, R.layout.dialog_sport_his_hint, null);
            rel_dialog_sporthis_hint = findViewByIds(mDialogView, R.id.rel_dialog_sporthis_hint);
            btn_dia_sporthis_hint = findViewByIds(mDialogView, R.id.btn_dia_sporthis_hint);

            mLayoutUtil.drawViewDefaultLinearLayout(rel_dialog_sporthis_hint, 523, 536, 0, 0, 0, 0);
            mLayoutUtil.drawViewRBLinearLayout(btn_dia_sporthis_hint, 46, 46, 0, 0, -1, 0);

            btn_dia_sporthis_hint.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (dialog != null) {
                        if (dialog != null) DialogUIUtils.dismiss(dialog);
                    }
                }
            });
        }
        if (dialog == null)
            dialog = DialogUIUtils.showCustomAlert(this, mDialogView, Gravity.CENTER, true, false).show();
        else dialog.show();

    }

}
