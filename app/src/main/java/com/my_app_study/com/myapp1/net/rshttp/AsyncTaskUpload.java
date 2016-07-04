package com.my_app_study.com.myapp1.net.rshttp;

import android.util.Log;

import com.google.gson.Gson;
import com.my_app_study.com.myapp1.net.IHttpClient;
import com.my_app_study.com.myapp1.net.IProgressCallBack;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.Map;
import java.util.Set;


/**
 * Created by MT3020 on 2015/11/4.
 */
public class AsyncTaskUpload<T> extends AbsAsyncTask {
    IProgressCallBack<T> callBack;
    String targetUri;
    Map<String,String> headers;
    Map<String,String> paras;
    MediaType type;
    String mFileName;
    Object mUploadData;
    String jsonData;
    Class<T> modelClass ;

    public AsyncTaskUpload(IHttpClient client, IProgressCallBack<T> listener, String targetUri, Map<String,String> paras,

                           Map<String, String> headers, MediaType type, String fileName, Object mUploadData , Class<T> defClass) {
        super(client);
        callBack = listener;
        this.targetUri=targetUri;
        this.paras = paras;
        this.headers = headers;
        this.type = type;
        this.mFileName =fileName;
        this.mUploadData = mUploadData;
        modelClass = defClass;
    }

    @Override
    protected Void doInBackground(Object[] params) {

        StringBuilder sb = new StringBuilder();
        sb.append(targetUri).append("-->");

        publishItemUpdateProgressChange(mFileName,0);

        final OkHttpClient client = new OkHttpClient();

        Request.Builder headBuilder = new Request.Builder();

        MultipartBuilder multipartBuild = new MultipartBuilder();

        multipartBuild.type(MultipartBuilder.FORM);

        if(headers !=null){
            Set<String> keys = headers.keySet();
            for(String key : keys){
                headBuilder.addHeader(key, headers.get(key));
                sb.append(key).append("=").append(headers.get(key)).append("--");
            }
        }

        if(params!=null){

            FormEncodingBuilder formBody = new FormEncodingBuilder();
            for(String key : paras.keySet()){

                formBody.add(key,paras.get(key));
                sb.append(key).append("=").append(paras.get(key)).append("--");
            }

            multipartBuild.addPart(formBody.build());
        }

        if(mUploadData != null){

            multipartBuild.addFormDataPart(type.type(), mFileName, new FileUploadRequestBody(type, this, mFileName,mUploadData));
            sb.append(type.type()).append("=").append(mFileName).append("--");


        }else {
            throw new RuntimeException("can not build RequestBody");
        }

        sb.setLength(sb.length()-2);
        Log.i("net_rs_post", sb.toString());

        Request request =   headBuilder.url(targetUri).post(multipartBuild.build()).build();

        Call call = client.newCall(request);
        try {
            jsonData = call.execute().body().string();
        }catch (IOException e){  e.printStackTrace(); }

        return null;
    }

    @Override
    protected void onPostExecute(Void data) {
        super.onPostExecute(data);

        Log.i("net_rs_back",targetUri+"-->"+jsonData);

        if(!mClient.IsCallBackNeed())
            return;

        T modelObject = null;
        if(modelClass!=null && jsonData !=null){
            try {
                modelObject = new Gson().fromJson(jsonData,modelClass);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        if(callBack!=null && !callBack.fail(modelObject ,jsonData)){
            callBack.successful(modelObject, jsonData);
        }
    }

    @Override
    protected void onProgressUpdate(Float[] values) {
        super.onProgressUpdate(values);
        float progress = values[0];
        if(callBack!=null){
            callBack.finishProgress(progress);
        }

    }

}
