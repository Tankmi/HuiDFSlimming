package com.huidf.slimming.fragment.today_movement.add;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.gson.Gson;
import com.huidf.slimming.R;
import com.huidf.slimming.adapter.movement.TodayMovementAddAdapter;
import com.huidf.slimming.base.BaseFragment;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.context.UrlConstant;
import com.huidf.slimming.entity.today_movement.MovementEntity;
import com.huidf.slimming.entity.user.UserEntity;
import com.huidf.slimming.view.swiperecyclerview.SpacesItemDecoration;

import org.xutils.http.RequestParams;

import java.util.LinkedList;

import huitx.libztframework.context.ContextConstant;
import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.ToastUtils;
import huitx.libztframework.utils.UnitConversion;

@SuppressLint("ValidFragment")
public class TodayMovementAddBaseFragment extends BaseFragment implements
		OnClickListener {

	public TodayMovementAddBaseFragment(int layoutId) {
		super(layoutId);
	}

	@Override
	protected void initContent() {
		findView();
	}


    private final int API_INSERTSPORT = 1001;

	public UserEntity mUserEntity;

	@Override
	public void paddingDatas(String mData, int type) {
		setLoading(false,"");
		Gson mGson = new Gson();
		try{
			mUserEntity = mGson.fromJson(mData, UserEntity.class);
		}catch(Exception e){
			return;
		}
		if(mUserEntity.code == ContextConstant.RESPONSECODE_200){
			if(type == API_INSERTSPORT){	//提交运动数据

			}
		} else if (mUserEntity.code == ContextConstant.RESPONSECODE_310) {    //登录信息过时跳转到登录页
			reLoading();
		} else {
			ToastUtils.showToast(NewWidgetSetting.getInstance().filtrationStringbuffer(mUserEntity.msg, "接口信息异常！"));
		}
	}

	@Override
	public void error(String msg, int type) {
		setLoading(false,"");
	}


	@Override
	protected void initHead() {
	}
	
	@Override
	protected void onVisibile() {
	}
	
    private RecyclerView mRecyclerView;
    protected SpacesItemDecoration decor;
    private TodayMovementAddAdapter mAdapter;
    private LinkedList<MovementEntity> movementInfoList = new LinkedList<>();
    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks;

	/**
	 * 初始化视图控件 添加监听事件
	 */
    protected void findView() {
        mRecyclerView = findViewByIds(R.id.rv_add_movement);
	}

    protected void setData() {
        movementInfoList.add(new MovementEntity(R.drawable.icon_kuaizou, "快走" , 5.36f, 1));
        movementInfoList.add(new MovementEntity(R.drawable.icon_manpao, "慢跑" , 7.37f, 2));
        movementInfoList.add(new MovementEntity(R.drawable.icon_youyong, "游泳" , 7.37f, 3));
        movementInfoList.add(new MovementEntity(R.drawable.icon_qiche, "骑车" , 7.77f, 4));
        movementInfoList.add(new MovementEntity(R.drawable.icon_tiaosheng, "跳绳" , 7.77f, 5));
        movementInfoList.add(new MovementEntity(R.drawable.icon_tiaowu, "跳舞" , 6.7f, 6));
        movementInfoList.add(new MovementEntity(R.drawable.icon_pashan, "爬山" , 11.38f, 7));
        movementInfoList.add(new MovementEntity(R.drawable.icon_panyan, "攀岩" , 11.38f, 8));
        movementInfoList.add(new MovementEntity(R.drawable.icon_palouti, "爬楼梯" , 7.24f, 9));
        movementInfoList.add(new MovementEntity(R.drawable.icon_tabancao, "踏板操" , 8.71f,10));
        movementInfoList.add(new MovementEntity(R.drawable.icon_lanqiu, "篮球" , 7.37f, 11));
        movementInfoList.add(new MovementEntity(R.drawable.icon_zuqiu, "足球" , 8.04f,12));
        movementInfoList.add(new MovementEntity(R.drawable.icon_paiqiu, "排球" , 6.3f,13));
        movementInfoList.add(new MovementEntity(R.drawable.icon_pingpang, "乒乓球" , 4.01f,14));
        movementInfoList.add(new MovementEntity(R.drawable.icon_yumaoqiu, "羽毛球" , 6.03f,15));
        movementInfoList.add(new MovementEntity(R.drawable.icon_wangqiu, "网球" , 5.36f,16));
        movementInfoList.add(new MovementEntity(R.drawable.icon_biqiu, "壁球" , 8.04f,17));
        movementInfoList.add(new MovementEntity(R.drawable.icon_huaban, "滑板" , 5.36f,18));
        movementInfoList.add(new MovementEntity(R.drawable.icon_huabing, "滑冰" , 8.04f,19));
        movementInfoList.add(new MovementEntity(R.drawable.icon_huaxue, "滑雪" , 8.04f,20));
        initRecycler();
    }
    private void initRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);

        mRecyclerView.setLayoutManager(linearLayoutManager);

        decor = new SpacesItemDecoration(0, 1,mContext.getResources().getColor(R.color.line_bg));
        mRecyclerView.addItemDecoration(decor);

        mAdapter = new TodayMovementAddAdapter(mContext, movementInfoList);
        mRecyclerView.setAdapter(mAdapter);

		mAdapter.setOnMovementClickListener(new TodayMovementAddAdapter.onMovementAddClick() {
			@Override
			public void onMovementClick(final MovementEntity mEntity)
			{
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
//						MovementTimeSelDialogFragment playQueueFragment = new MovementTimeSelDialogFragment();
//						playQueueFragment.show(getFragmentManager(), "movementdialog");
						LOG("name: " + mEntity.name);
						ShowMovementDialog(true,mEntity);
					}
				}, 60);
			}
		});
    }


	MovementTimeSelDialogFragment playQueueFragment;
	private FragmentManager fragmentManager;
	private String MOVEMENT_TIME_TAG = "movementdialog";
	/**
	 * 显示条目选择框
	 */
	private void ShowMovementDialog(boolean isShow, final MovementEntity mEntity)
	{
		if (playQueueFragment == null) playQueueFragment = new MovementTimeSelDialogFragment();
		if (fragmentManager == null) fragmentManager = getActivity().getSupportFragmentManager();
		playQueueFragment.show(fragmentManager,MOVEMENT_TIME_TAG);
		playQueueFragment.setDataInfo(mEntity);
		playQueueFragment.setOnMovementTimeListener(new MovementTimeSelDialogFragment.onSelMovementTimeListener() {
			@Override
			public int onSelMovementTime(int value,int equalCalorie)
			{
                commitRunningInfo(mEntity,value,equalCalorie);
				return 0;
			}
		});
	}

    /**
     * 提交运动数据
     */
    protected void commitRunningInfo(MovementEntity mEntity, int timeLength, int calorie)
    {
        LOG(mEntity.getSportType() + "");
        LOG(calorie + "");
        LOG(timeLength + "");

        RequestParams params = PreferenceEntity.getLoginParams();
        params.addBodyParameter("sportType", mEntity.getSportType() + "");
        params.addBodyParameter("sportKcal", "" + calorie);
        params.addBodyParameter("sportTime", "" + timeLength);
        params.addBodyParameter("type", "2");
        mgetNetData.GetData(this, UrlConstant.API_INSERTSPORT, API_INSERTSPORT, params);
        setLoading(true, "");
    }

	@Override
	protected void initLocation() {
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	protected void initLogic() {
	}

	@Override
	public void onClick(View view) {
	}

	@Override
	protected void pauseClose() {
	}

	@Override
	protected void destroyClose() {
	}

}
