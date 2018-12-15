package com.huidf.slimming.activity.personal_center.set;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huidf.slimming.R;
import com.huidf.slimming.base.BaseFragmentActivity;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.context.UrlConstant;
import com.huidf.slimming.entity.user.UserEntity;

import org.xutils.http.RequestParams;

import java.io.File;
import java.util.HashMap;

import huitx.libztframework.context.ContextConstant;
import huitx.libztframework.interf.ConsultNet;
import huitx.libztframework.utils.NetUtils;
import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.PreferencesUtils;
import huitx.libztframework.utils.ToastUtils;
import huitx.libztframework.utils.WidgetSetting;

public class UserSetBaseActivity extends BaseFragmentActivity implements OnClickListener, ConsultNet {

    public UserEntity mUserEntity;
    /**
     * 修改头像
     */
    public static final int Intent_Photo_100 = 100;
    /**
     * 1男 2女
     */
    public String userSex = "-1";

    /**
     * 用户修改头像的地址
     */
    public String userHeader;
    public String userNick;
    protected final int GETUSERINFO = 101;
    protected final int UPUSERINFO = 102;
    protected final int EDITHEADER = 103;

    public UserSetBaseActivity(int layoutId)
    {
        super(layoutId);
    }

    @Override
    protected void initHead()
    {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void initContent()
    {
        findView();
        setData();
    }

    public void setData()
    {
        String account = PreferencesUtils.getString(mContext, PreferenceEntity.KEY_USER_ACCOUNT);
        NewWidgetSetting.setViewText(tv_uspn_value, account, "点击绑定手机号");

    }

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
//            if (type == GETUSERINFO) {    //获取用户信息
//                Message message = Message.obtain();
//                message.what = type;
//                message.obj = mUserEntity.data;
//                mHandler.sendMessage(message);
//            } else if (type == UPUSERINFO) {    //修改用户信息
//                if (userNick != null && !userNick.equals("")) {
//                    tv_uspn_value.setText(userNick);
//                    PreferencesUtils.putString(mContext, PreferenceEntity.KEY_USER_NICK, userNick + "");
//                }
//            } else if (type == EDITHEADER) {    //修改头像
//                imageLoader_base.displayImage(userHeader, iv_user_info_header,
//                        ImageLoaderUtils.setImageOptionsLoadImg(mContext.getResources().getDrawable(R.drawable.iv_man_bef), 2),
//                        animateFirstListener_base);
//                PreferencesUtils.putString(mContext, PreferenceEntity.KEY_USER_HEADER, userHeader);
//            }
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
        if (!NetUtils.isAPNType(mContext)) {    //没网

        } else {
//			ToastUtils.showToast("操作失败，请稍候重试！");

        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg)
        {

            switch (msg.what) {
                case GETUSERINFO:    //获取用户信息

                    UserEntity.Data user_Entity = (UserEntity.Data) msg.obj;

                    WidgetSetting.setViewText(tv_uspn_value, user_Entity.name, "未设置");
                    if (user_Entity.sex != null) {
                        PreferencesUtils.putString(getApplicationContext(), PreferenceEntity.KEY_USER_SEX, user_Entity.sex);
                        if (user_Entity.sex.equals("1")) {    //男
                            userSex = 1 + "";
//						rb_user_setting_sex_man.setChecked(true);
                        } else {
                            userSex = 2 + "";
//						rb_user_setting_sex_woman.setChecked(true);
                        }
                    }
//                    WidgetSetting.setViewText(tv_uswechat_state, user_Entity.birthday == null ? "未设置" : tranTimes.convert(user_Entity.birthday, "yyyy年M月"), "未设置");
//                    WidgetSetting.setViewText(tv_user_info_height_value, "cm", user_Entity.height, "00", false);
//                    WidgetSetting.setViewText(tv_user_info_start_weight_value, "kg", user_Entity.weight, "00", false);

                    break;
                case 1:

                    break;
            }
        }

    };


