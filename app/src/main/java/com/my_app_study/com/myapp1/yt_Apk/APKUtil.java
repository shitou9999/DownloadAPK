package com.my_app_study.com.myapp1.yt_Apk;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by Administrator on 2016/3/10.
 */
public class APKUtil {
    private static String packageName="com.example.administrator.mytable";
    public static int getVersionCode(Context context){
        //获得apk安装包的信息
        PackageManager pn=context.getPackageManager();
        int versionCode=0;
        try {
            PackageInfo pkinfo=pn.getPackageInfo(packageName,0);
            versionCode=pkinfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }
    public static String getVersionName(Context context){
        //获得apk安装包的信息
        PackageManager pm=context.getPackageManager();
        String versionName="";
        try {
            versionName=pm.getPackageInfo(packageName,0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
