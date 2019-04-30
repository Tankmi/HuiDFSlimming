package com.huidf.slimming.dynamic.view.net;

import com.huidf.slimming.context.UrlConstant;
import com.huidf.slimming.entity.user.UserEntity;

import java.io.File;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * 作者：ZhuTao
 * 创建时间：2019/3/20 : 11:09
 * 描述：进行Retrofit网络请求接口的实例——发布动态
 */
public interface CreateDynamicService {

//    @POST("user/chgheader.do")
    @Multipart
    @POST("H5/uploadImg.do")
    Observable<ResponseBody> uploadingPicture(@Part("uId") RequestBody uId, @Part MultipartBody.Part  file);
//    Observable<ResponseBody> uploadingPicture(@Part MultipartBody.Part  file);

    //发布动态
    @POST(UrlConstant.API_SENDDYNAMIC)
    @FormUrlEncoded
    Observable<ResponseBody> createDynamic(@FieldMap Map<String, String> map);


}
