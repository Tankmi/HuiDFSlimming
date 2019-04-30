package com.huidf.slimming.activity.personal_center.set;

import android.app.Application;
import android.content.Intent;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;

import com.huidf.slimming.R;
import com.huidf.slimming.activity.personal_center.change_phone_number.ChangePhoneNumberActivity;
import com.huidf.slimming.activity.personal_center.joingroup.InputActivity;
import com.huidf.slimming.activity.personal_center.select_photo.SelectPhotoActivity;
import com.huidf.slimming.activity.user.SelLoginActivity;
import com.huidf.slimming.context.ApplicationData;
import com.huidf.slimming.context.HtmlUrlConstant;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.web.activity.WebViewActivity;

import java.io.File;

import huitx.libztframework.utils.BitmapUtils;
import huitx.libztframework.utils.FileUtils;
import huitx.libztframework.utils.ToastUtils;

import static com.huidf.slimming.R.id.et_user_info_name;

/**
 * 设置
 * @author ZhuTao
 * @date 2018/11/30 
 * @params 
*/

public class UserSetActivity extends UserSetBaseActivity {

    private static final int CHANGEPHONENUMBER = 7;
    
    public UserSetActivity() {
        super(R.layout.activity_user_set);
        TAG = this.getClass().getName();
    }

    @Override
    protected void initHead() {
        super.initHead();
        setStatusBarColor(true, mContext.getResources().getColor(R.color.status_bar_color));
        setTittle("设置");
    }

    @Override
    protected void initLogic() {
        super.initLogic();
        GetUserInfo();
    }
    
    @Override
    public void onClick(View view) {
        super.onClick(view);
        Intent intent;
        switch (view.getId()) {
            case R.id.lin_user_set_phone_number:    //修改手机号
                intent = new Intent(this, ChangePhoneNumberActivity.class);
                startActivityForResult(intent, CHANGEPHONENUMBER);
                break;
            case R.id.lin_user_set_clear_cache:    //清除缓存
                 boolean file = FileUtils.deleteDir(Environment.getExternalStorageDirectory() + PreferenceEntity.KEY_CACHE_PATH,false);
                 if (file) {
                 ToastUtils.showToast("清除缓存成功！");
                 }else {
                 ToastUtils.showToast("清除缓存失败!");
                 }
                break;
            case R.id.lin_user_set_wechat:    //微信
//                intent = new Intent(mContext, SetNickFragmentActivity.class);
//                startActivityForResult(intent, 6);
                break;
            case R.id.lin_user_set_qq:    //QQ
//                intent = new Intent(mContext, SetNickFragmentActivity.class);
//                startActivityForResult(intent, 6);
                break;
            case R.id.lin_user_set_question:    //常见问题 / 问题反馈
                intent = new Intent(this, InputActivity.class);
                intent.putExtra("type",2);
                startActivity(intent);
                break;
            case R.id.lin_user_set_about_us:    //关于我们
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", HtmlUrlConstant.HTML_ABOUTUS );
                intent.putExtra("is_refresh", false);
                startActivity(intent);
                break;
            case R.id.tv_user_set_logout:    //退出登录
                PreferenceEntity.clearData();
                ApplicationData.getInstance().exit();
                PreferenceEntity.isLogin = false;
                intent = new Intent(this, SelLoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Intent_Photo_100: // 设置 头像
                if (resultCode == 200) {
                    String uri = data.getExtras().getString("intent_photo_uri");
                    if (!uri.equals("")) {
                        uri = BitmapUtils.compressImg(uri,480,850);
                        File file = new File(uri);
                        if (file.exists() && file.length() > 0) {
                            userHeader = "file://" + uri;
                            uploadingCredentials(uri);
                        } else {
                            ToastUtils.showToast("图片选择失败，请重新选择！");
                            return;
                        }
                    }
                } else if (resultCode == 300) {// 取消
                    LOG("取消了");
                }
                break;
            case 6:
                if (data != null) {  //获取到昵称！
                    String user_nick = data.getStringExtra("user_nick");//获取到的昵称！
                    if (user_nick != null && !user_nick.equals("")) {
                        userNick = user_nick;
                        updateUserInfo("name", user_nick);
                    }

                }
                break;
            case CHANGEPHONENUMBER: //修改绑定的手机号
                if (resultCode == 200) { 
                    setData();
                }
                break;
        }
    }

    @Override
    protected void pauseClose() {
        super.pauseClose();
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//            ShowOrHidePrefectInfoView(false);
//            return true;
//        }
//        return super.onKeyDown(event.getKeyCode(), event);
//    }

}
