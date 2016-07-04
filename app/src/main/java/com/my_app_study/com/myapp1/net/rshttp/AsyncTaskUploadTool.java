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

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Created by MT3020 on 2016/1/14.
 */
public class AsyncTaskUploadTool<T> extends AbsAsyncTask {
    IProgressCallBack<T> callBack;
    String targetUri;
    Map<String,String> headers;
    Map<String,String> paras;
    String jsonData;
    Class<T> modelClass ;

    public AsyncTaskUploadTool(IHttpClient mClient, IProgressCallBack<T> callBack, Map<String, String> headers, Map<String, String> paras, Class<T> modelClass, Map<String, Object> mUploadData, String targetUri) {
        super(mClient);
        this.callBack = callBack;
        this.headers = headers;
        this.modelClass = modelClass;

        for(String key : mUploadData.keySet()){
            super.mDataMap.put(key,mUploadData.get(key));
            super.mDataProgress.put(key,0f);
        }

        this.paras = paras;
        this.targetUri = targetUri;
    }


    private MediaType getTypeByFile(File file){
        String[] nameSpilt = file.getName().split("\\.");
        return getType(nameSpilt[nameSpilt.length - 1]);
    }

    private MediaType getTypeByName(String name){
        String[] nameSpilt = name.split("\\.");
        return getType(nameSpilt[nameSpilt.length-1]);
    }

    private MediaType getType(String type){
        switch (type) {
            case "image":
            case "png":
                System.out.println("get.............png");
                return MediaType.parse("image/png");
            case "mkv":
                return MediaType.parse("video/mkv");
            case "jpg":
                return MediaType.parse("image/jpg");
            case "mp4":
                return MediaType.parse("video/mp4");
            case "3pg":
                return MediaType.parse("video/3gp");
        }
        return null;
    }

    @Override
    protected Void doInBackground(Object... params) {
        StringBuilder sb = new StringBuilder();
        sb.append(targetUri).append("-->");

   //     publishItemUpdataProgressChange(0);

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

        for (Map.Entry<String,Object> entry : super.mDataMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();


            System.out.println("--------------------------------------------------------------------------"+key+":::"+value);

            if(value != null){
                multipartBuild.addFormDataPart("image", key, new FileUploadRequestBody(getTypeByName(key), this, key, value));
                sb.append("image").append("=").append(key).append("--");

            }else {
                throw new RuntimeException("can not build RequestBody");
            }

        }

        sb.setLength(sb.length() - 2);
        Log.i("net_rs_post", sb.toString());

        Request request =   headBuilder.url(targetUri).post(multipartBuild.build()).build();

        Call call = client.newCall(request);
        try {
            jsonData = call.execute().body().string();
        }catch (IOException e){  e.printStackTrace(); }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

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
    protected void onProgressUpdate(Float... values) {
        super.onProgressUpdate(values);
        float progress = values[0];
        if(callBack!=null){
            callBack.finishProgress(progress);
        }
    }
}
