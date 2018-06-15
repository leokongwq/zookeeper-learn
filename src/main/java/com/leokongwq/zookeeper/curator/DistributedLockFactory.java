package com.leokongwq.zookeeper.curator;

/**
 * @author : kongwenqiang
 * DateTime: 18-6-15 下午5:47
 * Mail:leokongwq@gmail.com
 * Description: desc
 */
public interface DistributedLockFactory {

    DistributedLock createLock();
}