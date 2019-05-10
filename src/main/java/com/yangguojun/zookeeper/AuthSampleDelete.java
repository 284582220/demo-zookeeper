package com.yangguojun.zookeeper;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Pattern;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * 子节点和当前节点的删除
 */
public class AuthSampleDelete {
    final static String PATH = "/zk-book-auth_test2";
    final static String PATH_CHILDREN = "/zk-book-auth_test2/child";

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        ZooKeeper zookeeper = new ZooKeeper("172.19.101.141:2181", 50000, null);
        zookeeper.addAuthInfo("digest", "foo:true".getBytes());
        zookeeper.create(PATH, "init".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
        zookeeper.create(PATH_CHILDREN, "init".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);
        try{
            ZooKeeper zookeeper2 = new ZooKeeper("172.19.101.141:2181", 50000, null);
            zookeeper2.delete(PATH_CHILDREN, -1);
        } catch (Exception e){
            System.out.println("删除节点失败：" + e.getMessage());
        }
        ZooKeeper zooKeeper3 = new ZooKeeper("172.19.101.141:2181", 50000, null);
        zooKeeper3.addAuthInfo("digest", "foo:true".getBytes());
        zooKeeper3.delete(PATH_CHILDREN, -1);
        System.out.println("成功删除节点：" + PATH_CHILDREN);
        ZooKeeper zooKeeper4 =  new ZooKeeper("172.19.101.141:2181", 50000, null);
        zooKeeper4.delete(PATH, -1);
        System.out.println("成功删除节点：" + PATH);
    }
}
