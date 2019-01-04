package com.huidf.slimming.activity.home.weight;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.huidf.slimming.entity.home.weight.UploadingDataEntity;
import com.mengii.scale.api.MengiiSDK;
import com.mengii.scale.api.OnMengiiScaleListener;
import com.mengii.scale.model.MUser;
import com.mengii.scale.model.MWeight;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import java.lang.ref.WeakReference;
import huitx.libztframework.context.ContextConstant;
import huitx.libztframework.utils.MathUtils;
import huitx.libztframework.utils.NetUtils;
import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.ToastUtils;

public class WeightBaseActivity extends BaseFragmentActivityForAnnotation implements OnClickListener, OnCheckedChangeListener, OnItemClickListener {

    /**
     * 实体类
     */
    protected UploadingDataEntity mTopicentity;

    public WeightBaseActivity( ) {
        super();
    }

    @Override
    protected void initContent() {

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        animationDrawable = (AnimationDrawable) iv_dv_connect_anim.getDrawable();
        animationDrawable.start();
    }

    /**
     * weight=26.55,
     * bmi=8.6,
     * 体脂率 fat=5.0,
     * 皮下脂肪 subFat=1.2,
     * 内脏指数 visFat=1.9,
     * 水分 water=114.6,
     * 基础代谢  bmr=638,
     * 身体年龄 bodyAge=26,
     * 肌肉 muscle=78.6,
     * 蛋白质 protein=19.3,
     * 骨骼重量 bone=8.1,
     * 身体评分 score=32
     *
     * @param weight
     */
    public void showWeight(MWeight weight) {
//        setFatStatus(weight.getFat());

//        tv_dvcr_weight_num.setText("" + weight.getWeight() + "kg");
//        tv_dvcr_pixia_num.setText(weight.getSubFat() + "%");
//        submitWeightInfo(weight.getFat(), weight.getWeight(), weight.getSubFat());
        submitWeightInfo(weight.getWeight(),1);
    }

    public void showWeight(float value, float lb, int unit) {
        LOG("" + value + "kg");
    }


    private boolean insertData = false; //标记是否正在提交数据


    /**
     * 录入体重测量值
     * @param weight
     * @param type 1自动   2手动
     */
    public void submitWeightInfo(float weight,int type) {
    //    public void submitWeightInfo(float fat, float weight, float subfat) {
        if (insertData) return;
        insertData = true;
        RequestParams params = PreferenceEntity.getLoginParams();
//        params.addBodyParameter("bodyFat", fat + "");
        params.addBodyParameter("weight", weight + "");
        params.addBodyParameter("type", type + "");
//        params.addBodyParameter("underFat", subfat + "");
        mgetNetData.GetData(this, UrlConstant.API_INSERTWEIGHJT, mHandler.POSTWEIGHTDATA, params);
        setLoading(true, "");
    }


    /**
     * 获取体重信息
     */
    public void GetNowWeight() {
        RequestParams params = PreferenceEntity.getLoginParams();
        mgetNetData.GetData(this, UrlConstant.API_GETNOWEIGHJT, mHandler.GETWEIGHTDATA, params);
        setLoading(true, "");
    }

    @Override
    public void paddingDatas(String mData, int type) {
        setLoading(false, "");
        Gson gson = new Gson();
        try {
            mTopicentity = gson.fromJson(mData, UploadingDataEntity.class);
        } catch (Exception e) {
            LOG("JSON解析异常");
            e.printStackTrace();
            if (type == mHandler.POSTWEIGHTDATA)   //提交体重测量值
                insertData = false;
            return;
        }
        if (mTopicentity.code == ContextConstant.RESPONSECODE_200) {
            if (type == mHandler.POSTWEIGHTDATA) {    //提交体重测量值
                PreferenceEntity.isRefreshHomeData = true;
//               ToastUtils.showToast(mTopicentity.msg);
                insertData = false;
                isShowDialog = true;
                GetNowWeight();
            } else if (type == mHandler.GETWEIGHTDATA) {
                setData(mTopicentity.data);
            }
        } else if (mTopicentity.code == ContextConstant.RESPONSECODE_310) {    //登录信息过时跳转到登录页
            reLoading();
        } else {
            ToastUtils.showToast(NewWidgetSetting.getInstance().filtrationStringbuffer(mTopicentity.msg, "接口信息异常！"));
        }

    }

