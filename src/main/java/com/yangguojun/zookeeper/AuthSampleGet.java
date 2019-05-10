package com.yangguojun.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * 验证读取ACL控制的node
 */
public class AuthSampleGet {
    final static String PATH = "/zk-book-auth_test";

//    final static String PATH = "/node3/1";

//    final static String PATH = "/node3";

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        ZooKeeper zookeeper = new ZooKeeper("172.19.101.141:2181", 50000, null);
//        zookeeper.addAuthInfo("digest", "foo:true".getBytes());
        byte[] b = zookeeper.getData(PATH, false, null);
        String s = new String(b);
        System.out.println(s);
    }
}
