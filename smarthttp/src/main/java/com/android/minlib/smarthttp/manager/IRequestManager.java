package com.android.minlib.smarthttp.manager;

/**
 * @author: huangshunbo
 * @Filename: IRequestManager
 * @Description: 请求管理
 * @Copyright: Copyright (c) 2017 Tuandai Inc. All rights reserved.
 * @date: 2018/8/9 16:44 
 */
public interface IRequestManager<T> {

    void addCalls(T call);

    void removeAllCalls();

    void removeCall(T call);

}
