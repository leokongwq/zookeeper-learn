package com.leokongwq.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;

/**
 * @author : kongwenqiang
 * DateTime: 18-6-15 下午6:00
 * Mail:leokongwq@gmail.com
 * Description: desc
 */
public class ZookeeperDistributedLockFactory implements DistributedLockFactory {

    private CuratorFramework curatorFramework;

    @Override
    public DistributedLock createLock() {
        return new ZookeeperDistributedLock(curatorFramework);
    }
}