package com.huidf.slimming.fragment.personal_center;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.huidf.slimming.R;
import com.huidf.slimming.base.BaseFragment;
import com.huidf.slimming.context.ApplicationData;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.context.UrlConstant;
import com.huidf.slimming.entity.user.UserEntity;

import org.xutils.http.RequestParams;

import huitx.libztframework.context.ContextConstant;
import huitx.libztframework.utils.BitmapUtils;
import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.PreferencesUtils;
import huitx.libztframework.utils.ToastUtils;
import huitx.libztframework.utils.image_loader.ImageLoaderUtils;

@SuppressLint("ValidFragment")
public class PersonalCenterBaseFragment extends BaseFragment implements
        OnClickListener {

    public PersonalCenterBaseFragment(int layoutId)
    {
        super(layoutId);
    }

    @Override
    protected void initContent()
    {
        findView();
        iv_sett_back.setBackground(BitmapUtils.getDrawableResources(R.drawable.back_personal_center));
    }


    protected final int GETUSERINFO = 101;

    /**
     * 获取用户的信息
     */
    public void GetUserInfo()
    {
        RequestParams params = PreferenceEntity.getLoginParams();
        mgetNetData.GetData(this, UrlConstant.GET_PERSONAL_CENTER, GETUSERINFO, params);
        setLoading(true, "");
    }

    public UserEntity mUserEntity;

    @Override
    public void paddingDatas(String mData, int type)
    {
        setLoading(false, "");
        Gson mGson = new Gson();
        try {
            mUserEntity = mGson.fromJson(mData, UserEntity.class);
        } catch (Exception e) {
            return;
        }
        if (mUserEntity.code == ContextConstant.RESPONSECODE_200) {
            if (type == GETUSERINFO) {    //获取用户信息
                Message message = Message.obtain();
                message.what = type;
                message.obj = mUserEntity.data;
                mHandler.sendMessage(message);
            }
        } else if (mUserEntity.code == ContextConstant.RESPONSECODE_310) {    //登录信息过时跳转到登录页
            reLoading();
        } else {
            ToastUtils.showToast(NewWidgetSetting.getInstance().filtrationStringbuffer(mUserEntity.msg, "接口信息异常！"));
        }
    }

    @Override
    public void error(String msg, int type)
    {
        setLoading(false, "");
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg)
        {

            switch (msg.what) {
                case GETUSERINFO:    //获取用户信息
                    UserEntity.Data userInfo = (UserEntity.Data) msg.obj;
                    PreferenceEntity.setUserInfoEntity(userInfo);
                    setData(userInfo);
                    break;
            }
        }

    };

    @Override
    protected void initHead()
    {
    }

    @Override
    protected void onVisibile()
    {
    }

    public void setData(UserEntity.Data userInfo)
    {
        if (userInfo == null) userInfo = UserEntity.getUserInfo();
        NewWidgetSetting.setViewText(tv_setti_name,userInfo.name,"暂无账户信息");
        NewWidgetSetting.setViewText(tv_setti_phone, "手机号：", userInfo.account, "暂未绑定手机号", true);
        NewWidgetSetting.setViewText(tv_sett_name_value, userInfo.name,"点击设置用户名");
        NewWidgetSetting.setViewText(tv_sett_age_value, "岁 ", userInfo.age, "暂无信息", false);
        NewWidgetSetting.setViewText(tv_sett_dynamic_value, "条 ", userInfo.count, "0", false);
        NewWidgetSetting.setViewText(tv_sett_height_value, " cm", userInfo.height, "暂无信息",false);
        NewWidgetSetting.setViewText(tv_sett_weight_value, " kg", userInfo.latestWeight, "暂无信息",false);
        NewWidgetSetting.setViewText(tv_sett_bmi_value, userInfo.bmi, "暂无信息");
        NewWidgetSetting.setViewText(tv_sett_target_weight_value," kg", userInfo.targetWeight, "暂无信息" ,false);
        String time = NewWidgetSetting.getInstance().filtrationStringbuffer(userInfo.targetTime,"");
        tv_sett_target_reach_time_value.setText((time == null || time.equals("")?"未知":tranTimes.convert(time,"yyyy年M月d日")));
//        NewWidgetSetting.setViewText(tv_sett_target_reach_time_value, tranTimes.convert(time,"yyyy年M月d日"), "暂无信息");
        NewWidgetSetting.setViewText(tv_sett_step_num_value, "运动步数", "暂无信息");

        setSexHeader(userInfo.sex, userInfo.head);

    }

    private String user_header;

    /**
     * 设置用户头像
     */
    public void setSexHeader(String sex,String header)
    {
        if (user_header != null && header != null && user_header.equals(header)) {    //头像没做修改
            return;
        }
        user_header = header;
        RequestOptions options;
        if (sex != null && sex.equals("1")) {    //男
            options = new RequestOptions()
                    .placeholder(R.drawable.iv_man_bef)
                    .circleCrop();
        } else {
            options = new RequestOptions()
                    .placeholder(R.drawable.iv_woman_bef)
                    .circleCrop();
        }

        Glide.with(mContext).load(user_header).apply(options).into(iv_sett_header);

    }

    protected ScrollView sc_settings;
    protected RelativeLayout rel_settings_title;
    protected ImageView iv_sett_back;
    protected TextView tv_sett_title;
    protected Button btn_sett_setting;
    protected RelativeLayout rel_sett_info;
    protected ImageView iv_sett_header;
    protected TextView tv_setti_name;
    protected TextView tv_setti_phone;
    protected LinearLayout lin_sett_name;
    protected TextView tv_sett_name;
    protected TextView tv_sett_name_value;
    protected LinearLayout lin_sett_age;
    protected TextView tv_sett_age;
    protected TextView tv_sett_age_value;
    protected LinearLayout lin_sett_dynamic;
    protected TextView tv_sett_dynamic;
    protected TextView tv_sett_dynamic_value;
    protected LinearLayout lin_sett_height;
    protected TextView tv_sett_height;
    protected TextView tv_sett_height_value;
    protected LinearLayout lin_sett_weight;
    protected TextView tv_sett_weight;
    protected TextView tv_sett_weight_value;
    protected LinearLayout lin_sett_bmi;
    protected TextView tv_sett_bmi;
    protected TextView tv_sett_bmi_value;
    protected LinearLayout lin_sett_target_weight;
    protected TextView tv_sett_target_weight;
    protected TextView tv_sett_target_weight_value;
    protected LinearLayout lin_sett_target_reach_time;
    protected TextView tv_sett_target_reach_time;
    protected TextView tv_sett_target_reach_time_value;
    protected LinearLayout lin_sett_step_num;
    protected TextView tv_sett_step_num;
    protected TextView tv_sett_step_num_value;


    /**
     * 初始化视图控件 添加监听事件
     */
    public void findView()
    {
        sc_settings = findViewByIds(R.id.sc_settings);
        rel_settings_title = findViewByIds(R.id.rel_settings_title);
        iv_sett_back = findViewByIds(R.id.iv_sett_back);
        tv_sett_title = findViewByIds(R.id.tv_sett_title);
        btn_sett_setting = findViewByIds(R.id.btn_sett_setting);
        rel_sett_info = findViewByIds(R.id.rel_sett_info);
        iv_sett_header = findViewByIds(R.id.iv_sett_header);
        tv_setti_name = findViewByIds(R.id.tv_setti_name);
        tv_setti_phone = findViewByIds(R.id.tv_setti_phone);
        lin_sett_name = findViewByIds(R.id.lin_sett_name);
        tv_sett_name = findViewByIds(R.id.tv_sett_name);
        tv_sett_name_value = findViewByIds(R.id.tv_sett_name_value);
        lin_sett_age = findViewByIds(R.id.lin_sett_age);
        tv_sett_age = findViewByIds(R.id.tv_sett_age);
        tv_sett_age_value = findViewByIds(R.id.tv_sett_age_value);
        lin_sett_dynamic = findViewByIds(R.id.lin_sett_dynamic);
        tv_sett_dynamic = findViewByIds(R.id.tv_sett_dynamic);
        tv_sett_dynamic_value = findViewByIds(R.id.tv_sett_dynamic_value);
        lin_sett_height = findViewByIds(R.id.lin_sett_height);
        tv_sett_height = findViewByIds(R.id.tv_sett_height);
        tv_sett_height_value = findViewByIds(R.id.tv_sett_height_value);
        lin_sett_weight = findViewByIds(R.id.lin_sett_weight);
        tv_sett_weight = findViewByIds(R.id.tv_sett_weight);
        tv_sett_weight_value = findViewByIds(R.id.tv_sett_weight_value);
        lin_sett_bmi = findViewByIds(R.id.lin_sett_bmi);
        tv_sett_bmi = findViewByIds(R.id.tv_sett_bmi);
        tv_sett_bmi_value = findViewByIds(R.id.tv_sett_bmi_value);
        lin_sett_target_weight = findViewByIds(R.id.lin_sett_target_weight);
        tv_sett_target_weight = findViewByIds(R.id.tv_sett_target_weight);
        tv_sett_target_weight_value = findViewByIds(R.id.tv_sett_target_weight_value);
        lin_sett_target_reach_time = findViewByIds(R.id.lin_sett_target_reach_time);
        tv_sett_target_reach_time = findViewByIds(R.id.tv_sett_target_reach_time);
        tv_sett_target_reach_time_value = findViewByIds(R.id.tv_sett_target_reach_time_value);
        lin_sett_step_num = findViewByIds(R.id.lin_sett_step_num);
        tv_sett_step_num = findViewByIds(R.id.tv_sett_step_num);
        tv_sett_step_num_value = findViewByIds(R.id.tv_sett_step_num_value);

        rel_sett_info.setOnClickListener(this);
        btn_sett_setting.setOnClickListener(this);

        tv_sett_name.setText("姓名");
        tv_sett_age.setText("年龄");
        tv_sett_dynamic.setText("动态");
        tv_sett_height.setText("身高");
        tv_sett_weight.setText("体重");
        tv_sett_bmi.setText("BMI");
        tv_sett_target_weight.setText("目标体重");
        tv_sett_target_reach_time.setText("目标达成时间");
        tv_sett_step_num.setText("今日运动步数");
    }

    @Override
    protected void initLocation()
    {
        mLayoutUtil.drawViewRBLayout(iv_sett_back, 0, 362, 0, 0, 0, 0);
        mLayoutUtil.drawViewDefaultLayout(rel_settings_title, -1,
                mLayoutUtil.getWidgetHeight(362) - (int) PreferenceEntity.ScreenTop + mLayoutUtil.getWidgetHeight(215) / 2,
                -1, -1, (int) PreferenceEntity.ScreenTop, -1);
        mLayoutUtil.drawViewRBLayout(btn_sett_setting, 117, 56, 0, -1, -1, 0);
        rel_sett_info.setMinimumHeight(mLayoutUtil.getWidgetHeight(215));

        mLayoutUtil.drawViewRBLayout(iv_sett_header, 114, 114, 0, 0, -1, -1);
        mLayoutUtil.drawViewRBLayout(tv_setti_name, -1, -1, -1, -1, 52, -1);
        mLayoutUtil.drawViewRBLayout(tv_setti_phone, -1, -1, -1, -1, 42, -1);
    }

    @Override
    public void onPause()
    {
        super.onPause();
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
    protected void pauseClose()
    {
    }

    @Override
    protected void destroyClose()
    {
    }

}
