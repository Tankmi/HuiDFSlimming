package com.huidf.slimming.fragment.today_movement.add;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huidf.slimming.R;
import com.huidf.slimming.base.BaseDialogFragment;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.entity.today_movement.MovementEntity;
import com.huidf.slimming.view.loading.RadioHorizonalRuler;

import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.PreferencesUtils;
import huitx.libztframework.utils.UnitConversion;

public class MovementTimeSelDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    public MovementTimeSelDialogFragment()
    {
        super(R.layout.dialog_fragment_movement_time);
    }

    private PlayQueueListener mQueueListener;

    public interface PlayQueueListener {
        void onPlay(String id);
    }

    public void setQueueListener(PlayQueueListener listener)
    {
        mQueueListener = listener;
    }

    @Override
    protected void initHead()
    {
        //设置无标题
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置从底部弹出
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setAttributes(params);

        mUserWeight = Float.parseFloat(PreferencesUtils.getString(mContext, PreferenceEntity.KEY_USER_CURRENT_WEIGHT, "" + 66.0f));
        ;

    }

    @Override
    public void onStart()
    {
        super.onStart();
        //设置fragment高度 、宽度
        int dialogHeight = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.6);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, dialogHeight);
        getDialog().setCanceledOnTouchOutside(true);
    }


    @Override
    protected void initLogic()
    {
        view_guidance_weight.initViewParam(20, 300, 1, 10);    //设置默认值，最大值，最小值，间隔

        updateValue(20);
        //设置监听
        view_guidance_weight.setValueChangeListener(new RadioHorizonalRuler.OnValueChangeListener() {

            @Override
            public void onValueChange(int value)
            {
                updateValue(value);
            }

        });
    }

    private float mUnit;

    private void updateValue(int value)
    {
        tv_movement_time_ruler_value.setText(value + "");
        NewWidgetSetting.setIdenticalLineTvColor(
                tv_movement_time_ruler_value,
                mContext.getResources().getColor(R.color.text_color_hint), 0.5f,
                "  分钟", false);

        float equalCalorie = mUnit * (value / 60.0f);
        equalCalorie = UnitConversion.preciseNumber(equalCalorie, 0);
        tv_movement_time_value.setText((int)equalCalorie + " 千卡");

//        if (mMovementListener != null) {
//            mMovementListener.onSelMovementTime(value,equalCalorie);
//        }
    }

    private float mUserWeight;
    private boolean isCanNotUpdateData = false;
    private boolean isShowView = false;

    private void initData()
    {
        isCanNotUpdateData = false;
        Glide.with(mContext).load(mMovement.icon).into(iv_mtm);
        tv_mtm.setText(mMovement.name);
        mUnit = mUserWeight * mMovement.equValue;
        mUnit = UnitConversion.preciseNumber(mUnit, 0);
        NewWidgetSetting.getInstance().setIdenticalLineTvColor(
                tv_mtm, mContext.getResources().getColor(R.color.text_color_hint), 0.8f, (int)mUnit + " (千卡)/60分钟", true);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        LOG("onResume isCanNotUpdateData: " + isCanNotUpdateData);
        isShowView = true;
        if (mMovement != null && isCanNotUpdateData) {
            initData();
            updateValue(view_guidance_weight.getValue());
        }

    }

    @Override
    public void paddingDatas(String mData, int type)
    {

    }

    private MovementEntity mMovement;

    /**
     * 接收实体信息
     */
    public void setDataInfo(MovementEntity mMovement)
    {
        LOG("setDataInfo: isShowView" + isShowView);
        isCanNotUpdateData = true;
        this.mMovement = mMovement;

    }

    @Override
    protected void initContent()
    {
        rel_movement_time_main = findViewByIds(R.id.rel_movement_time_main);
        iv_mtm = findViewByIds(R.id.iv_mtm);
        tv_mtm = findViewByIds(R.id.tv_mtm);
        tv_movement_time_value = findViewByIds(R.id.tv_movement_time_value);
        tv_movement_time_ruler_value = findViewByIds(R.id.tv_movement_time_ruler_value);
        view_guidance_weight = findViewByIds(R.id.view_guidance_weight);
        lin_movement_time_control = findViewByIds(R.id.lin_movement_time_control);
        tv_mtc_cancel = findViewByIds(R.id.tv_mtc_cancel);
        tv_mtc_affirm = findViewByIds(R.id.tv_mtc_affirm);
        lin_movement_time_control = findViewByIds(R.id.lin_movement_time_control);
        tv_mtc_cancel.setOnClickListener(this);
        tv_mtc_affirm.setOnClickListener(this);
    }

    @Override
    protected void initLocation()
    {
        mLayoutUtil.drawViewRBLayout(iv_mtm, 88, 88, -1, -1, -1, -1);
        mLayoutUtil.drawViewRBLayout(view_guidance_weight, 0, 110, -1, -1, -1, -1);
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
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.tv_mtc_cancel:
                dismiss();
                break;
            case R.id.tv_mtc_affirm:
                int value = view_guidance_weight.getValue();
                float equalCalorie = mUnit * (value / 60.0f);
                equalCalorie = UnitConversion.preciseNumber(equalCalorie, 0);
                if (mMovementListener != null)  mMovementListener.onSelMovementTime(value,(int)equalCalorie);
                else LOG("回调事件为空！");
                dismiss();
                break;
        }
    }

    private onSelMovementTimeListener mMovementListener;

    public void setOnMovementTimeListener(onSelMovementTimeListener mMovementListener)
    {
        this.mMovementListener = mMovementListener;
    }

    public interface onSelMovementTimeListener {
        int onSelMovementTime(int value,int equalCalorie);
    }

    private RelativeLayout rel_movement_time_main;
    protected ImageView iv_mtm;
    protected LinearLayout lin_movement_time_control;
    private TextView tv_mtm, tv_movement_time_value, tv_movement_time_ruler_value, tv_mtc_cancel, tv_mtc_affirm;
    private RadioHorizonalRuler view_guidance_weight;
}
