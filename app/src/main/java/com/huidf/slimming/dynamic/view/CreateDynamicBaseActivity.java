package com.huidf.slimming.dynamic.view;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huidf.slimming.R;
import com.huidf.slimming.base.BaseActivity;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.context.UrlConstant;
import com.huidf.slimming.dynamic.adapter.DynamicPhotoAdapter;
import com.huidf.slimming.dynamic.model.CreDynSelectPicEntity;
import com.huidf.slimming.entity.ranking.RankingEntity;
import com.huidf.slimming.entity.user.UserEntity;

import org.greenrobot.eventbus.EventBus;
import org.xutils.http.RequestParams;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;

import huitx.libztframework.context.ContextConstant;
import huitx.libztframework.utils.NetUtils;
import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.ToastUtils;


public class CreateDynamicBaseActivity extends BaseActivity implements OnClickListener, DynamicPhotoAdapter.OnPictureOperateInterface {

    /**
     * 输入框
     */
    protected EditText ET_InputView;
    //************************图片选择布局
    private LinearLayout lin_selpic_photo, lin_selpic_camera;
    private RecyclerView mRecyclerView;
    private TextView tv_selpic_nums;
    protected DynamicPhotoAdapter mAdapter;

    //上传图片
    protected String selectedImagePath = "", userHeader = "";

    public CreateDynamicBaseActivity(int layoutId) {
        super(layoutId);
    }

    @Override
    public void onPictureOperate(int PicListSize) {
        tv_selpic_nums.setText(String.format(mContext.getResources().getString(R.string.picture_picture_num), PicListSize, 9));
    }

    @Override
    protected void initContent() {
        ET_InputView = findViewById(R.id.et_cd_input);
        lin_selpic_camera = findViewById(R.id.lin_selpic_camera);
        lin_selpic_photo = findViewById(R.id.lin_selpic_photo);
        mRecyclerView = findViewById(R.id.recy_selpic_list);
        tv_selpic_nums = findViewById(R.id.tv_selpic_nums);

        lin_selpic_camera.setOnClickListener(this);
        lin_selpic_photo.setOnClickListener(this);

        onPictureOperate(0);
        initRecyclerView();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initRecyclerView() {
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        LinkedList<RankingEntity.Data.RankingInfo> mList = new LinkedList<>();
        mAdapter = new DynamicPhotoAdapter(mContext, this, null);
        mRecyclerView.setAdapter(mAdapter);
    }

    private final int UPLOADINGPICTURE = 100;
    private final int SENDDYNAMIC = 101;

    //上传图片
    protected void uploadingCredentials(String path) {
        RequestParams params = PreferenceEntity.getLoginParams();
        File file = new File(path);
        if (file.exists() && file.length() > 0) {
            params.addBodyParameter("pic", file);
        } else {
            ToastUtils.showToast("图片选择失败，请重试！");
            return;
        }
        HashMap map = PreferenceEntity.getLoginData();
        params.addBodyParameter("uId", map.get("id") + "");
        mgetNetData.GetData(this, UrlConstant.API_UPLOADINGPICTURE, UPLOADINGPICTURE, params);
        setLoading(true, "");
    }

    //发送动态
    protected void sendDynamic() {
        if(ET_InputView.getText().length()<1){
            ToastUtils.showToast("请输入内容");
            return;
        }
        if(mAdapter.getDataSize() < 1){
            ToastUtils.showToast("最少选择一张图片");
            return;
        }
        HashMap map = PreferenceEntity.getLoginData();
        RequestParams params = PreferenceEntity.getLoginParams();
        params.addBodyParameter("content", ET_InputView.getText().toString());
        LOG("mAdapter.getPictureUrls()：" + mAdapter.getPictureUrls());
        params.addBodyParameter("piclist", mAdapter.getPictureUrls());
        params.addBodyParameter("uId", map.get("id") + "");
        mgetNetData.GetData(this, UrlConstant.API_SENDDYNAMIC, SENDDYNAMIC, params);
        setLoading(true, "");
    }


    @Override
    public void paddingDatas(String mData, int type) {
        setLoading(false, "");
        Gson mGson = new Gson();
        UserEntity mUserEntity;
        try {
            mUserEntity = mGson.fromJson(mData, UserEntity.class);
        } catch (Exception e) {
            return;
        }
        if (mUserEntity.code == ContextConstant.RESPONSECODE_200) {
            if (type == UPLOADINGPICTURE) {    //上传图片
                LinkedList<CreDynSelectPicEntity> mLists = new LinkedList<>();
                CreDynSelectPicEntity userEntity = new CreDynSelectPicEntity(userHeader);
                userEntity.setUrl(mUserEntity.data.img);
                mLists.add(userEntity);
                mAdapter.addData(mLists);
            }else   if (type == SENDDYNAMIC) {  //发帖
                ToastUtils.showToast("发帖成功");
                EventBus.getDefault().post(true);
                finish();
            }

        } else if (mUserEntity.code == ContextConstant.RESPONSECODE_310) {    //登录信息过时跳转到登录页
            reLoading();
        } else {
            ToastUtils.showToast(NewWidgetSetting.getInstance().filtrationStringbuffer(mUserEntity.msg, "接口信息异常！"));
        }
    }

    @Override
    public void error(String msg, int type) {
        setLoading(false, "");
        if (NetUtils.isAPNType(mContext)) {    //没网
            if (type == SENDDYNAMIC) {
//                ToastUtils.showToast("发布动态失败");
            }
        }
    }


    protected MyHandler mHandler;

    @Override
    public void onClick(View v) {

    }

    protected class MyHandler extends Handler {
        protected final int ANIMATION_START = 100;  //启动倒计时背景渲染动画

        private final WeakReference<Activity> mActivity;

        protected MyHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case ANIMATION_START:

                        break;
                }
            }
            super.handleMessage(msg);
        }
    }


    @Override
    protected void initHead() {
    }

    @Override
    protected void pauseClose() {
    }

    @Override
    protected void destroyClose() {
    }

}
