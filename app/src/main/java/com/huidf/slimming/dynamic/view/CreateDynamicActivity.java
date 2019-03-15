package com.huidf.slimming.dynamic.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;

import com.huidf.slimming.R;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.dynamic.model.CreDynSelectPicEntity;
import com.huidf.slimming.util.ProviderUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import huitx.libztframework.utils.BitmapUtils;
import huitx.libztframework.utils.ToastUtils;


/**
 * 发布动态
 * @author ZhuTao
 * @date 2018/12/5 
 * @params 
*/
public class CreateDynamicActivity extends CreateDynamicBaseActivity {

   public CreateDynamicActivity() {
       super(R.layout.activity_create_dynamic);
       TAG = getClass().getSimpleName();
   }

   @Override
   protected void initHead() {
       setStatusBarColor(false, true, mContext.getResources().getColor(R.color.white));
       setTittle("发布动态");
       setRightButtonText("发送",R.color.main_color);
       mBtnRight.setOnClickListener(this);
       if(mHandler == null) mHandler = new MyHandler(CreateDynamicActivity.this);
   }

    @Override
    protected void initContent() {
        super.initContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_selpic_camera: //拍照
                requestPermissionsIfAboveM(Picture_Camera);
                break;
            case R.id.lin_selpic_photo: //相册
                requestPermissionsIfAboveM(Picture_Photo);
                break;
            case R.id.btn_title_view_right:	//发送
                sendDynamic();
                break;

        }
    }

    /* 拍照的照片存储位置 */
    private final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + PreferenceEntity.KEY_CACHE_PATH);
    private String fileName = "";
    protected File mCurrentPhotoFile; // 照相机拍照得到的图片

    /** 用来标识请求照相功能的activity */
    protected static final int CAMERA_WITH_DATA = 1001;
    protected static final int PHOTO_WITH_DATA = 1002;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_WITH_DATA) {  //拍照

            if (mCurrentPhotoFile != null && mCurrentPhotoFile.exists() && mCurrentPhotoFile.length() > 0) {
                selectedImagePath = mCurrentPhotoFile.getAbsolutePath();
                if (!selectedImagePath.equals("")) {
                    selectedImagePath = BitmapUtils.compressImg(selectedImagePath);
                }
            }else{	//没有获取到照片
                LOG("mCurrentPhotoFile != null: " + "mCurrentPhotoFile = null");
            }
        }else if(requestCode == PHOTO_WITH_DATA){    //相册
                if(data!=null){
                    Uri uri = data.getData();
                    selectedImagePath = getPath(uri);
                    selectedImagePath = BitmapUtils.compressImg(selectedImagePath);
                    LOG("selectedImagePath: " + selectedImagePath);
                    if (TextUtils.isEmpty(selectedImagePath)) {		//没有获取到图片
                        return;
                    }
                }
        }

        File file = new File(selectedImagePath);
        if (file.exists() && file.length() > 0) {
            userHeader = "file://" + selectedImagePath;
            try {
                uploadingCredentials(selectedImagePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ToastUtils.showToast("图片选择失败，请重新选择！");
            return;
        }

    }
    /**
     *
     * 获取访问SD卡中图片路径
     *
     * @return
     */
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void hasPermission(){
        // 授予权限
//        Intent intent = new Intent(this, SelectSinglePhotoActivity.class);
//        intent.putExtra("intent_title", "选择照片");
//        startActivityForResult(intent, Intent_Photo_100);

        if(PictureState == Picture_Photo){
            Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PHOTO_WITH_DATA);
        }
        else if(PictureState == Picture_Camera){
            String status = Environment.getExternalStorageState();
            if (!status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
                ToastUtils.showToast("SD卡状态异常，暂时无法拍照！");
                return;
            }

            if (!PHOTO_DIR.exists()) {
                boolean iscreat = PHOTO_DIR.mkdirs();// 创建照片的存储目录
                LOG("创建存放头像文件夹 = " + iscreat);
            }
            fileName = System.currentTimeMillis() + ".jpg";
            mCurrentPhotoFile = new File(PHOTO_DIR, fileName);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
            Uri uri;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
                uri = Uri.fromFile(mCurrentPhotoFile);

            }else{
                /**
                 * 7.0 调用系统相机拍照不再允许使用Uri方式，应该替换为FileProvider
                 * 并且这样可以解决MIUI系统上拍照返回size为0的情况
                 */
                uri = FileProvider.getUriForFile(CreateDynamicActivity.this,
                        ProviderUtil.getFileProviderName(), mCurrentPhotoFile);
            }
//				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, CAMERA_WITH_DATA);
        }
    }

    @Override
   protected void pauseClose() {
       super.pauseClose();
   }

   @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
   @Override
   protected void destroyClose() {
       super.destroyClose();
       if(mHandler != null) mHandler.removeCallbacksAndMessages(null);

   }

    private static final int REQUEST_CODE_PERMISSION = 1,Picture_Camera = 100,Picture_Photo = 101;
    private int PictureState = Picture_Camera;
    private Map<String, String> permissionHintMap = new HashMap<>();
    private void requestPermissionsIfAboveM(int pictureState) {
        if(mAdapter.getDataSize() >= 9 ){
            ToastUtils.showToast("最多只能添加9张图片");
            return;
        }
        PictureState = pictureState;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Map<String, String> requiredPermissions = new HashMap<>();
            requiredPermissions.put(Manifest.permission.READ_EXTERNAL_STORAGE, "读取");
            requiredPermissions.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储");
            requiredPermissions.put(Manifest.permission.CAMERA, "相机");
            for (String permission : requiredPermissions.keySet()) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionHintMap.put(permission, requiredPermissions.get(permission));
                }
            }
            if (!permissionHintMap.isEmpty()) {
                //申请权限
                requestPermissions(permissionHintMap.keySet().toArray(new String[0]), REQUEST_CODE_PERMISSION);
            }else{
                hasPermission();
            }
        }else{
            hasPermission();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<String> failPermissions = new LinkedList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                failPermissions.add(permissions[i]);
            }
        }
        if (!failPermissions.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String permission : failPermissions) {
                sb.append(permissionHintMap.get(permission)).append("、");
            }
            sb.deleteCharAt(sb.length() - 1);
            String hint = "未授予必要权限: " + sb.toString() + "，请前往设置页面开启权限";
            new AlertDialog.Builder(this)
                    .setMessage(hint)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    }).setPositiveButton("设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    System.exit(0);
                }
            }).show();
        }else{
            hasPermission();
        }
    }

}
