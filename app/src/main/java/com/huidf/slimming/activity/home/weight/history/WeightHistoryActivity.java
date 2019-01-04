package com.huidf.slimming.activity.home.weight.history;

import com.huidf.slimming.R;
import com.huidf.slimming.activity.home.weight.WeightBaseActivity;

import org.xutils.view.annotation.ContentView;

/**
 * 体重历史记录
 * @author ZhuTao
 * @date 2018/12/15 
 * @params 
*/
@ContentView(R.layout.activity_weight_history)
public class WeightHistoryActivity extends WeightHistoryBaseActivity {

    public WeightHistoryActivity() {
        super( );
        TAG = this.getClass().getSimpleName();
    }

    @Override
    protected void initHead() {
        super.initHead();
        setStatusBarColor(true, mContext.getResources().getColor(R.color.status_bar_color));
        setTittle("体重记录");
        setHideTitleLine();
        if (mHandler == null) mHandler = new MyHandler(this);
    }

    @Override
    protected void initLogic() {
        super.initLogic();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessage(mHandler.GETWEIGHTHISDATA);
    }

    @Override
    protected void pauseClose() {
        super.pauseClose();
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }


}
