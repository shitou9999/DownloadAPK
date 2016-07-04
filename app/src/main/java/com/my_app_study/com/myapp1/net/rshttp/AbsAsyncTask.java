package com.my_app_study.com.myapp1.net.rshttp;

import android.os.AsyncTask;

import com.my_app_study.com.myapp1.net.HttpManager;
import com.my_app_study.com.myapp1.net.IHttpClient;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by MT3020 on 2015/11/4.
 */
public abstract class AbsAsyncTask extends AsyncTask<Object, Float, Void> {

    protected IHttpClient mClient;

    public AbsAsyncTask(IHttpClient mClient){
        this.mClient = mClient;
    }

    Map<String, Object> mDataMap =new LinkedHashMap<>() ;
    Map<String, Float> mDataProgress = new LinkedHashMap<>();

    // 为了让外部类访问publishProgress方法(已经确保是公用线程)
    public void publishItemUpdateProgressChange(String fileName , float value){

        mDataProgress.remove(fileName);
        mDataProgress.put(fileName,value);

        float sumProgress =0;
        float upCount = mDataProgress.size();
        for(String key : mDataProgress.keySet()){
            sumProgress+= 1f/upCount * mDataProgress.get(key);
        }

        publishProgress(sumProgress);
    }



    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        HttpManager.getDefault().remove(mClient);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Float... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
