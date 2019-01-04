package com.huidf.slimming.activity.personal_center;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.huidf.slimming.R;
import com.huidf.slimming.activity.personal_center.change_phone_number.ChangePhoneNumberActivity;
import com.huidf.slimming.activity.personal_center.select_photo.SelectPhotoActivity;
import com.huidf.slimming.activity.user.SelLoginActivity;
import com.huidf.slimming.context.ApplicationData;

import java.io.File;

import huitx.libztframework.utils.BitmapUtils;
import huitx.libztframework.utils.NetUtils;
import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.ToastUtils;

/**
 * @author : Zhutao
 * @version 创建时间：@2017年1月16日
 * @Description: 个人中心，个人设置
 * @params：
 */
public class UserInfoActivity extends UserInfoBaseActivity {

    private static final int CHANGEPHONENUMBER = 7;

    public UserInfoActivity() {
        super(R.layout.activity_user_info);
        TAG = this.getClass().getName();
    }

    @Override
    protected void initHead() {
        super.initHead();
        setStatusBarColor(true, mContext.getResources().getColor(R.color.status_bar_color));
        setTittle("个人资料");
//        islogin = getIntent().getBooleanExtra("islogin", false);
    }

    @Override
    protected void initLogic() {
        super.initLogic();
        GetUserInfo();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        Intent intent;
        switch (view.getId()) {
//            case R.id.btn_title_view_left:
//                if (islogin) {
//                    intent = new Intent(this, SelLoginActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    onBackPressed();
//                }
//                break;
            case R.id.lin_user_info_header:
                getPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                        "拍照或者选择本地图片，如果拒绝此权限，将无法正常进行头像修改！","拒绝权限，无法修改头像",READ_EXTERNAL_STORAGEDATA);
                break;
            case R.id.btn_user_info_name_clear:    //清空修改姓名内容信息
                et_user_info_name.setText("");
                break;
            case R.id.lin_user_info_phone:    //修改手机号
                intent = new Intent(this, ChangePhoneNumberActivity.class);
                startActivityForResult(intent, CHANGEPHONENUMBER);
                break;
            case R.id.tv_user_info_commit:    //修改完信息后，点击提交按钮，提交内容
                userNick = et_user_info_name.getText().toString();
                updateUserInfo("name", userNick);
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
                        uri = BitmapUtils.compressImg(uri);
                        File file = new File(uri);
                        if (file.exists() && file.length() > 0) {
                            userHeader = "file://" + uri;
                            try {
                                RequestOptions options = new RequestOptions()
                                        .placeholder(R.drawable.iv_man_bef)
                                        .circleCrop();
                                Glide.with(mContext).load(userHeader).apply(options).into(iv_user_info_header);

                                uploadingCredentials(uri);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            ToastUtils.showToast("图片选择失败，请重新选择！");
                            return;
                        }
                    }
                } else if (resultCode == 300) {// 取消
                    LOG("取消了");
                }
                break;
//            case 6:
//                if (data != null) {  //获取到昵称！
//                    String user_nick = data.getStringExtra("user_nick");//获取到的昵称！
//                    if (user_nick != null && !user_nick.equals("")) {
//                        userNick = user_nick;
//                        updateUserInfo("name", user_nick);
//                    }
//
//                }
//                break;
            case CHANGEPHONENUMBER: //修改绑定的手机号
                if (resultCode == 200) {
                    setData(null);
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

    private int READ_EXTERNAL_STORAGEDATA = 100;
    private int WRITE_EXTERNAL_STORAGEDATA = 101;
    private int CAMERADATA = 102;
    private String mPermission;
    private String mPermissionHint;
    private String mPermissionToastHint;
    private int onActivityResultCode;

    /**
     * WRITE_EXTERNAL_STORAGE
     * @param permission
     */
    private void getPermission(final String permission, String hint, String toastHint, final int onActivityResultCode)
    {
        this.mPermission = permission;
        this.mPermissionHint = hint;
        this.mPermissionToastHint = toastHint;
        this.onActivityResultCode = onActivityResultCode;
        // 判断没有获取权限的话，申请获取权限
        if (ActivityCompat.checkSelfPermission(this, mPermission) != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, mPermission)){
                // 向用户详细解释申请该权限的原因
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage(mPermissionHint)
                        .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(UserInfoActivity.this,
                                        new String[]{mPermission}, onActivityResultCode);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("不给", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ToastUtils.showToast(mPermissionToastHint);
                            }
                        })
                        .show();
            }else{
                ActivityCompat.requestPermissions(UserInfoActivity.this,
                        new String[]{mPermission}, onActivityResultCode);
            }

        } else {
            if(onActivityResultCode == READ_EXTERNAL_STORAGEDATA){
                getPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        "编辑照片时，需要获取存储卡写入权限，如果拒绝此权限，将无法正常进行头像修改！","拒绝权限，无法修改头像",WRITE_EXTERNAL_STORAGEDATA);
            }else if(onActivityResultCode == WRITE_EXTERNAL_STORAGEDATA){
                getPermission(Manifest.permission.CAMERA,
                        "需要获取拍照权限，如果拒绝此权限，将无法正常进行头像修改！","拒绝权限，无法修改头像",CAMERADATA);
            }else {
                hasPermission();
            }
        }
    }

    private void hasPermission(){
        // 授予权限
        Intent intent = new Intent(this, SelectPhotoActivity.class);
        intent.putExtra("intent_title", "修改头像");
        startActivityForResult(intent, Intent_Photo_100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode == READ_EXTERNAL_STORAGEDATA){
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        "拍照时需要获取存储卡写入权限，如果拒绝此权限，将无法正常进行头像修改！","拒绝权限，无法修改头像",WRITE_EXTERNAL_STORAGEDATA);
            } else {
                getPermission(mPermission,mPermissionHint,mPermissionToastHint,onActivityResultCode);
            }
        }else if(requestCode == WRITE_EXTERNAL_STORAGEDATA){
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPermission(Manifest.permission.CAMERA,
                        "需要获取拍照权限，如果拒绝此权限，将无法正常进行头像修改！","拒绝权限，无法修改头像",CAMERADATA);
            } else {
                getPermission(mPermission,mPermissionHint,mPermissionToastHint,onActivityResultCode);
            }

        }else if(requestCode == WRITE_EXTERNAL_STORAGEDATA){
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasPermission();
            } else {
                getPermission(mPermission,mPermissionHint,mPermissionToastHint,onActivityResultCode);
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
