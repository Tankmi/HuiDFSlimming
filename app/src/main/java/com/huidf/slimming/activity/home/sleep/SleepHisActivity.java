package com.huidf.slimming.activity.home.sleep;

import android.content.Intent;
import android.view.View;

import com.huidf.slimming.R;
import com.huidf.slimming.context.ApplicationData;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

/**
 * 睡眠历史记录
 * @author ZhuTao
 * @date 2018/12/18 
 * @params 
*/

@ContentView(R.layout.activity_sleep_his)
public class SleepHisActivity extends SleepHisBaseActivity {
    public SleepHisActivity() {
        super( );
        TAG = getClass().getName();
        mContext = ApplicationData.context;
    }

    @Override
    protected void initHead() {
        setStatusBarColor(true, true, mContext.getResources().getColor(R.color.white));
        setTittle("睡眠记录");
        if (mHandler == null) mHandler = new MyHandler(this);
    }

    @Override
    protected void initLogic() {
        super.initLogic();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessage(mHandler.GETSLEEPINFO);
    }

    @Event(value = {R.id.btn_title_view_right})
    private void getEvent(View view)
    {//必须用private进行修饰,否则无效

        switch (view.getId()) {
            case R.id.btn_title_view_right:  //提示
                showHintDialogView();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
//        if(REINSTALLUSERINFO == requestCode){
//            if(requestCode == 200){
//                LOG("更新数据");
//                mHandler.sendEmptyMessage(mHandler.GETSPORTHISTORY);
//            }
//        }
    }

    @Override
    protected void pauseClose() {
        super.pauseClose();
    }


    @Override
    protected void destroyClose() {
        if (mHandler != null) mHandler.removeCallbacksAndMessages(null);
    }
}
