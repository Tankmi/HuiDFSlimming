package com.huidf.slimming.fragment.today_movement.running;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.amap.api.maps.MapView;
import com.huidf.slimming.R;
import com.huidf.slimming.activity.toady_movement.run.RunActivity;
import com.huidf.slimming.base.BaseFragment;

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
				getActivity().startActivity(new Intent(getActivity(),RunActivity.class));
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

}
