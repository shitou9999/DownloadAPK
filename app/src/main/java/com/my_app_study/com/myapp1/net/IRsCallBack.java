package com.my_app_study.com.myapp1.net;

/**
 * Created by MT3020 on 2015/11/11.
 */
public interface IRsCallBack<T> {
    public abstract void successful(T data, String json);
    /**
     * 如果为真表示该方法返回错误，不会调用successful方法执行UI等其他业务逻辑
     * 若为假表示该方法返回正确，调用successful方法修改UI
     * @param data
     * @return
     */

    boolean fail(T data, String json);
}
