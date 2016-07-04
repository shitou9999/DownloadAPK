package com.my_app_study.com.myapp1.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;


import com.my_app_study.com.myapp1.net.rshttp.AbsAsyncTask;
import com.my_app_study.com.myapp1.net.rshttp.AsyncTaskHandleJson;
import com.my_app_study.com.myapp1.net.rshttp.HttpClientImpl;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by MT3020 on 2015/11/4.
 */
public class HttpManager {
    static {

        mHttpManager = new HttpManager();
        rsRequestQueue = Collections.synchronizedList(new LinkedList<IHttpClient>());
        iconImageHost = "http://192.168.1.12:8080/auth/postAuth";

    }

    private static final HttpManager mHttpManager;
    private static final List<IHttpClient> rsRequestQueue;


    private Set<AbsAsyncTask> delayJsonPostQueue = Collections.synchronizedSet(new HashSet<AbsAsyncTask>());
    private long mLoginStateUpDataTime;

    public static final String iconImageHost;


    public static HttpManager getDefault() {
        return mHttpManager;
    }


    public void init(Context context) {
        EventBus.getDefault().register(this);
    }


    // 文件上传入口

//    public <T> void uploadFile(IProgressCallBack<T> callBack, File file, Class<T> defClass) {
//        String[] nameSplit = file.getName().split("\\.");
//
//        Map<String, String> headers = prepareHeadersInfo();
//        Map<String, String> paras = prepareParasInfo();
//        IHttpClient<T> client = new HttpClientImpl<T>(null);
//        client.uploadFile(callBack, Utiles.UPLOAD_IMG_URI, headers, paras, file, defClass);
//    }
//
//    public <T> void uploadFile(IProgressCallBack<T> listener, String name, byte[] data, Class<T> defClass) {
//        Map<String, String> headers = prepareHeadersInfo();
//        Map<String, String> paras = prepareParasInfo();
//        IHttpClient<T> client = new HttpClientImpl<T>(null);
//        client.uploadFile(listener, Utiles.UPLOAD_IMG_URI, headers, paras, name, data, defClass);
//    }

    public <T> void uploadFile(String uri, Map<String, String> paras, Map<String, String> heads, String fileName, Bitmap bitmap,
                               IProgressCallBack<T> listener, Class<T> defClass) {

        IHttpClient<T> client = new HttpClientImpl<T>(null);
        client.uploadFile(listener, uri , heads, paras, fileName, bitmap, defClass);
    }

    public <T> void uploadFile(String uri, Map<String, String> paras, Map<String, String> heads, String fileName, File file,
                               IProgressCallBack<T> listener, Class<T> defClass) {
        IHttpClient<T> client = new HttpClientImpl<T>(null);
        client.uploadFile(listener, uri , heads, paras, fileName, file, defClass);
    }




    public <T> void uploadFiles(String uri, Map<String,String> paras, Map<String, String> heads , Map<String, Object> files, IProgressCallBack<T> callBack, Class<T> defClass){

        IHttpClient<T> client = new HttpClientImpl<T>(null);
        client.uploadFiles(callBack,uri, heads, paras, files,defClass);
    }






    private Map<String, String> prepareHeadersInfo() {
        Map<String, String> map = new HashMap<String, String>();
       // map.put("token", GlobalCfg.getToken_Key());
        return map;
    }

    private Map<String, String> prepareParasInfo() {
   //     User user = GlobalCfg.getUsr();

        Map<String, String> map = new HashMap<>();
//        if (user != null) {
//            map.put("phoneNumber", user.phoneNumber);
//        }

        return map;
    }

    private <T> void mUploadFile(IProgressCallBack<T> listener, String name, Object file, Class<T> defClass, String
            tag) {
        Map<String, String> headers = prepareHeadersInfo();
        Map<String, String> paras = prepareParasInfo();
        IHttpClient<T> client = new HttpClientImpl<T>(tag);
        client.uploadFile(listener, iconImageHost, paras, headers, name, file, defClass);
    }


    // 文件下载入口
    public void downloadFile(IProgressCallBack callBack, String targetUri) {

        final OkHttpClient client = new OkHttpClient();

        MultipartBuilder builder = new MultipartBuilder();


        RequestBody body = builder.build();

        Request request = new Request.Builder().url(targetUri).post(body).build();

        Call call = client.newCall(request);


    }

