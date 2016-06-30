package com.my_app_study.com.myapp1.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.my_app_study.com.myapp1.R;

public class ToastUtils {

    private static Toast toast;

    public static void ShowToastMessage(String msg, Context context) {
        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void ShowToastMessage(int msgId, Context context) {
        toast = Toast.makeText(context, msgId, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 显示一段时间消失
     */
    public static void showCustomToast(Context context,String str){
        Toast toast=Toast.makeText(context,str,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        //布局文件想要什么样式自定义，
        LinearLayout view=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.prompt_custom_toast_layout,null);
        ((TextView)view.findViewById(R.id.tv_title)).setText(str);
        toast.setView(view);
        toast.show();
    }
    /*
        <?xml version="1.0" encoding="utf-8"?>
        <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:minHeight="144dp"
        android:minWidth="144dp"
        android:gravity="center"
        android:padding="10dp"
        android:orientation="vertical"
        android:background="@drawable/toast_bg_shape">
        <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/toast_photo"/>
        <TextView
        android:id="@+id/tv_title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text=""
        android:textColor="#FFFFFFFF"
        android:textSize="@dimen/dimen_45sp"
        android:layout_marginTop="@dimen/dimen_45"/>
        </LinearLayout>
    */

}