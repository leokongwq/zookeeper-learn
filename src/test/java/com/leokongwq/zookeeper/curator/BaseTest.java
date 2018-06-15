package com.leokongwq.zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author : kongwenqiang
 * DateTime: 18-6-15 下午3:13
 * Mail:leokongwq@gmail.com
 * Description: desc
 */
public class BaseTest {

    private static final String zookeeperConnectionString = "127.0.0.1:2181";

    static CuratorFramework curatorFramework;

    @BeforeClass
    public static void buildCuratorFramework() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        curatorFramework = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        curatorFramework.start();
    }

    @AfterClass
    public static void destory() {
        if (null != curatorFramework) {
            curatorFramework.close();
        }
    }

}