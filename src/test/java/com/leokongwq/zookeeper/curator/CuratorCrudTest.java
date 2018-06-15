package com.leokongwq.zookeeper.curator;

import org.apache.zookeeper.data.Stat;
import org.junit.Test;

/**
 * @author : kongwenqiang
 * DateTime: 18-6-15 下午2:50
 * Mail:leokongwq@gmail.com
 * Description: desc
 */
public class CuratorCrudTest extends BaseTest {

    private String curdTestPath  = "/config/lock";

    /**
     * 不能重复创建
     * @throws Exception　throw KeeperException$NodeExistsException if Node Exist.
     */
    @Test
    public void testCreate() throws Exception {
        Stat stat = curatorFramework.checkExists().forPath(curdTestPath);
        if (null != stat) {
            curatorFramework.delete().forPath(curdTestPath);
        }
        curatorFramework.create().creatingParentsIfNeeded().forPath(curdTestPath, "hello".getBytes());
    }

    @Test
    public void testExist() throws Exception {
        Stat stat = curatorFramework.checkExists().forPath(curdTestPath);
        System.out.println(stat);
    }

    /**
     * test delete Znode
     * @throws Exception　throw KeeperException$NoNodeException If Node Not Exist.
     */
    @Test
    public void testDelete() throws Exception {
        curatorFramework.delete().forPath("/abc");
    }

    @Test
    public void testGetData() throws Exception {
        byte[] data = curatorFramework.getData().forPath(curdTestPath);
        if (null != data) {
            System.out.println(new String(data));
        }
    }
}