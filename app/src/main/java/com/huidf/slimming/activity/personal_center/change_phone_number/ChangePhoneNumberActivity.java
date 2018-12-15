package com.huidf.slimming.activity.personal_center.change_phone_number;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;

import com.huidf.slimming.R;
import com.huidf.slimming.activity.personal_center.select_photo.SelectPhotoActivity;
import com.huidf.slimming.fragment.user.login.LoginBindBaseFragment;

import java.io.File;

import huitx.libztframework.utils.BitmapUtils;
import huitx.libztframework.utils.ToastUtils;

import static com.huidf.slimming.R.id.et_user_info_name;

/**
 * 修改绑定手机号
 * @author ZhuTao
 * @date 2018/11/30 
 * @params 
*/

public class ChangePhoneNumberActivity extends ChangePhoneNumberBaseActivity {

    public ChangePhoneNumberActivity() {
        super(R.layout.activity_change_phone_number);
        TAG = this.getClass().getName();
    }

    @Override
    protected void initHead() {
        super.initHead();
        setStatusBarColor(true, mContext.getResources().getColor(R.color.status_bar_color));
        setTittle("更换绑定手机号");
    }

    @Override
    protected void initLogic() {
        super.initLogic();
        mTimeCount = new TimeCount(60000, 1000);// 构造CountDownTimer对象
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_title_view_right: //完成
                commitChangePhoneNumber();
                break;
            case R.id.tv_cpnv_verify:    //获取验证码
                GetVerifyCode();
                break;
        }
    }

    @Override
    protected void pauseClose() {
        super.pauseClose();
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

}
