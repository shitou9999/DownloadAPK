package com.my_app_study.com.myapp1.net.rshttp;


import com.my_app_study.com.myapp1.net.HttpManager;
import com.my_app_study.com.myapp1.net.IRsCallBack;
import com.my_app_study.com.myapp1.net.model.RsBaseModel;

import java.util.Map;

/**
 * Created by MT3020 on 2015/12/30.
 */
public class RSHttpClient<T extends RsBaseModel> extends HttpClientImpl<T>  {

    public RSHttpClient(String tag) {
        super(tag);
    }




    @Override
    public void postJson(String targetUri, Map<String, String> params, Map<String, String> header, final IRsCallBack<T> callback , final Class<T> modelClass) {
        IRsCallBack<T> reSendCallBack = new IRsCallBackImp(targetUri,params,header, callback, modelClass);
        task = new AsyncTaskHandleJson<T>(this,"post",reSendCallBack,targetUri,params,header, modelClass);
        task.execute();
    }




    private class IRsCallBackImp implements IRsCallBack<T>{
        String targetUri;
        Map<String, String> params;
        Map<String, String> header;
        IRsCallBack<T> callback;
        Class<T> modelClass;
        IRsCallBackImp(String targetUri, Map<String, String> params, Map<String, String> header, final IRsCallBack<T> callback, final Class<T> modelClass){
            this.targetUri = targetUri;
            this.params = params;
            this.header = header;
            this.callback=callback;
            this.modelClass = modelClass;
        }

        @Override
        public void successful(T data, String json) {
            if(data!=null && "-1".equals(data.error)){
                AsyncTaskHandleJson delay = new AsyncTaskHandleJson<T>(RSHttpClient.this,"post",callback,targetUri,params,header, modelClass);
                HttpManager.getDefault().addJsonPostTask(delay);
                HttpManager.getDefault().setNetBrokOrTokenInvalidInfo(HttpManager.NetWorkError.TOKEN_INVALID);
            }else {
                if(data==null && !json.equals("")&&json!=null){
                    HttpManager.getDefault().setNetBrokOrTokenInvalidInfo(HttpManager.NetWorkError.NET_WORK_ERROR);
                }else {
                    HttpManager.getDefault().setNetBrokOrTokenInvalidInfo(HttpManager.NetWorkError.NET_WORK_OK);
                }
                callback.successful(data,json);
            }
        }

        @Override
        public boolean fail(T data, String json) {
            return false;
        }
    }



}
