package com.my_app_study.com.myapp1.net.rshttp;


import com.my_app_study.com.myapp1.net.IHttpClient;
import com.my_app_study.com.myapp1.net.IProgressCallBack;
import com.my_app_study.com.myapp1.net.IRsCallBack;
import com.squareup.okhttp.MediaType;

import java.io.File;
import java.util.Map;


/**
 * Created by MT3020 on 2015/11/4.
 * // okhttp  mClient 封装
 *
 */
public class HttpClientImpl<T> implements IHttpClient<T> {
    AbsAsyncTask task; //网络请求的实际操作


    String tag;//标记
    boolean isCallBackNeed =true;

    public HttpClientImpl(String tag){
        this.tag = tag;
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
                return MediaType.parse("image/png");
            case "mkv":
                return MediaType.parse("file/mkv");
            case "jpg":
                return MediaType.parse("image/jpg");
            case "mp4":
                return MediaType.parse("file/mp4");
            case "3gp":
                return MediaType.parse("file/3gp");
        }
        return null;
    }


    // 上传文件
    @Override
    public void uploadFile (IProgressCallBack<T> callback , String targetUri,
                            Map<String,String> headers, Map<String,String> paras, String fileName, Object data , Class<T> defClass) {

        task = new AsyncTaskUpload<T>(this,callback,targetUri,paras,headers,getTypeByName(fileName), fileName, data,  defClass);
        task.execute();
    }


    @Override
    public void uploadFile(IProgressCallBack<T> callBack, String targetUri, Map<String,String> paras, Map<String,String> headers, File file, Class<T> defClass) {
        MediaType type = getTypeByFile(file);
        String fileName = file.getName();

        task = new AsyncTaskUpload<T>(this,callBack,targetUri,paras ,headers, type,fileName,file, defClass);
        task.execute();
    }

    public void uploadFiles(IProgressCallBack<T> callback, String targetUri, Map<String, String> headers, Map<String, String> params, Map<String, Object> fileMap, Class<T> defClass) {
        task = new AsyncTaskMultiUpload<T>(this,callback,headers,params,defClass,fileMap,targetUri);
        task.execute();
    }

    // 文件下载
    @Override
    public void downLoadFileAndSave(IProgressCallBack callBack, String targetUri) {
        task = new AsyncTaskDownLoadSaveToFile(this,callBack,targetUri);
        task.execute();
    }


    @Override
    public void postJson(String targetUri, Map<String, String> params, Map<String, String> header, final IRsCallBack<T> callback , final Class<T> modelClass) {

        task = new AsyncTaskHandleJson<T>(this,"post",callback,targetUri,params,header, modelClass);
        task.execute();

    }

    @Override
    public void getJson(String targetUri, Map<String, String> params, Map<String, String> header, final IRsCallBack<T> callback, final Class<T> modelClass) {
        task = new AsyncTaskHandleJson<T>(this,"get",callback,targetUri,params,header, modelClass);
        task.execute();
    }

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public void setIsCallBackNeed(boolean isNeedCallBack) {
        this.isCallBackNeed = isNeedCallBack;
    }

    @Override
    public boolean IsCallBackNeed() {
        return this.isCallBackNeed;
    }

}









