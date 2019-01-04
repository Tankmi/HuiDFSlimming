package com.huidf.slimming.activity.personal_center.joingroup;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.huidf.slimming.R;
import com.huidf.slimming.adapter.ranking.RankingAdapter;
import com.huidf.slimming.base.BaseFragmentActivityForAnnotation;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.context.UrlConstant;
import com.huidf.slimming.entity.ranking.RankingEntity;
import com.huidf.slimming.view.swiperecyclerview.SpacesItemDecoration;
import com.huidf.slimming.view.swiperecyclerview.SwipeRecyclerView;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.LinkedList;
import java.util.List;

import huitx.libztframework.context.ContextConstant;
import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.StringUtils;
import huitx.libztframework.utils.ToastUtils;
import huitx.libztframework.utils.UnitConversion;

/**
 * @author ZhuTao
 * @date 2018/12/14
 * @params type, 1,申请加群，2，反馈问题
 */

@ContentView(R.layout.activity_input)
public class InputActivity extends BaseFragmentActivityForAnnotation{

    private int mType;  // 1,申请加群，2，反馈问题

    @ViewInject(R.id.et_input)
    private EditText et_input;
    @ViewInject(R.id.btn_input)
    private Button btn_input;


    public InputActivity()
    {
        super();
    }

    @Override
    protected void initHead()
    {
        mType = getIntent().getIntExtra("type",1);
        setStatusBarColor(false, true, mContext.getResources().getColor(R.color.white));
        setTittle(mType==1?"拉人进群":"问题反馈");
        LOG("data(1,申请加群，2，反馈问题) type:  " + mType);
    }

    @Override
    protected void initContent()
    {

    }

    @Event(value={R.id.btn_input})
    private void getEvent(View view){
        switch (view.getId()){
            case R.id.btn_input:
                IputData();
                break;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    private final int JOINGROUP = 10001, FEEDBACKPRO = 10002;

    public void IputData()
    {
        RequestParams params = PreferenceEntity.getLoginParams();

        if (mType == 1) {
            String phone = et_input.getText().toString();
            if(StringUtils.isMobileNO(phone))
                params.addBodyParameter("account", phone + "");
            else {
                ToastUtils.showToast("请输入正确的手机号！");
                return;
            }
            mgetNetData.GetData(this, UrlConstant.API_JOINGROUP, JOINGROUP, params);

        } else{
            String content = et_input.getText().toString();
            if(StringUtils.isEmpty(content)){
                ToastUtils.showToast("请输入反馈内容！");
                return;
            }
            params.addBodyParameter("content", content + "");
            mgetNetData.GetData(this, UrlConstant.API_FEEDBACK, FEEDBACKPRO, params);
        }
        setLoading(true, "");
    }

    @Override
    public void paddingDatas(String mData, int type)
    {
        setLoading(false, "");
        Gson gson = new Gson();
        RankingEntity mTopicentity;
        try {
            mTopicentity = gson.fromJson(mData, RankingEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (mTopicentity.code == ContextConstant.RESPONSECODE_200) {
            if (type == JOINGROUP) {
                ToastUtils.showToast("拉人进群成功！");
            } else if (type == FEEDBACKPRO) {
                ToastUtils.showToast("感谢您的反馈！");
            }
        } else if (mTopicentity.code == ContextConstant.RESPONSECODE_310) {    //登录信息过时跳转到登录页
            reLoading();
        } else {
            ToastUtils.showToast(NewWidgetSetting.filtrationStringbuffer(mTopicentity.msg, "接口信息异常！"));
        }
    }

    @Override
    public void error(String msg, int type)
    {
        super.error(msg, type);
        LOG(msg);
    }


    @Override
    protected void initLocation()
    {

    }

    @Override
    protected void initLogic()
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
