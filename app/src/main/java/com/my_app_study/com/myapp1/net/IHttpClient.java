package com.my_app_study.com.myapp1.net;

import java.io.File;
import java.util.Map;

/**
 * Created by MT3020 on 2015/11/4.
 */
public interface IHttpClient<T>  {

    // 上传文件
    public void uploadFile(IProgressCallBack<T> callback, String targetUri,
                           Map<String, String> headers, Map<String, String> paras, String fileName, Object data, Class<T> defClass /*data只能是BitMap或者byte[]*/);
    //批量上传文件
   void uploadFiles(IProgressCallBack<T> callback, String targetUri, Map<String, String> headers, Map<String, String> params, Map<String, Object> fileMap, Class<T> defClass);
//                           //支持 byte[] File BitMap Uri
//


    public void uploadFile(IProgressCallBack<T> callBack, String targetUri,
                           Map<String, String> paras, Map<String, String> headers, File file, Class<T> defClass);                       //支持 File

    // 文件下载 保存到本地sd；CallBack 传入参数为 File类型；
    public void downLoadFileAndSave(IProgressCallBack<T> callBack, String targetUri);

    // jsonPost
    public void postJson(String targetUri, Map<String, String> params, Map<String, String> header, IRsCallBack<T> callback, Class<T> modelClass);

    // jsonGet
    public void getJson(String targetUri, Map<String, String> params, Map<String, String> header, IRsCallBack<T> callback, Class<T> modelClass);


    //标记 队列管理： 相同tag表示更新ui 可能冲突
    public String getTag();

    //标记 表示是否需要进行回调操作；
    void    setIsCallBackNeed(boolean isNeedCallBack);

    boolean IsCallBackNeed();
}









