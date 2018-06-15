package com.leokongwq.zookeeper.curator;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author : kongwenqiang
 * DateTime: 18-6-15 下午3:17
 * Mail:leokongwq@gmail.com
 * Description: desc
 */
public class DistributedLockTest extends BaseTest {

    private String lockPath = "/config/lock";

    private int counter = 0;
    private CountDownLatch start = new CountDownLatch(1);
    private CountDownLatch finish = new CountDownLatch(20);

    @Test
    public void testDistributedLock() throws Exception {

        ZookeeperDistributedLock distributedLock = new ZookeeperDistributedLock(curatorFramework);

        ExecutorService threadPool = Executors.newFixedThreadPool(20);

        for (int i = 0; i < 20; i++) {
//            threadPool.execute(new Worker(start, finish));
            threadPool.execute(new Worker(start, finish, distributedLock));
        }
        start.countDown();
        finish.await();

        System.out.println(counter);

        threadPool.shutdown();
    }

    private class Worker implements Runnable {

        private CountDownLatch start;
        private CountDownLatch finish;
        private ZookeeperDistributedLock distributedLock;

        Worker(CountDownLatch start, CountDownLatch finish) {
            this.start = start;
            this.finish = finish;
        }

        Worker(CountDownLatch start, CountDownLatch finish, ZookeeperDistributedLock distributedLock) {
            this.start = start;
            this.finish = finish;
            this.distributedLock = distributedLock;
        }

        public void run() {
            try {
                start.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (this.distributedLock != null && distributedLock.lock(lockPath, 1000000)) {
                for (int i = 0 ; i < 5; i++) {
                    counter++;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (this.distributedLock != null) {
                    distributedLock.unLock(lockPath);
                }
            }
            finish.countDown();
        }
    }
}