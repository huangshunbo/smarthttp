package com.android.minlib.smarthttp.strategy;

public interface IURLStrategy {
    /**
     *<br> Description: 判断使用HTTPS的条件
     *<br> Author:      huangshunbo
     *<br> Date:        2018/4/15 0015 下午 11:06
     */
    boolean isHttps();

    /**
     *<br> Description: 设置域名组
     *<br> Author:      huangshunbo
     *<br> Date:        2018/4/15 0015 下午 11:21
     */
    String getDomains();

}
