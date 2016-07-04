package com.my_app_study.com.myapp1.DownloadAPK_Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.google.gson.Gson;
import com.my_app_study.com.myapp1.R;
import com.my_app_study.com.myapp1.net.HttpManager;
import com.squareup.okhttp.Headers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 主要提供版本检查及文件下载操作--------给服务端版本号
 * 版本更新的基本流程：
 一般是将本地版本告诉服务器，服务器经过相关处理会返回客户端相关信息，告诉客户端需不需要更新!!!!!!!!!!!，
 如果需要更新是强制更新还是非强制更新。客户端得到服务器返回的相关信息后再进一步做逻辑处理。
 强制更新：
 一般的处理就是进入应用就弹窗通知用户有版本更新，弹窗可以没有取消按钮并不能取消。这样用户就只能选择更新或者关闭应用了，
 当然也可以添加取消按钮，但是如果用户选择取消则直接退出应用。
 非强制更新
 一般的处理是在应用的设置中添加版本检查的操作，如果用户主动检查版本则弹窗告知用户有版本更新。这时用户可以取消或者更新。
 文／laogui（简书作者）
 原文链接：http://www.jianshu.com/p/02424e35daf1#
 著作权归作者所有，转载请联系作者获得授权，并标注“简书作者”。
 */
public class VersionUpdateService extends Service {
    private static final String TAG = VersionUpdateService.class.getSimpleName();
    private LocalBinder binder = new LocalBinder();

    private DownLoadListener downLoadListener;//下载任务监听回调接口
    private boolean downLoading;
    private int progress;

    private NotificationManager mNotificationManager;
    private NotificationUpdaterThread notificationUpdaterThread;
    private Notification.Builder notificationBuilder;
    private final int NOTIFICATION_ID = 100;

    private VersionInfoBean versionUpdateModel;//服务器返回的实体类
    private CheckVersionCallBack checkVersionCallBack;//下载任务监听回调接口

    public VersionUpdateService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        LogUtil.d(TAG, "onCreate called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        LogUtil.d(TAG, "onDestroy called");
        setDownLoadListener(null);
        setCheckVersionCallBack(null);
        stopDownLoadForground();
        if (mNotificationManager != null)
            mNotificationManager.cancelAll();
        downLoading = false;
    }

    public interface DownLoadListener {
        void begain();
        void inProgress(float progress, long total);
        void downLoadLatestSuccess(File file);
        void downLoadLatestFailed();
    }

    public interface CheckVersionCallBack {
        void onSuccess();
        void onError();
    }

    public void setCheckVersionCallBack(CheckVersionCallBack checkVersionCallBack) {
        this.checkVersionCallBack = checkVersionCallBack;
    }

    private class NotificationUpdaterThread extends Thread {
        @Override
        public void run() {
            while (true) {
                notificationBuilder.setContentTitle("正在下载更新" + progress + "%"); // the label of the entry
                notificationBuilder.setProgress(100, progress, false);
                mNotificationManager.notify(NOTIFICATION_ID, notificationBuilder.getNotification());
                if (progress >= 100) {
                    break;
                }
            }
        }
    }

    public boolean isDownLoading() {
        return downLoading;
    }

    public void setDownLoading(boolean downLoading) {
        this.downLoading = downLoading;
    }

    /**
     * 让Service保持活跃,避免出现:
     * 如果启动此服务的前台Activity意外终止时Service出现的异常(也将意外终止)
     *  创建通知栏!!!!!!!!!!
     */
    private void starDownLoadForground() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "下载中,请稍后...";
        // The PendingIntent to launch our activity if the user selects this notification
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, MainActivity.class), 0);
        notificationBuilder = new Notification.Builder(this);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);  // the status icon
        notificationBuilder.setTicker(text);  // the status text
        notificationBuilder.setWhen(System.currentTimeMillis());  // the time stamp
        notificationBuilder.setContentText(text);  // the contents of the entry
