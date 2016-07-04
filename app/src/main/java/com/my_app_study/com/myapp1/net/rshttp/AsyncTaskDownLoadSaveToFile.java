package com.my_app_study.com.myapp1.net.rshttp;


import com.my_app_study.com.myapp1.net.IHttpClient;
import com.my_app_study.com.myapp1.net.IProgressCallBack;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by MT3020 on 2015/11/6.
 */
public class AsyncTaskDownLoadSaveToFile extends AbsAsyncTask {
    private static String appDir;


    IProgressCallBack callBack;
    String targetUri;
    long contentLen=1;
    long commitLen =0;

    int progress = 0;

    File resultFile;

    OutputStream fileOutPutSteam;

    public AsyncTaskDownLoadSaveToFile(IHttpClient client, IProgressCallBack listener, String targetUri) {
        super(client);
        callBack = listener;
        this.targetUri=targetUri;
    }

    private void checkResultFile(){
        if(appDir!=null){
            return;
        }

       // appDir = GlobalCfg.getImageDirPath();
    }

    @Override
    protected Void doInBackground(Object[] params) {
        publishItemUpdateProgressChange(null,0);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(targetUri).build();

        do{
            resultFile=new File(appDir+"/"+(int) System.currentTimeMillis());

            System.out.println("fuck you");

        }while (resultFile.exists());



        try {
            if(resultFile.createNewFile()){
                fileOutPutSteam = new FileOutputStream(resultFile);
            }
        }catch (FileNotFoundException e){
            throw new RuntimeException("can not find File "+resultFile.getPath());
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                fileOutPutSteam.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }



        byte[] bytes = new byte[1024*8];
        int perLen =0;

        try {
            Response response =  client.newCall(request).execute();
            ResponseBody body = response.body();
            contentLen =  body.contentLength();
            InputStream inputStream = body.byteStream();
            while ((perLen=inputStream.read(bytes))>=0){
                fileOutPutSteam.write(bytes,0,perLen);
                commitLen+=perLen;
                int nowProgress = (int)(100*commitLen/contentLen);
                if(nowProgress != progress){
                    progress = nowProgress;
                    publishItemUpdateProgressChange(null,progress * 0.01f);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void success) {
        super.onPostExecute(success);
        if(!mClient.IsCallBackNeed())
            return;

        if(callBack!=null&&!callBack.fail(resultFile ,null)){
            callBack.successful(resultFile ,null );
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