    @Override
    public void error(String msg, int type) {
        setLoading(false, "");
        if (type == 0) {    //提交体重测量值
            insertData = false;
        }
        if (!NetUtils.isAPNType(mContext)) {
        }
    }

    private boolean isShowDialog;
    private float newWeight;
    @SuppressLint("SetTextI18n")
    protected void setData(UploadingDataEntity.Data dataEntity){
        if(dataEntity == null)  return;
        if(isShowDialog){
            isShowDialog = false;
            initDialogView((int)Float.parseFloat(dataEntity.weight),(int)Float.parseFloat(dataEntity.latestWeight));
        }
        newWeight = MathUtils.stringToFloat(dataEntity.latestWeight,66.0f);
        tv_weight_initial.setText("" + dataEntity.weight);
        NewWidgetSetting.setIdenticalLineTvColor(tv_weight_initial, mContext.getResources().getColor(R.color.text_color_hint), 0.5f , "  KG", false);
        tv_weight_current.setText("" + dataEntity.latestWeight);
        NewWidgetSetting.setIdenticalLineTvColor(tv_weight_current, mContext.getResources().getColor(R.color.text_color_hint), 0.5f , "  KG", false);
        tv_weight_target.setText("" + dataEntity.targetWeight);
        NewWidgetSetting.setIdenticalLineTvColor(tv_weight_target, mContext.getResources().getColor(R.color.text_color_hint), 0.5f , "  KG", false);
        tv_weight_initial_time.setText("开始减重时间\n" + tranTimes.convert(dataEntity.createTime,"yyyy年MM月dd日"));
        tv_weight_current_time.setText("减重第" + dataEntity.days + "天\n" + tranTimes.convert(dataEntity.latestWeightTime,"yyyy年MM月dd日"));
        tv_weight_target_time.setText("目标达成日期\n" + tranTimes.convert(dataEntity.targetTime,"yyyy年MM月dd日"));


    }

    protected MyHandler mHandler;

    protected class MyHandler extends Handler{
        protected final int BLE_START = 100;  //初始化检测
        protected final int POSTWEIGHTDATA = 10001;
        protected final int GETWEIGHTDATA = 10002;

        // SoftReference<Activity> 也可以使用软应用 只有在内存不足的时候才会被回收
        private final WeakReference<Activity> mActivity;

