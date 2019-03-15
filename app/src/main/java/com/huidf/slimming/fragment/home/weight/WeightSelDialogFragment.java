package com.huidf.slimming.fragment.home.weight;

import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huidf.slimming.R;
import com.huidf.slimming.base.BaseDialogFragment;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.view.loading.RadioHorizonalRulerDecimals;

import huitx.libztframework.utils.MathUtils;
import huitx.libztframework.utils.NewWidgetSetting;

public class WeightSelDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    public WeightSelDialogFragment()
    {
        super(R.layout.dialog_fragment_horizonal_ruler);
    }

    protected PlayQueueListener mQueueListener;

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
        float mWeight = MathUtils.getFloatForPreference(PreferenceEntity.KEY_USER_INITIAL_WEIGHT,50.0f);
        float height = MathUtils.stringToFloatForPreference(PreferenceEntity.KEY_USER_HEIGHT, 166) * 0.01f;
        int minWeight = (int) (height*height*15.0f);

        view_guidance_weight.initViewParam(mWeight, 300, minWeight, 10);    //设置默认值，最大值，最小值，间隔之间的分隔数目
//        view_guidance_weight.initViewParam(50.1f, 100, 20, 10);    //设置默认值，最大值，最小值，间隔之间的分隔数目

        updateValue(mWeight);
        //设置监听
        view_guidance_weight.setValueChangeListener(new RadioHorizonalRulerDecimals.OnValueChangeListener() {

            @Override
            public void onValueChange(float value)
            {
                updateValue(value);
            }

        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
         if(view_guidance_weight != null) updateValue(view_guidance_weight.getValue());
    }

    @Override
    public void paddingDatas(String mData, int type)
    {

    }

    private void updateValue(float value)
    {
        tv_movement_time_ruler_value.setText(value + "");
        NewWidgetSetting.setIdenticalLineTvColor(
                tv_movement_time_ruler_value,
                mContext.getResources().getColor(R.color.text_color_hint), 0.5f,
                "  kg", false);
    }

    @Override
    protected void initContent()
    {
        tv_movement_time_ruler_value = findViewByIds(R.id.tv_movement_time_ruler_value);
        view_guidance_weight = findViewByIds(R.id.view_guidance_weight);
        lin_movement_time_control = findViewByIds(R.id.lin_movement_time_control);
        tv_mtc_cancel = findViewByIds(R.id.tv_mtc_cancel);
        tv_mtc_affirm = findViewByIds(R.id.tv_mtc_affirm);

        tv_mtc_cancel.setOnClickListener(this);
        tv_mtc_affirm.setOnClickListener(this);
    }

    @Override
    protected void initLocation()
    {
        mLayoutUtil.drawViewRBLayout(view_guidance_weight, 0, 110, -1, -1, -1, -1);
        lin_movement_time_control.setMinimumHeight(mLayoutUtil.getWidgetHeight(110));
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
                float value = view_guidance_weight.getValue();
                if (mMovementListener != null)  mMovementListener.onSelData(value);
                dismiss();
                break;
        }
    }

    private onSelDataListener mMovementListener;

    public void setOnSelDataListener(onSelDataListener mMovementListener)
    {
        this.mMovementListener = mMovementListener;
    }

    public interface onSelDataListener {
        int onSelData(float value);
    }

    protected LinearLayout lin_movement_time_control;
    private TextView tv_movement_time_ruler_value, tv_mtc_cancel, tv_mtc_affirm;
    private RadioHorizonalRulerDecimals view_guidance_weight;
}