    //get请求方法
    public void get(String url, Map<?, ?> params, IRsCallBack callBack) {
        get(url, params, null, callBack, null, null);
    }

    //get请求方法
    public void get(String url, Map<?, ?> params, Map<?, ?> header, IRsCallBack callBack) {
        get(url, params, header, callBack, null, null);
    }

    //get请求方法
    public <T> void get(String url, Map<?, ?> params, IRsCallBack callBack, Class<T> tClass) {
        get(url, params, null, callBack, tClass, null);
    }

    //get请求方法
    public <T> void get(String url, Map<?, ?> params, Map<?, ?> header, IRsCallBack callBack, Class<T> tClass) {
        get(url, params, header, callBack, tClass, null);
    }

    //get请求方法
    public void get(String url, Map<?, ?> params, IRsCallBack callBack, String tag) {
        get(url, params, null, callBack, null, tag);
    }

    //get请求方法
    public void get(String url, Map<?, ?> params, Map<?, ?> header, IRsCallBack callBack, String tag) {
        get(url, params, header, callBack, null, tag);
    }

    //get请求方法
    public <T> void get(String url, Map<?, ?> params, IRsCallBack callBack, Class<T> tClass, String tag) {
        get(url, params, null, callBack, tClass, tag);
    }

    /**
     * get请求方法
     *
     * @param url      请求地址
     * @param params   请求参数
     * @param header   请求头设置
     * @param callBack 请求完成后回调
     * @param tClass   JSON转模型类
     * @param tag      请求表示，可用来取消回调
     * @param <T>
     */
    public <T> void get(String url, Map<?, ?> params, Map<?, ?> header, IRsCallBack callBack, Class<T> tClass, String
            tag) {
        StringBuilder builder = new StringBuilder();
        builder.append(url);
        builder.append(":?");

        //将参数从模型转成网络参数字符串
        Map<String, String> tParams = new HashMap<>();
        if (params != null) {
            for (Map.Entry<?, ?> entry : params.entrySet()) {
                tParams.put(entry.getKey().toString(), entry.getValue() == null ? "" : entry.getValue().toString());

                builder.append(entry.getKey().toString());
                builder.append("=");
                builder.append(entry.getValue() == null ? "" : entry.getValue().toString());
                builder.append("--");
            }
        }

        //将header从模型转成网络参数字符串
        Map<String, String> tHeader = new HashMap<>();
        if (header != null) {

            for (Map.Entry<?, ?> entry : header.entrySet()) {
                tHeader.put(entry.getKey().toString(), entry.getValue() == null ? "" : entry.getValue().toString());

                builder.append(entry.getKey().toString());
                builder.append("=");
                builder.append(entry.getValue() == null ? "" : entry.getValue().toString());
                builder.append("--");
            }
        }
        IHttpClient client = getClient(tag);

        client.getJson(url, tParams, tHeader, callBack, tClass);

        builder.setLength(builder.length() - 2);
        Log.i("net_rs_get", builder.toString());
    }

    //post请求方法
    public void post(String url, Map<?, ?> params, Map<?, ?> header, IRsCallBack callBack) {
        post(url, params, header, callBack, null, null);
    }

    //post请求方法
    public <T> void post(String url, Map<?, ?> params, IRsCallBack<T> callBack, Class<T> tClass) {
        post(url, params, null, callBack, tClass, null);
    }

    //post请求方法
    public <T> void post(String url, Map<?, ?> params, Map<?, ?> header, IRsCallBack<T> callBack, Class<T> tClass) {
        post(url, params, header, callBack, tClass, null);
    }


    //post请求方法
    public void post(String url, Map<?, ?> params, IRsCallBack callBack, String tag) {
        post(url, params, null, callBack, null, tag);
    }

    //post请求方法
    public void post(String url, Map<?, ?> params, Map<?, ?> header, IRsCallBack callBack, String tag) {
        post(url, params, header, callBack, null, tag);
    }

    //post请求方法
    public <T> void post(String url, Map<?, ?> params, IRsCallBack<T> callBack, Class<T> tClass, String tag) {
        post(url, params, null, callBack, tClass, tag);
    }

