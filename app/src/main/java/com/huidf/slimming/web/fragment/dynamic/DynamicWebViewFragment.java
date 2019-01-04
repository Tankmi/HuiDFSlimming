package com.huidf.slimming.web.fragment.dynamic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.huidf.slimming.R;
import com.huidf.slimming.context.HtmlUrlConstant;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.fragment.dynamic.WechatShareDialogFragment;
import com.huidf.slimming.web.MyWebViewUtil;
import com.huidf.slimming.web.activity.WebViewActivity;
import com.huidf.slimming.web.fragment.WebViewBaseFragment;

import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.PreferencesUtils;

/**
 * 动态列表
 * @author ZhuTao
 * @date 2018/12/25 
 * @params 
*/

public class DynamicWebViewFragment extends WebViewBaseFragment implements View.OnClickListener{
//	private String titleName;
	private static final String ARGUMENT_URL = "url";


	public DynamicWebViewFragment(){
		super(R.layout.fragment_webview);
		this.urlLoading = HtmlUrlConstant.HTML_MAIN_DYNAMIC;
		TAG = this.getClass().getName();
	}

	@SuppressLint("ValidFragment")
	public DynamicWebViewFragment(String urlLoading) {
		super(R.layout.fragment_webview);
		LOG("DynamicWebViewFragment    " + urlLoading);
		this.urlLoading = urlLoading;
		TAG = this.getClass().getName();
	}

	@Override
	protected void initHead() {
		super.initHead();
//		urlLoading  = "http://192.168.0.142:8090/html/oneRecord.html?id=12&date=2018-2-26";
		mWebUtil = MyWebViewUtil.getInstance();
	}

	@Override
	protected void initLogic() {
		super.initLogic();
		mWebView.getSettings().setSupportZoom(false);// 支持缩放
		mWebView.getSettings().setBuiltInZoomControls(false);// 显示放大缩小[/mw_shl_code]
	}

	@Override
	public void onResume() {
		super.onResume();
		if(PreferenceEntity.isRefreshDynamic){
			LOG("刷新动态列表");
			mWebView.reload();
		}
//		if(isReload)mWebView.reload();
		PreferenceEntity.isRefreshDynamic = false;
	}

	private int SENDDYNAMIC = 100;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LOG("fragment onActivityResult requestCode" + requestCode);
	}

	@Override
	protected boolean filtrationUrl(String mUrl) {
		Intent intent = null;
		if(mUrl.contains(HtmlUrlConstant.HTML_CUT_DYNAMIC_DETAIL)) {  //动态详情
			String pId = MyWebViewUtil.getInstance().getSubString(mUrl, "?postId=");
			intent = new Intent(mContext, WebViewActivity.class);
			intent.putExtra("url", HtmlUrlConstant.HTML_USER_DYNAMICDETAILS + pId);
			intent.putExtra("is_refresh", false);
			startActivity(intent);
			return true;
		}else if(mUrl.contains(HtmlUrlConstant.HTML_CUT_USERINFO)) {  //个人主页
			String pId = MyWebViewUtil.getInstance().getSubString(mUrl, "person?id=");
			intent = new Intent(mContext, WebViewActivity.class);
			intent.putExtra("url", HtmlUrlConstant.HTML_USER_INFO + pId);
			intent.putExtra("is_refresh", false);
			startActivity(intent);
			return true;
		}
		else if(mUrl.contains(HtmlUrlConstant.HTML_CUT_SHARE)) {  //分享
			String pId = MyWebViewUtil.getInstance().getSubString(mUrl, "moment?");
			String userId = PreferencesUtils.getString(mContext, PreferenceEntity.KEY_USER_ID);
			if(NewWidgetSetting.filtrationStringbuffer(userId,"").equals("")) return true;
			ShowMovementDialog(HtmlUrlConstant.HTM_SPLICE_WECHATSHARE + userId + "&momentId=" + pId);
			return true;
		}
//		if(mUrl.contains(HtmlUrlConstant.HTML_CUTOUT_DYNAMIC_SEND)) {  //发布动态
//			isReload = true;
//			intent = new Intent(mContext, WebViewActivity.class);
//			intent.putExtra("url", HtmlUrlConstant.HTML_USER_SENDDYNAMIC);
//			intent.putExtra("is_refresh", false);
//			startActivityForResult(intent,SENDDYNAMIC);
//			return true;
//		}
//		if(mUrl.contains(HtmlUrlConstant.HTML_CUTOUT_DYNAMIC_PERSON)) {  //进入个人主页
//			String userID = MyWebViewUtil.getInstance().getSubString(mUrl, "?id=");
//			String mID = PreferencesUtils.getString(mContext, PreferenceEntity.KEY_USER_ID);
//			if(!userID.equals("")){
//				if(!mID.equals("") && mID.equals(userID)){
//					intent = new Intent(mContext, UserInfoActivity.class);
//					startActivity(intent);
//				}else{
//					intent = new Intent(mContext, WebViewActivity.class);
//					intent.putExtra("url", HtmlUrlConstant.HTML_USER_INFO + userID);
//					intent.putExtra("is_refresh", false);
//					startActivity(intent);
//				}
//			}
//			return true;
//		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_title_view_right:
				mWebView.reload();
				break;
		}
	}

	WechatShareDialogFragment playQueueFragment;
	private FragmentManager fragmentManager;
	private String MOVEMENT_TIME_TAG = "sharedialog";
	/**
	 * 显示分享框
	 */
	private void ShowMovementDialog(String url)
	{
		if (playQueueFragment == null) playQueueFragment = new WechatShareDialogFragment();
		if (fragmentManager == null) fragmentManager = getActivity().getSupportFragmentManager();
		playQueueFragment.setShareInfo(url);
		playQueueFragment.show(fragmentManager,MOVEMENT_TIME_TAG);
	}

}
