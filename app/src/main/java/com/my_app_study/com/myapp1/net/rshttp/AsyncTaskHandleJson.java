package com.my_app_study.com.myapp1.net.rshttp;

import android.util.Log;

import com.google.gson.Gson;
import com.my_app_study.com.myapp1.net.IHttpClient;
import com.my_app_study.com.myapp1.net.IRsCallBack;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.Map;


/**
 * Created by MT3020 on 2015/11/9.
 */

public class AsyncTaskHandleJson<T> extends AbsAsyncTask {

    IRsCallBack<T> callBack;//执行成功以后的回调函数
    String targetUri;    //网络请求地址
    Map<String,String> netParams;//网络参数
    Map<String,String> netHeaders;//网络请求头设置
    Class<T> mType;          //模型类型,空接口
    T mModel;       //模型实例
    String mResultJson;
    String method;       //判断到底是GET或者POST方法

    public AsyncTaskHandleJson(IHttpClient client, String method, IRsCallBack<T> listener, String targetUri, Map<String, String> params, Map<String, String> headers, Class<T> defClass) {
        super(client);
        this.callBack = listener;
        this.targetUri=targetUri;
        this.netParams =params;
        this.mType = defClass;
        this.netHeaders = headers;
        this.method = method;
    }




    @Override
    protected Void doInBackground(Object[] params) {


        OkHttpClient client = new OkHttpClient();

        Request.Builder builder = new Request.Builder();

        if(netHeaders != null) {

            for (String key : netHeaders.keySet()) {
                builder.addHeader(key, netHeaders.get(key));
            }
        }

        Request request = null;

        if(method == null || method.equalsIgnoreCase("get")) {

            StringBuilder sb = new StringBuilder();
            sb.append(targetUri);

            if (netParams != null && netParams.size() > 0) {
                sb.append("?");
                for (String key : netParams.keySet()) {
                    sb.append(key).append("=").append(netParams.get(key)).append("&");
                }
                sb.setLength(sb.length() - 1);
            }

            request = builder.url(sb.toString()).get().build();


        }else{
            FormEncodingBuilder encodingBuilder = new FormEncodingBuilder();

            if (netParams != null) {
                for (String key : netParams.keySet()) {
                    encodingBuilder.add(key, netParams.get(key));
                }
            }
            request = builder.url(targetUri).post(encodingBuilder.build()).build();
        }



        try {
            Response response = client.newCall(request).execute();
            if(response !=null){
                mResultJson = response.body().string();

                Log.i("net_rs_back",targetUri+"--->"+mResultJson);

                if(mType != null) {
                    Gson gson = new Gson();
                    mModel = gson.fromJson(mResultJson, mType);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void success) {
        super.onPostExecute(success);
        if(!mClient.IsCallBackNeed())
            return;

        if(callBack!=null&&!callBack.fail(mModel , mResultJson)){
            callBack.successful(mModel, mResultJson);
        }

    }

}







//=================================分隔符--无用代码暂存==================================================




//        OkHttpClient mClient = new OkHttpClient();
//
//        MultipartBuilder multipartBuilder = new MultipartBuilder();
//
//        Request.Builder builder = new Request.Builder();
//
//        if(params != null) {
//            for(String key : params.keySet()){
//                multipartBuilder.addFormDataPart(key , params.get(key));
//            }
//        }
//
//        if(header != null) {
//
//            for (String key : header.keySet()) {
//                builder.addHeader(key, header.get(key));
//            }
//        }
//
//
//
//        Request request = builder.url(targetUri).post(multipartBuilder.build()).build();
//
//
//        Callback cb = new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//
//                defaultErrorHandler();
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                String json = response.body().string();
//
//                if(modelClass == null) {
//
//                    if (!callback.fail(json)) {
//                        callback.successful(json);
//                    }
//
//                }else {
//
//                    Gson gson = new Gson();
//                    Object mModel = gson.fromJson(json, modelClass);
//
//                    if (!callback.fail(mModel)) {
//                        callback.successful(mModel);
//                    }
//                }
//
//            }
//        };
//
//        mClient.newCall(request).enqueue(cb);


//  AsyncTaskUpload task = new AsyncTaskUpload(CallBack,targetUri,headers,type,fileName,file);
// task.execute();