//        notificationBuilder.setContentIntent(contentIntent);  // The intent to send when the entry is clicked
        notificationBuilder.setContentTitle("正在下载更新" + 0 + "%"); // the label of the entry
        notificationBuilder.setProgress(100, 0, false);
        notificationBuilder.setOngoing(true);
        notificationBuilder.setAutoCancel(true);
        Notification notification = notificationBuilder.getNotification();
        startForeground(NOTIFICATION_ID, notification);
    }

    private void stopDownLoadForground() {
        stopForeground(true);
    }
    //执行版本检查任务
    public void doCheckUpdateTask() {
        //获取本定版本号
        final int currentBuild = APKUtil.getVersionCode(this);
//        String client = "android";
//        String q = "needUpgrade";
        //调用版本检查接口
        Map<String, String> map = new HashMap<>();
        map.put("type",1+"");
        HttpManager.getDefault().post("http://api.cunli.zhanyaa.com/api/version.do",map,new RequestCallBack<VersionInfoBean>(){
//        ApiManager.getInstance().versionApi.upgradeRecords(q, currentBuild, client, new RequestCallBack() {
            @Override
            public void onSuccess(VersionInfoBean headers, String response) {
                try {
                    Gson gson=new Gson();
                    versionUpdateModel=gson.fromJson(response, VersionInfoBean.class);
                    if (versionUpdateModel.getVersionCode() < currentBuild) {
                        versionUpdateModel.setUpdatetype(0);
                    }
                    //TEST DATA
                    versionUpdateModel.setUpdatetype(1);

//                    MainApplication.getInstance().setVersionUpdateModelCache(versionUpdateModel); //原版的带这句
                    if (checkVersionCallBack != null)
                        checkVersionCallBack.onSuccess();
                } catch (Exception e) {
//                    ToastUtil.toast(VersionUpdateService.this, "获取版本信息失败");
                }
            }

            @Override
            public void onError(int code, String response) {
                if (checkVersionCallBack != null) {
                    checkVersionCallBack.onError();
                }
            }
        });
    }
    //启动通知栏进度更新线程
    public void doDownLoadTask() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        starDownLoadForground();

        notificationUpdaterThread = new NotificationUpdaterThread();
        notificationUpdaterThread.start();
        //文件下载存放路径
        final File fileDir = FolderUtil.getDownloadCacheFolder();
        final String url = versionUpdateModel.getUrl();
        final String fileName_ = url.substring(url.lastIndexOf("/") + 1);
        final String fileName = StringUtil.string2MD5(fileName_) + ".apk"; //加密

        downLoading = true;

        if (downLoadListener != null) {
            downLoadListener.begain();
        }
        //下载apk请求
        NetManager.getInstance().download(url, fileDir.getAbsolutePath(), fileName, new DownloadCallBack() {
            @Override
            public void inProgress(float progress_, long total) {
                progress = (int) (progress_ * 100);
                if (downLoadListener != null) {  //执行进度更新
                    downLoadListener.inProgress(progress_, total);
                }
                if (progress >= 100) {
                    mNotificationManager.cancelAll();
                }
            }

            @Override
            public void onSuccess(Headers headers, String response) {
                //执行成功回调
                final File destFile = new File(fileDir.getAbsolutePath(), fileName);
                if (downLoadListener != null) {
                    downLoadListener.downLoadLatestSuccess(destFile);
                }
                downLoading = false;
                installApk(destFile, VersionUpdateService.this);
            }

            @Override  //执行失败回调
            public void onError(int code, String response) {
                downLoading = false;
                if (mNotificationManager != null)
                    mNotificationManager.cancelAll();
                if (downLoadListener != null) {
                    downLoadListener.downLoadLatestFailed();
                }
            }
        });
    }

    public VersionInfoBean getVersionUpdateModel() {
        return versionUpdateModel;
    }

    public void setDownLoadListener(DownLoadListener downLoadListener) {
        this.downLoadListener = downLoadListener;
    }

    //安装apk
    public void installApk(File file, Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public VersionUpdateService getService() {
            return VersionUpdateService.this;
        }
    }
}