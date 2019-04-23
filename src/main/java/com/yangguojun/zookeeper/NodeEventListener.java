package com.yangguojun.zookeeper;

/**
 * Created by yangguojun on 2017/12/22.
 */
public interface NodeEventListener {

    void nodeEventHandle(NodeEvent event);

}