    /**
     * POST请求方法
     *
     * @param url      请求地址
     * @param params   请求参数
     * @param header   请求头设置
     * @param callBack 请求完成后回调
     * @param tClass   JSON转模型类
     * @param tag      请求表示，可用来取消回调
     */
    public void post(String url, Map<?, ?> params, Map<?, ?> header, IRsCallBack callBack, Class tClass, String tag) {

        // 打印 post请求详情；
        StringBuilder builder = new StringBuilder();
        builder.append(url);
        builder.append("--->");

        //将参数从模型转成网络参数字符串
        Map<String, String> tParams = new HashMap<>();
        if (params != null) {
            for (Map.Entry<?, ?> entry : params.entrySet()) {
                tParams.put(entry.getKey().toString(), entry.getValue() == null ? "null" : entry.getValue().toString());

                builder.append(entry.getKey().toString());
                builder.append("=");
                builder.append(entry.getValue() == null ? "" : entry.getValue().toString());
                builder.append("--");
            }
        }

        //将header从模型转成网络参数字符串
        Map<String, String> tHeader = new HashMap<>();

        if (header != null) {
            for (Map.Entry<?, ?> entry : header.entrySet()) {
                tHeader.put(entry.getKey().toString(), entry.getValue() == null ? "null" : entry.getValue().toString());

                builder.append(entry.getKey().toString());
                builder.append("=");
                builder.append(entry.getValue() == null ? "" : entry.getValue().toString());
                builder.append("--");
            }
        }
        IHttpClient client = new HttpClientImpl(tag);
        client.postJson(url, tParams, tHeader, callBack, tClass);


        builder.setLength(builder.length() - 2);
        Log.i("net_rs_post", builder.toString());
    }


    //------------------------------------------------------------取消回调管理-------------------------------------------------------//
    public void cancel(Object tag) {
        for (IHttpClient client : rsRequestQueue) {
            if (client.getTag() != null && client.getTag().equals(tag)) {
                client.setIsCallBackNeed(false);
            }
        }
    }

    public void cancelAll() {
        for (IHttpClient client : rsRequestQueue) {
            client.setIsCallBackNeed(false);
        }
    }

    public void remove(IHttpClient client) {
        rsRequestQueue.remove(client);
    }

    private <T> IHttpClient getClient(String tag) {
        IHttpClient<T> client = new HttpClientImpl<T>(tag);
        rsRequestQueue.add(client);
        return client;
    }
    //-----------------------------------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------二次重发管理-------------------------------------------------------//


    public void addJsonPostTask(AsyncTaskHandleJson absAsyncTask) {

//        if (RSAuthManager.getDefault().getRSLoginState() == LoginState.LOGINED) {
//            absAsyncTask.execute();
//            clearDelayPostIfNeed();
//        } else {
//            delayJsonPostQueue.add(absAsyncTask);
//        }
    }


//    public void onEventMainThread(LoginState state) {
//        if (state == LoginState.LOGINED) {
//            clearDelayPostIfNeed();
//        }
//    }

    private void clearDelayPostIfNeed() {
//        if (delayJsonPostQueue.size() != 0 && RSAuthManager.getDefault().getRSLoginState() == LoginState.LOGINED) {
//            for (AbsAsyncTask task : delayJsonPostQueue) {
//                task.execute();
//            }
//            delayJsonPostQueue.clear();
//        }
    }


    //------------------------------------------------------------------------------------------------------------------------------//
    //----------------------------------辅助RSAuthManager获取用户登录状态  可以获得 1:网络异常 2:token超时-------------------------------//
    private NetWorkError mLoginState = NetWorkError.NET_WORK_OK;

    public NetWorkError getNetBrokOrTokenInvalidInfo() {
        return mLoginState;
    }

    public void setNetBrokOrTokenInvalidInfo(NetWorkError state) {
        if (mLoginState == state) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - mLoginStateUpDataTime < 5000 && state == NetWorkError.TOKEN_INVALID) {
            Log.i("用户Token超时", "5000毫秒内被重复设置多次");
        } else {
            mLoginState = state;
        }

    }
    //------------------------------------------------------------------------------------------------------------------------------//

    public enum NetWorkError {
        TOKEN_INVALID,
        NET_WORK_ERROR,
        NET_WORK_OK;
    }
}







