    /**
     * 获取用户的信息
     */
    public void GetUserInfo()
    {
        RequestParams params = PreferenceEntity.getLoginParams();
        mgetNetData.GetData(this, UrlConstant.GET_PERSONAL_CENTER, GETUSERINFO, params);
        setLoading(true, "");
    }

    /**
     * 上传用户信息
     */
    public void updateUserInfo(String name, String value)
    {
        RequestParams params = PreferenceEntity.getLoginParams();
        params.addBodyParameter(name, value);
        mgetNetData.GetData(this, UrlConstant.API_UPDATEPERSONAL, UPUSERINFO, params);
        setLoading(true, "");
    }

    /**
     * 上传用户信息
     */
    public void updateUserInfo(HashMap<String, Integer> maps)
    {
        RequestParams params = PreferenceEntity.getLoginParams();
        params.addBodyParameter("isDiabetes", "" + maps.get("isDiabetes"));
        params.addBodyParameter("isPressureDisease", "" + maps.get("isPressureDisease"));
        params.addBodyParameter("isKedneyDisease", "" + maps.get("isKedneyDisease"));
        mgetNetData.GetData(this, UrlConstant.API_UPDATEPERSONAL, UPUSERINFO, params);
        setLoading(true, "");
    }

    /**
     * 修改头像
     */
    public void uploadingCredentials(String path)
    {
        RequestParams params = PreferenceEntity.getLoginParams();
        File file = new File(path);
        if (file.exists() && file.length() > 0) {
            params.addBodyParameter("header", file);
        } else {
            ToastUtils.showToast("头像选择失败，请重试！");
            return;
        }
        mgetNetData.GetData(this, UrlConstant.API_USER_CHANGEHEADER, EDITHEADER, params);
        setLoading(true, "");
    }

    @Override
    protected void initLocation()
    {
        tv_user_set_logout.setMinWidth(mLayoutUtil.getWidgetWidth(332));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void findView()
    {
        lin_user_set_phone_number = findViewByIds(R.id.lin_user_set_phone_number);
        tv_uspn_value = findViewByIds(R.id.tv_uspn_value);
        lin_user_set_clear_cache = findViewByIds(R.id.lin_user_set_clear_cache);
        lin_user_set_wechat = findViewByIds(R.id.lin_user_set_wechat);
        lin_user_set_qq = findViewByIds(R.id.lin_user_set_qq);
        tv_uswechat_state = findViewByIds(R.id.tv_uswechat_state);
        tv_usqq_state = findViewByIds(R.id.tv_usqq_state);
        lin_user_set_question = findViewByIds(R.id.lin_user_set_question);
        lin_user_set_about_us = findViewByIds(R.id.lin_user_set_about_us);
        tv_user_set_logout = findViewByIds(R.id.tv_user_set_logout);

        lin_user_set_phone_number.setOnClickListener(this);
        lin_user_set_clear_cache.setOnClickListener(this);
        lin_user_set_qq.setOnClickListener(this);
        lin_user_set_question.setOnClickListener(this);
        lin_user_set_about_us.setOnClickListener(this);
        lin_user_set_wechat.setOnClickListener(this);
        tv_user_set_logout.setOnClickListener(this);
    }


    private LinearLayout lin_user_set_phone_number;
    private TextView tv_uspn_value;
    private TextView tv_uswechat_state;
    private TextView tv_usqq_state;
    private LinearLayout lin_user_set_clear_cache;
    private LinearLayout lin_user_set_wechat;
    private LinearLayout lin_user_set_qq;
    private LinearLayout lin_user_set_question;
    private LinearLayout lin_user_set_about_us;
    private TextView tv_user_set_logout;

    @Override
    protected void initLogic()
    {
    }

    @Override
    protected void destroyClose()
    {
    }

    @Override
    protected void pauseClose()
    {
    }

    @Override
    public void onClick(View arg0)
    {
    }




}
