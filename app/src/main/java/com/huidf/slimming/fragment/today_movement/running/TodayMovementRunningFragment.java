package com.huidf.slimming.fragment.today_movement.running;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.amap.api.maps.MapView;
import com.huidf.slimming.R;
import com.huidf.slimming.activity.toady_movement.run.RunActivity;
import com.huidf.slimming.base.BaseFragment;

import huitx.libztframework.utils.ToastUtils;

public class TodayMovementRunningFragment extends BaseFragment implements
		OnClickListener {

	public TodayMovementRunningFragment() {
		super(R.layout.fragment_movement_running);
	}

	@Override
	protected void initContent() {
		findView();

		mMapView.onCreate(null);
	}


	protected final int GETUSERINFO = 101;

	@Override
	public void paddingDatas(String mData, int type) {
	}

	@Override
	public void error(String msg, int type) {
	}

	@Override
	public void onResume()
	{
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void initHead() {
	}


	@Override
	protected void initLogic() {

	}

    private MapView mMapView;
    private TextView tv_movement_mapview;


	/**
	 * 初始化视图控件 添加监听事件
	 */
    protected void findView() {
		mMapView = findViewByIds(R.id.map_movement_mapview);
		tv_movement_mapview = findViewByIds(R.id.tv_movement_mapview);


		tv_movement_mapview.setOnClickListener(this);
	}

	@Override
	protected void initLocation() {
    	mLayoutUtil.drawViewRBLayout(tv_movement_mapview, 190, 190, 0,0,-1,-1);
	}

	@Override
	public void onClick(View view) {
		ObjectAnimator mObjAniX = ObjectAnimator.ofFloat(tv_movement_mapview, "scaleX",1.0f,1.2f, 1.0f);
		ObjectAnimator mObjAniY = ObjectAnimator.ofFloat(tv_movement_mapview, "scaleY",1.0f,1.2f, 1.0f);
		AnimatorSet animSet = new AnimatorSet();
		animSet.play(mObjAniX).with(mObjAniY);
		animSet.setDuration(200);
		animSet.start();

		animSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation)
			{
				super.onAnimationEnd(animation);
				getPermission(str1, hint1,Refusehint,REQUEST1);
			}
		});
//		setStartRunState();
	}

	private boolean state;
	@Override
	protected void pauseClose() {
		mMapView.onPause();
	}

	@Override
	protected void destroyClose() {
		mMapView.onDestroy();
	}


	private  String str1 = Manifest.permission.ACCESS_COARSE_LOCATION,
			str2 = Manifest.permission.ACCESS_FINE_LOCATION,
			str3 = Manifest.permission.WRITE_EXTERNAL_STORAGE,
			str4 = Manifest.permission.READ_EXTERNAL_STORAGE,
			str5 = Manifest.permission.READ_PHONE_STATE;
	private int REQUEST1 = 100,REQUEST2 = 101,
			REQUEST3 = 102,REQUEST4 = 103, REQUEST5 = 104;
	private  String hint1 = "跑步时需要获取定位权限，用以实时记录运动信息";
	private  String hint2 = "跑步时需要获取定位权限，用以实时记录运动信息";
	private  String hint3 = "需要获取内存卡读写权限，用以保证运动信息及时保存";
	private  String hint4 = "需要获取内存卡读写权限，用以保证运动信息及时保存";
	private  String hint5 = "需要获取手机状态权限，用以实时记录运动信息";

	private  String Refusehint = "您拒绝了权限，设备将无法正常定位，但您还可以通过手机传感器记录运动信息。";

	private String mPermission;	//申请的权限名称
	private String mPermissionHint;	//弹框提示，写明权限的用意
	private String mPermissionToastHint;	//拒绝权限后，提示无法继续操作
	private int onActivityResultCode;	//权限对应回调

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
		if (ActivityCompat.checkSelfPermission(getActivity(), mPermission) != PackageManager.PERMISSION_GRANTED) {

			if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), mPermission)){
				// 向用户详细解释申请该权限的原因
				new android.app.AlertDialog.Builder(getActivity())
						.setCancelable(false)
						.setMessage(mPermissionHint)
						.setPositiveButton("好的", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ActivityCompat.requestPermissions(getActivity(),
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
				ActivityCompat.requestPermissions(getActivity(),
						new String[]{mPermission}, onActivityResultCode);
			}

		} else {
			if(onActivityResultCode == REQUEST1){
				getPermission(str2, hint2,Refusehint,REQUEST2);
			}else if(onActivityResultCode == REQUEST2){
				getPermission(str3, hint3,Refusehint,REQUEST3);
			}else if(onActivityResultCode == REQUEST3){
				getPermission(str4, hint4,Refusehint,REQUEST4);
			}else if(onActivityResultCode == REQUEST4){
				getPermission(str5, hint5,Refusehint,REQUEST5);
			}else {
				hasPermission();
			}
		}
	}

	private void hasPermission(){
		getActivity().startActivity(new Intent(getActivity(),RunActivity.class));
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				if(onActivityResultCode == REQUEST1){
					getPermission(str2, hint2,Refusehint,REQUEST2);
				}else if(onActivityResultCode == REQUEST2){
					getPermission(str3, hint3,Refusehint,REQUEST3);
				}else if(onActivityResultCode == REQUEST3){
					getPermission(str4, hint4,Refusehint,REQUEST4);
				}else if(onActivityResultCode == REQUEST4){
					getPermission(str5, hint5,Refusehint,REQUEST5);
				}else {
					hasPermission();
				}
			} else {
				getPermission(mPermission,mPermissionHint,mPermissionToastHint,onActivityResultCode);
			}
//		if(requestCode == ACCESSCOREASELOCATION){
//			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//				getPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
//						"拍照时需要获取存储卡写入权限，如果拒绝此权限，将无法正常进行头像修改！","拒绝权限，无法修改头像", WRITEEXTERNALSTORAGE);
//			} else {
//				getPermission(mPermission,mPermissionHint,mPermissionToastHint,onActivityResultCode);
//			}
//		}else if(requestCode == WRITEEXTERNALSTORAGE){
//			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//				getPermission(Manifest.permission.CAMERA,
//						"需要获取拍照权限，如果拒绝此权限，将无法正常进行头像修改！","拒绝权限，无法修改头像",CAMERADATA);
//			} else {
//				getPermission(mPermission,mPermissionHint,mPermissionToastHint,onActivityResultCode);
//			}
//
//		}else if(requestCode == WRITEEXTERNALSTORAGE){
//			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//				hasPermission();
//			} else {
//				getPermission(mPermission,mPermissionHint,mPermissionToastHint,onActivityResultCode);
//			}
//
//		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

}
