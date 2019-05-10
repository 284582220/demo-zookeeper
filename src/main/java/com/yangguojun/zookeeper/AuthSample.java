package com.yangguojun.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * 创建两个节点
 */
public class AuthSample {
    private static String PATH = "/zk-book-auth_test3";
    final static String PATH_CHILDREN = "/zk-book-auth_test/child";

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        ZooKeeper zookeeper = new ZooKeeper("172.19.101.141:2181", 50000, null);
        zookeeper.addAuthInfo("digest", "foo:true".getBytes());
//        zookeeper.create(PATH, "init".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);

        zookeeper.create(PATH_CHILDREN, "init".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);
//        Thread.sleep(Integer.MAX_VALUE);
    }
}
