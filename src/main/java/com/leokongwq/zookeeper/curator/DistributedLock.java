package com.leokongwq.zookeeper.curator;

import java.util.concurrent.TimeUnit;

/**
 * @author : kongwenqiang
 * DateTime: 18-6-15 下午5:50
 * Mail:leokongwq@gmail.com
 * Description: desc
 */
public interface DistributedLock {

    boolean tryLock(final String lockKey);

    boolean lock(final String lockKey);

    boolean lock(final String lockKey, final Integer waitTime, TimeUnit timeUnit);

    void unlock(final String lockKey);
}