        protected MyHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = mActivity.get();
            if (activity != null){
                //做操作
                switch (msg.what) {
                    case BLE_START: // 初始化检测
                            if (turnOnBluetooth()) {
                                initMengiiScale();
                                scanDev(true);
                            } else {    //打开蓝牙失败
                                ToastUtils.showToast("启动蓝牙失败！");
                            }
                        break;
                    case GETWEIGHTDATA: // 获取体重信息
                        GetNowWeight();
                        break;
//                    case GETSLEEPINFO: // 提交体重
//                        submitWeightInfo();
//                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected void initLocation() {
        lin_device_weight_anim_main.setMinimumHeight(mLayoutUtil.getWidgetHeight(408));

        mLayoutUtil.drawViewDefaultLinearLayout(iv_dvca_phone, 70, 70, 0, 0, 0, 0);
        mLayoutUtil.drawViewRBLinearLayout(iv_dv_connect_anim, 238, 20, -1, -1, 0, 0);
        mLayoutUtil.drawViewRBLinearLayout(lin_dv_connect_anim_success, 238, 20, -1, -1, 0, 0);
        mLayoutUtil.drawViewRBLinearLayout(iv_dvca_success, 22, 22, -1, -1, 0, 0);
        mLayoutUtil.drawViewDefaultLinearLayout(iv_dvca_scale, 70, 70, 0, 0, 0, 0);
        mLayoutUtil.drawViewRBLinearLayout(lin_dv_connect_anim, 0, 0, 0, 0, 30, 0);
        mLayoutUtil.drawViewRBLinearLayout(tv_dv_connect_result_hint, 0, 0, 0, 0, 45, 0);
    }

    /**
     * @param state 0，正在连接；-1，断开连接；1，正在测量；2，测量成功
     */
    public void setState(int state) {
        switch (state) {
            case 0:
                tv_dv_connect_result_hint.setText("设备连接中请等待");
                if (animationDrawable.isRunning()) {
                    animationDrawable.stop();
                }
                animationDrawable.start();
                iv_dv_connect_anim.setVisibility(View.VISIBLE);
                lin_dv_connect_anim_success.setVisibility(View.GONE);
                break;
            case -1:
                tv_dv_connect_result_hint.setText("设备断开连接");
                iv_dv_connect_anim.setVisibility(View.VISIBLE);
                animationDrawable.start();
                lin_dv_connect_anim_success.setVisibility(View.GONE);
                break;
            case 1:
                tv_dv_connect_result_hint.setText("设备已连接");
                animationDrawable.stop();
                iv_dv_connect_anim.setVisibility(View.GONE);
                lin_dv_connect_anim_success.setVisibility(View.VISIBLE);
                break;
            case 2:
                tv_dv_connect_result_hint.setText("测量成功");
                animationDrawable.stop();
                iv_dv_connect_anim.setVisibility(View.GONE);
                lin_dv_connect_anim_success.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }


    @ViewInject (R.id.lin_device_weight_anim_main) protected LinearLayout lin_device_weight_anim_main;
    @ViewInject (R.id.lin_dv_connect_anim)  protected LinearLayout lin_dv_connect_anim;
    @ViewInject (R.id.iv_dvca_phone)   protected ImageView iv_dvca_phone;
    @ViewInject (R.id.iv_dv_connect_anim)  protected ImageView iv_dv_connect_anim;
    @ViewInject (R.id.iv_dvca_scale)  protected ImageView iv_dvca_scale;
    @ViewInject (R.id.lin_dv_connect_anim_success)   protected LinearLayout lin_dv_connect_anim_success;
    @ViewInject (R.id.iv_dvca_success)   protected ImageView iv_dvca_success;
    @ViewInject (R.id.tv_dv_connect_result_hint)   protected TextView tv_dv_connect_result_hint;
    protected AnimationDrawable animationDrawable;
    @ViewInject (R.id.tv_weight_initial)   protected TextView tv_weight_initial;
    @ViewInject (R.id.tv_weight_initial_time)   protected TextView tv_weight_initial_time;
    @ViewInject (R.id.tv_weight_current)   protected TextView tv_weight_current;
    @ViewInject (R.id.tv_weight_current_time)   protected TextView tv_weight_current_time;
    @ViewInject (R.id.tv_weight_target)   protected TextView tv_weight_target;
    @ViewInject (R.id.tv_weight_target_time)   protected TextView tv_weight_target_time;
    @ViewInject (R.id.tv_weight_new_target)   protected TextView tv_weight_new_target;

    @Override
    protected void initLogic() {
    }

    @Override
    public void onClick(View view) {
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

    @Override
    public void onCheckedChanged(RadioGroup arg0, int arg1) {
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    }

    //	 public DeviceEntity mDevice;
    public MUser mUserData;

    public static boolean turnOnBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter != null) {
            return bluetoothAdapter.enable();
        }

        return false;
    }

    public boolean Scanning = false;

    /**
     * 是否扫描秤
     *
     * @param state
     */
    private void scanDev(boolean state) {

        if (state) {
            if (Scanning) {
                return;
            }
//			if(mDeviceAddress==null || mDeviceAddress.equals("")){
//				tv_dv_connect_result_hint.setText("请先绑定体脂称!");
//				iv_dv_connect_anim.setVisibility(View.GONE);
//				return;
//			}
            Scanning = true;

            LOG("开始扫描体脂称");
            MengiiSDK.the().startScan();
            setState(0);
        } else {
            if (MengiiSDK.the() != null) {
                MengiiSDK.the().stopScan();
                Scanning = false;
            }

        }
    }

    /** 判断是否正在连接，是的话，停止扫描 */
    protected boolean isConnecting;
    protected boolean isInitMengiiScale;

    private void initMengiiScale() {
        if(isInitMengiiScale) return;
        isInitMengiiScale = true;
        MengiiSDK.the().setUser(mUserData);
        MengiiSDK.the().init(mContext, new OnMengiiScaleListener() {

            @Override
            public void onRealtimeWeightMesured(float value, float lb, int unit) {// 实时体重数据
                // 获取实时和稳定体重时,如果单位为ST,则参数value为ST,参数lb为LB,其他单位只取参数vaule即可,参数lb无效
                // LOG("onRealtimeWeightMesured--->>> value=" + value + "  lb=" + lb);
                setState(1);
                LOG("实时体重数据");
                showWeight(value, lb, unit);
                // 注意: 在人实时称重时,要下发人体数据,否则测出的身体数据是之前一个人的而导致不准确
                MengiiSDK.the().setUser(mUserData);
            }

            @Override
            public void onBodyParamMesured(float value, float lb, int unit, MWeight weight) {// 身体其他数据
                // LOG("onBodyParamMesured--->>> weight=" + weight.toString());
                // bmi=21.8, fat=16.1, subFat=14.6, visFat=4.0, water=64.6,
                // bmr=1496, bodyAge=26, muscle=53.4, protein=16.74, bone=3.2,
                // score=88
                // 对应:BMI,体脂率,皮下脂肪率,内脏脂肪等级,水分率,基础代谢,身体年龄,肌肉率,蛋白质,骨骼重量、身体评分

                showWeight(weight);
                LOG(weight.toString());
            }

            @Override
            public void onStableWeightMesured(float value, float lb, int unit, float weight, float resistance) {// 稳定体重数据
                // 获取实时和稳定体重时,如果单位为ST,则参数value为ST,参数lb为LB,其他单位只取参数vaule即可,参数lb无效
                // 如果resistance=0，体重秤，只有体重数据，这时可以直接保存了；resistance=1，体脂秤，等测完身体参数后保存
                // weight是KG为单位的重量，可以用来保存数据
                // LOG("onStableWeightMesured--->>> value=" + value + "  lb=" +
                // lb + "   resistance=" + resistance);
                LOG("稳定体重数据");
                setState(2);
                showWeight(value, lb, unit);
            }

            @Override
            public void onLowVoltage() {// 萌亿体脂秤低电压提醒
//				tv_dv_connect_result_hint.setText("体脂秤没有能量了，快换电池补充点能量吧^^^");
            }

            @Override
            public void onDeviceFound(BluetoothDevice device) {// 发现萌亿体脂秤
                LOG("发现体脂称--->>> address=" + device.getAddress());
//				if(mDeviceAddress.equals(device.getAddress())){
//					iv_dv_connect_anim.setVisibility(View.VISIBLE);
                if(!isConnecting) {
                    LOG("连接");
                    MengiiSDK.the().stopScan();
                    isConnecting = true;
                    MengiiSDK.the().connect(device);
                }
//					tv_dv_connect_result_hint.setText("正在连接体脂称" + "");
//				}
            }

            @Override
            public void onConnected(BluetoothDevice device) {// 萌亿体脂秤已连接
                LOG("连接成功--->>> address=" + device.getAddress());
                isConnecting = false;
//				iv_dv_connect_anim.setVisibility(View.VISIBLE);
//				tv_dv_connect_result_hint.setText("已连接");
                setState(1);
                scanDev(false);
            }

            @Override
            public void onDisconnected(BluetoothDevice device) {// 萌亿体脂秤断开连接
                LOG("断开连接--->>> address=" + device.getAddress());
                setState(-1);
//					tv_dv_connect_result_hint.setText("断开连接");
//					iv_dv_connect_anim.setVisibility(View.GONE);
                scanDev(true);
            }

            @Override
            public void onBluetoothOn() {// 蓝牙开启
                LOG("蓝牙开启");
//				tv_dv_connect_result_hint.setText("蓝牙已开启");

//				MengiiSDK.the().startScan();
                scanDev(true);
            }

            @Override
            public void onBluetoothOff() {// 蓝牙关闭
                LOG("蓝牙关闭");
//				tv_dv_connect_result_hint.setText("蓝牙已关闭");
                scanDev(false);
            }


            // 专门为智能镜子提供快速显示体重数据，不能通过App对秤切换单位，其他场景不建议使用
            @Override
            public void onSimpleStableWeightMesured(BluetoothDevice device, float value, float lb, int unit, float weight, float resistance) {
                // 获取实时和稳定体重时,如果单位为ST,则参数value为ST,参数lb为LB,其他单位只取参数vaule即可,参数lb无效
                // 如果resistance=0，体重秤，只有体重数据，这时可以直接保存了；resistance=1，体脂秤，等测完身体参数后保存
                // weight是KG为单位的重量，可以用来保存数据
                // LOG("onSimpleStableWeightMesured--->>> value=" + value +
                // "  lb=" + lb + "   address=" + device.getAddress());

                // showWeight(value, lb, unit);
            }

            @Override
            public void onSimpleRealtimeWeightMesured(BluetoothDevice arg0, float arg1, float arg2, int arg3) {
            }

            // 专门为智能镜子提供快速显示体重数据，不能通过App对秤切换单位，其他场景不建议使用
            @Override
            public void onSimpleBodyParamMesured(BluetoothDevice device, float value, float lb, int unit, MWeight weight) {
                // mTxtResult.setText(weight.toString());
            }
        });
    }

    private View mDialogView;
    protected Dialog dialog;
    private RelativeLayout rel_dialog_weight_hint;
    private ImageView iv_dialog_weight_hint;
    private TextView tv_dialog_weight_hint;
    private Button btn_dia_weight_hint;

    /**
     * @param initWeight 初始体重
     * @param currentWeight 最新体重
     */
    protected void initDialogView(int initWeight,int currentWeight) {
        LOG("初始体重： " + initWeight);
        LOG("上次体重： " + this.newWeight);
        LOG("最新体重： " + currentWeight);
        if(this.newWeight == currentWeight)return;
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (mDialogView == null) {
            mDialogView = View.inflate(this, R.layout.dialog_weight_hint, null);
            rel_dialog_weight_hint = findViewByIds(mDialogView, R.id.rel_dialog_weight_hint);
            iv_dialog_weight_hint = findViewByIds(mDialogView, R.id.iv_dialog_weight_hint);
            tv_dialog_weight_hint = findViewByIds(mDialogView, R.id.tv_dialog_weight_hint);
            btn_dia_weight_hint = findViewByIds(mDialogView, R.id.btn_dia_weight_hint);

            mLayoutUtil.drawViewRBLinearLayout(rel_dialog_weight_hint, 522, 605, 0, 0, -1, 0);
            mLayoutUtil.drawViewRBLayout(iv_dialog_weight_hint, 400, 400, 0, 0, -1, 0);
            mLayoutUtil.drawViewRBLinearLayout(btn_dia_weight_hint, 46, 46, 0, 0, -1, 0);

            btn_dia_weight_hint.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        if (dialog != null) DialogUIUtils.dismiss(dialog);
                    }
                }
            });
        }
        int difference = initWeight - currentWeight;
        if(difference > 0){
            tv_dialog_weight_hint.setText("比初始体重轻了");
        } else {
            tv_dialog_weight_hint.setText("比初始体重增加了");
        }
        NewWidgetSetting.setIdenticalLineTvColor(tv_dialog_weight_hint, mContext.getResources().getColor(R.color.weight_main_color),
                1.6f , "  " + Math.abs(difference), false);
        NewWidgetSetting.setIdenticalLineTvColor(tv_dialog_weight_hint, -999,
                1 , "  （kg）", false);


        if (dialog == null)
            dialog = DialogUIUtils.showCustomAlert(this, mDialogView, Gravity.CENTER, true, false).show();
        else dialog.show();

    }

}
