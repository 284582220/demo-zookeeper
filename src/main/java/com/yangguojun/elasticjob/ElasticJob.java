package com.yangguojun.elasticjob;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobRootConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ElasticJob {

    public static void main(String[] args) throws UnknownHostException {
        System.out.println("Start...");
        System.out.println(InetAddress.getLocalHost());
        CoordinatorRegistryCenter coordinatorRegistryCenter = createRegistryCenter();
//        createSimpleJobConfiguration();
    }


    private static CoordinatorRegistryCenter createRegistryCenter() {
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration("172.19.100.128:2181,172.19.100.129:2181,172.19.100.131:2181", "elastic-job");
        zookeeperConfiguration.setDigest("taskCenter:TaskCenter!@#2018");
        CoordinatorRegistryCenter zookeeperRegistryCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);
        zookeeperRegistryCenter.init();
        zookeeperRegistryCenter.getChildrenKeys("/test");
        return zookeeperRegistryCenter;
    }

    private static LiteJobConfiguration createSimpleJobConfiguration() {
        // 定义作业核心配置
        JobCoreConfiguration simpleCoreConfig = JobCoreConfiguration.newBuilder("SimpleJobDemo", "0/15 * * * * ?", 10).build();
        // 定义SIMPLE类型配置
        SimpleJobConfiguration simpleJobConfig = new SimpleJobConfiguration(simpleCoreConfig, MyElasticSimpleJob.class.getCanonicalName());
        // 定义Lite作业根配置
        JobRootConfiguration simpleJobRootConfig = LiteJobConfiguration.newBuilder(simpleJobConfig).build();

        return (LiteJobConfiguration) simpleJobRootConfig;
    }

    private static LiteJobConfiguration createDataflowJobConfiguration() {
        // 定义作业核心配置
        JobCoreConfiguration dataflowCoreConfig = JobCoreConfiguration.newBuilder("DataflowJob", "0/30 * * * * ?", 10).build();
        // 定义DATAFLOW类型配置
//        DataflowJobConfiguration dataflowJobConfig = new DataflowJobConfiguration(dataflowCoreConfig, MyElasticDataflowJob.class.getCanonicalName(), true);
//        // 定义Lite作业根配置
//        JobRootConfiguration dataflowJobRootConfig = LiteJobConfiguration.newBuilder(dataflowJobConfig).build();

//        return (LiteJobConfiguration) dataflowJobRootConfig;
        return null;
    }

//    private void initZookeeper(){
//        String subNamespace = "";
//        if (!subNamespace.startsWith("/")) {
//            subNamespace = "/" + subNamespace;
//        }
//
//        ZookeeperConfig zookeeperConfig = YzSchedulerConfigLoader.loadZookeeperConfig();
//        String serverList = zookeeperConfig.getServerList();
//        String namespace = zookeeperConfig.getNamespace() + subNamespace;
//
//        logger.info("Scheduler-zookeeper paramaters: namespace({}), worker dataId({}), zkTimeoutMs({}).", namespace, dataId, zkTimeoutMs);
//
//        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(serverList, namespace);
//        zookeeperConfiguration.setDigest(zookeeperConfig.getDigest());
//
//        if (zkTimeoutMs != 0) {
//            zookeeperConfiguration.setMaxSleepTimeMilliseconds(zkTimeoutMs);
//        }
//
//        zkRegistryCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);
//        zkRegistryCenter.init();
//    }
}