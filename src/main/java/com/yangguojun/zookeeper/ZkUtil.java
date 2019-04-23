package com.yangguojun.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.RetryForever;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yangguojun on 2017/11/30.
 */
public class ZkUtil {

    private ZkUtil() {
    }

    private static String zkConnectString;
    private static RetryPolicy retryPolicy;
    private static final AtomicInteger lock = new AtomicInteger(0);
    private static int retryIntervalMs;
    private static String auth;

    private static final String ENCODE = "UTF-8";
    public static final String ZK_AUTH_STR = "taskCenter:TaskCenter!@#2018";

    private static class SingleHolder {
        private static CuratorFramework curatorFramework;

        static {
            RetryPolicy retry = ZkUtil.retryPolicy;
            if (ZkUtil.retryPolicy == null) {
                retry = new RetryForever(ZkUtil.retryIntervalMs);
            }

            if (auth == null || "".equals(auth)) {
                curatorFramework = CuratorFrameworkFactory.newClient(zkConnectString, retry);
            } else {
                curatorFramework = CuratorFrameworkFactory.builder()
                        .authorization("digest", auth.getBytes())
                        .connectString(zkConnectString)
                        .retryPolicy(retry)
                        .build();
            }


            curatorFramework.start();

            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    if (curatorFramework != null) {
                        CloseableUtils.closeQuietly(curatorFramework);
                    }
                }
            }));
        }

    }


    public static String generateDigest(String auth) {
        try {
            return DigestAuthenticationProvider.generateDigest(auth);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static void init(String zkConnectString, int retryIntervalMs, String auth) {
        if (lock.compareAndSet(0, 1)) {
            ZkUtil.zkConnectString = zkConnectString;
            ZkUtil.retryIntervalMs = retryIntervalMs;
            ZkUtil.auth = auth;
        }
    }


    public static void init(String zkConnectString, RetryPolicy retryPolicy,  String auth) {
        if (lock.compareAndSet(0, 1)) {
            ZkUtil.zkConnectString = zkConnectString;
            ZkUtil.retryPolicy = retryPolicy;
            ZkUtil.auth = auth;
        }
    }


    private static CuratorFramework getClient() {
        return SingleHolder.curatorFramework;
    }


    public static void createData(String path, String value) throws Exception {
        getClient().create().creatingParentContainersIfNeeded().forPath(path, value.getBytes(ENCODE));
    }


    public static void createEphemeral(String path, String value) throws Exception {
        getClient().create().creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL).forPath(path, value.getBytes(ENCODE));
    }


    public static String createEphemeralSequential(String path, String value) throws Exception {
        return getClient().create().creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path, value.getBytes(ENCODE));
    }


    public static String createSequential(String path, String value) throws Exception {
        return getClient().create().creatingParentContainersIfNeeded()
                .withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(path, value.getBytes(ENCODE));

    }


    public static void setData(String path, String value) throws Exception {
        getClient().setData().forPath(path, value.getBytes(ENCODE));
    }


    public static void setPathWatcher(String path, final PathEventListener pathEventListener) throws Exception {
        PathChildrenCache pathCache = new PathChildrenCache(getClient(), path, true);
        pathCache.start(PathChildrenCache.StartMode.NORMAL);

        PathChildrenCacheListener listener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                PathEvent pathEvent = new PathEvent();
                pathEvent.setType(event.getType());
                if (event.getData() != null) {
                    pathEvent.setPath(event.getData().getPath());
                    pathEvent.setValue(new String(event.getData().getData(), ENCODE));
                }
                pathEventListener.pathEventHandle(pathEvent);
            }
        };

        pathCache.getListenable().addListener(listener);
    }

    public static void startPathWatcher(String path) throws Exception {
        PathChildrenCache watcher = new PathChildrenCache(getClient(), path, true);
        watcher.getListenable().addListener((client, event) -> {
            ChildData data = event.getData();
            if (data == null) {
                System.out.println("No data in event[" + event + "]");
            } else {
                System.out.println("Receive event: "
                        + "type=[" + event.getType() + "]"
                        + ", path=[" + data.getPath() + "]"
                        + ", data=[" + new String(data.getData()) + "]"
                        + ", stat=[" + data.getStat().toString().trim() + "]");
            }
        });
        watcher.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
    }


    public static void setNodeWatcher(String path, final NodeEventListener listener) throws Exception {
        final NodeCache nodeCache = new NodeCache(getClient(), path);
        nodeCache.start();
        NodeCacheListener nodeCacheListener = new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                NodeEvent event = new NodeEvent();
                if (nodeCache.getCurrentData() == null) {
                    event.setType(NodeEvent.NODE_DELETE);
                } else {
                    event.setType(NodeEvent.NODE_ADD_OR_UPDATE);
                    event.setValue(new String(nodeCache.getCurrentData().getData(), ENCODE));
                    event.setVersion(nodeCache.getCurrentData().getStat().getVersion());
                }

                listener.nodeEventHandle(event);
            }
        };

        nodeCache.getListenable().addListener(nodeCacheListener);
    }


    public static String getData(String path) throws Exception {
        byte[] bytes = getClient().getData().forPath(path);
        return new String(bytes, ENCODE);
    }


    public static List<String> getChildren(String path) throws Exception {
        return getClient().getChildren().forPath(path);
    }


    public static void delete(String path) throws Exception {
        getClient().delete().guaranteed().forPath(path);
    }


    public static void deleteWithChildren(String path) throws Exception {
        getClient().delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
    }


    public static boolean exists(String path) throws Exception {
        Stat stat = getClient().checkExists().forPath(path);
        return !(stat == null);
    }


    public static void setAcl(String path) throws Exception {
        getClient().setACL()
                .withACL(Collections.singletonList(new ACL(ZooDefs.Perms.ALL, new Id("digest", generateDigest(auth)))))
                .forPath(path);
    }
}
