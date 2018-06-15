package com.leokongwq.zookeeper.curator;

import com.google.common.collect.Maps;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @author : kongwenqiang
 * DateTime: 18-6-15 下午3:18
 * Mail:leokongwq@gmail.com
 * Description: desc
 */
public class ZookeeperDistributedLock implements DistributedLock {

    private CuratorFramework curatorFramework;

    private ConcurrentMap<String, InterProcessLock> distributedLockContainer = Maps.newConcurrentMap();

    private boolean reentent;

    public ZookeeperDistributedLock(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    @Override
    public boolean tryLock(String lockKey) {
        return lock(lockKey, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean lock(String lockKey) {
        return lock(lockKey, null, TimeUnit.SECONDS);
    }

    @Override
    public boolean lock(String lockKey, Integer waitTime, TimeUnit timeUnit) {
        InterProcessLock lock;
        if (reentent) {
            lock = new InterProcessMutex(curatorFramework, lockKey);
        } else {
            lock = new InterProcessSemaphoreMutex(curatorFramework, lockKey);
        }

        InterProcessLock existLock = distributedLockContainer.putIfAbsent(lockKey, lock);
        if (null != existLock) {
            lock = existLock;
        }
        System.out.println(Thread.currentThread().getName() + "=== get lock ===>>>>>" + lock);
        try {
            if (null != waitTime) {
                return lock.acquire(waitTime, timeUnit);
            } else {
                lock.acquire();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    @Override
    public void unlock(String lockKey) {
        InterProcessLock existLock = distributedLockContainer.get(lockKey);
        if (null == existLock) {
            throw new RuntimeException("Can Not Release Nonexistent Lock !");
        }
        System.out.println(Thread.currentThread().getName() + "=== release lock ===>>>>>" + existLock);
        try {
            existLock.release();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}