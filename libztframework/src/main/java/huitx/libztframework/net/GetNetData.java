package huitx.libztframework.net;

import android.os.Environment;
import android.util.Log;


import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

import huitx.libztframework.context.LibPreferenceEntity;
import huitx.libztframework.interf.ConsultNet;
import huitx.libztframework.utils.LOGUtils;
import huitx.libztframework.utils.NetUtils;
import huitx.libztframework.utils.StringUtils;

import static android.R.attr.name;
import static org.xutils.x.http;

/**
 * @author ZhuTao
 * @Title: GetNetData.java
 * @Description: TODO(获取网络数据, 配合ConcultNet接口使用)
 */
public class GetNetData {

    protected ConsultNet mConsultNet;

    private static GetNetData mGetNetDataInstance;

    public GetNetData() { }

    public static GetNetData getInstance()
    {
        synchronized (GetNetData.class) {
            if (mGetNetDataInstance == null) {
                mGetNetDataInstance = new GetNetData();
            }
        }
        return mGetNetDataInstance;
    }

    /**
     * 无参数,get请求
     *
     * @param consultNet  实例对象
     * @param url         请求链接
     * @param connectType 请求类型
     */
    public void GetData(ConsultNet consultNet, String url, int connectType)
    {
        this.mConsultNet = consultNet;
        if (mConsultNet != null) {
            getConsultListData(url, connectType);
        }
    }

    /**
     * 有参数，post请求
     * @param consultNet  实例对象
     * @param url         请求链接
     * @param connectType 请求类型
     * @param params      附带参数
     */
    public void GetData(ConsultNet consultNet, String url, int connectType, RequestParams params)
    {
        this.mConsultNet = consultNet;
        if (mConsultNet != null) {
            postConsultListData(url, connectType, params);
        }
    }

    /**
     * 下载数据
     */
    public void downloadData(ConsultNet consultNet, String url, int connectType, String name)
    {
        this.mConsultNet = consultNet;
        if (mConsultNet != null) {
            downloadData(url, name, connectType);
        }
    }


    /**
     * 向后台进行请求  POST
     *
     * @param url 请求地址
     */
    private void postConsultListData(final String url, final int connecttype, RequestParams params)
    {
        Log.i("spoort_list", "GetNetData 数据请求地址" + url);
        params.setUri(url);
        params.setConnectTimeout(1000 * 10);
        x.http().post(params, new Callback.CommonCallback<String>(){

            @Override
            public void onSuccess(String result)
            {
                String data = StringUtils.replaceJson(result.toString());
                Log.i("spoort_list", "GetNetData 数据请求结果" + data);
                mConsultNet.paddingDatas(data, connecttype);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback)
            {
                Log.i("spoort_list", "GetNetData 数据请求失败：" + ex.getMessage().toString());
                ex.printStackTrace();
                if(NetUtils.isAPNType()) mConsultNet.error("数据请求失败", connecttype);
//                mConsultNet.error(ex.getMessage().toString(), connecttype);
            }

            @Override
            public void onCancelled(CancelledException cex)
            {
                LOGUtils.LOG("取消请求 onFinished");
                mConsultNet.error("取消请求", connecttype);
            }

            @Override
            public void onFinished()
            {
                LOGUtils.LOG("请求完成 onFinished");
            }
        });
    }

    /**
     * 向后台进行请求  GET
     *
     * @param url 请求地址
     */
    private void getConsultListData(final String url, final int connecttype)
    {

        final RequestParams params = new RequestParams(url);
        params.setConnectTimeout(1000 * 10);
        http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result)
            {
                String data = StringUtils.replaceJson(result.toString());
                Log.i("spoort_list", "GetNetData 数据请求结果" + data);
                mConsultNet.paddingDatas(data, connecttype);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback)
            {
                Log.i("spoort_list", "GetNetData 数据请求失败：" + ex.getMessage().toString());
                ex.printStackTrace();
                if(NetUtils.isAPNType()) mConsultNet.error("数据请求失败", connecttype);

            }

            @Override
            public void onCancelled(CancelledException cex)
            {

            }

            @Override
            public void onFinished()
            {

            }
        });
    }

    /**
     * @param url         地址
     * @param name        本地保存的文件名
     * @param connecttype 请求类型
     */
    private void downloadData(String url, String name, final int connecttype)
    {
		final String dPath = Environment.getExternalStorageDirectory().getPath() + LibPreferenceEntity.KEY_CACHE_PATH + "/" + name;
//        final String dPath = Environment.getExternalStorageDirectory().getPath() + savePath + "/" + name;
        File file = new File(dPath);
        if (file.exists()) {  //文件如果存在，先删除文件，否则无法下载
            LOG("文件已存在，先删除文件。" + file.delete());
        }

//		if (Build.VERSION.SDK_INT >= 21) {
//			LOG("版本大于5.0，采用xutils3.0下载");
        org.xutils.http.RequestParams params = new org.xutils.http.RequestParams(url);
//		RequestParams params = new RequestParams(url);
        //自定义保存路径，Environment.getExternalStorageDirectory()：SD卡的根目录
        params.setSaveFilePath(dPath);
        //自动为文件命名
        params.setAutoRename(true);
        http().get(params, new Callback.ProgressCallback<File>() {

            @Override
            public void onSuccess(File result)
            {
                LOG("下载成功");
                mConsultNet.paddingDatas("okss" + dPath, connecttype);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback)
            {
                LOG("下载失败");
                ex.printStackTrace();
                mConsultNet.error("下载失败", connecttype);
            }

            @Override
            public void onCancelled(CancelledException cex)
            {
            }

            @Override
            public void onFinished()
            {
            }

            //网络请求之前回调
            @Override
            public void onWaiting()
            {

            }

            //网络请求开始的时候回调
            @Override
            public void onStarted()
            {
                LOG("开始下载");
            }

            //下载的时候不断回调的方法
            @Override
            public void onLoading(long total, long current, boolean isDownloading)
            {
                LOG("正在下载，总共" + total + "，已经下载：" + current + ";进度：" + (Float.parseFloat(current + "") / Float.parseFloat(total + "") * 100) + "%");
                mConsultNet.paddingDatas("" + (int) (Float.parseFloat(current + "") / Float.parseFloat(total + "") * 100), connecttype);
            }
        });
//		}else{
//			if(httpUtils == null){
//				httpUtils = new HttpUtils(3000);
//				httpUtils.configCurrentHttpCacheExpiry(1);
//			}
//
//			httpUtils.download(url, dPath, true, true, new RequestCallBack<File>() {
//
//				@Override
//				public void onFailure(HttpException error, String msg) {
//					LOG("下载失败");
//					mConsultNet.error(msg,connecttype);
//				}
//
//				@Override
//				public void onSuccess(ResponseInfo<File> arg0) {
//					// TODO Auto-generated method stub
//					LOG("下载成功");
//					mConsultNet.paddingDatas("okss" + dPath,connecttype);
//
//				}
//
//				@Override
//				public void onStart() {
//					LOG("开始下载");
//				}
//
//				@Override
//				public void onLoading(long total, long current, boolean isUploading) {
//					super.onLoading(total, current, isUploading);
//					LOG("正在下载，总共" + total + "，已经下载：" + current + ";进度：" + (Float.parseFloat(current + "")/Float.parseFloat(total + "") * 100) + "%");
//					mConsultNet.paddingDatas("" + (int)(Float.parseFloat(current + "")/Float.parseFloat(total + "") * 100),connecttype);
//				}
//
//			});
//		}
    }

    private void LOG(String data)
    {
        Log.i("spoort_list", "" + data);
    }

    ;
}
