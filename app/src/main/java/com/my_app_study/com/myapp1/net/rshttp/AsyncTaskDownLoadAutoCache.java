package com.my_app_study.com.myapp1.net.rshttp;
import com.my_app_study.com.myapp1.net.IHttpClient;
import com.my_app_study.com.myapp1.net.IProgressCallBack;

@Deprecated

public abstract class AsyncTaskDownLoadAutoCache extends AbsAsyncTask {
    Object result;
    String targetUri;
    IProgressCallBack callBack;

    public AsyncTaskDownLoadAutoCache (IHttpClient client, IProgressCallBack callBack, String targetUri){
        super(client);
        this.callBack = callBack;
        this.targetUri = targetUri;
    }


//    @Override
//    protected Void doInBackground(Object... params) {
//        Bitmap resultMap = null;
//
//
//        try {
//            resultMap = Picasso.with(null).load("").get();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        Picasso.with(RSApplication.getGlobeContext()).load(targetUri).into(new Target() {
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                CallBack.successful(bitmap);
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable errorDrawable) {
//                CallBack.fail(null);
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//                CallBack.finishProgress(0);
//            }
//        });
//
//    }




    @Override
    protected void onPostExecute(Void data) {
        super.onPostExecute(data);
        if(!mClient.IsCallBackNeed())
            return;

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
