package com.leokongwq.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.*;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author : kongwenqiang
 * DateTime: 18-6-15 下午4:44
 * Mail:leokongwq@gmail.com
 * Description: desc
 */
public class LeaderElectionTest extends BaseTest {

    private String leaderSelectionPath = "/config/vip/db/master";

    @Test
    public void testLeaderElection() throws Exception {
        ExecutorService threadPool = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {
            threadPool.execute(new Contender(buildLeaderSelectorListener()));
        }

        Thread.sleep(1000 * 1000);

        threadPool.shutdown();
    }

    @Test
    public void testLeaderRandomElection() throws Exception {
        ExecutorService threadPool = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {
            LeaderLatch leaderLatch = new LeaderLatch(curatorFramework, leaderSelectionPath, String.valueOf(i));
            threadPool.execute(new RandomSelectLeader(leaderLatch));
        }

        Thread.sleep(1000 * 1000);

        threadPool.shutdown();
    }

    /**
     * 公平的选举（顺序性）
     */
    class Contender implements Runnable {
        private LeaderSelectorListener listener;

        Contender(LeaderSelectorListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            LeaderSelector selector = new LeaderSelector(curatorFramework, leaderSelectionPath, listener);
            // not required, but this is behavior that you will probably expect
            selector.autoRequeue();
            //开始进行选举，不过该方法会立即返回。选举的结果是通过异步回调实现的
            selector.start();
        }
    }

    private LeaderSelectorListener buildLeaderSelectorListener() {
        LeaderSelectorListener listener = new LeaderSelectorListenerAdapter() {
            @Override
            public void takeLeadership(CuratorFramework client) throws Exception {
                // this callback will get called when you are the leader
                // do whatever leader work you need to and only exit
                // this method when you want to relinquish leadership
                Thread.sleep(5000);

                System.out.println("Current Leader is : " + Thread.currentThread().getName());
            }
        };
        return listener;
    }

    class RandomSelectLeader implements Runnable {

        private LeaderLatch leaderLatch;

        RandomSelectLeader(LeaderLatch leaderLatch) {
            this.leaderLatch = leaderLatch;
        }

        @Override
        public void run() {
            try {
                //强烈建议添加Listener来监听链接变化，　处理Leader丢失问题
                leaderLatch.addListener(new LeaderLatchListener() {
                    @Override
                    public void isLeader() {
                        System.out.println("I'am Selected to be a Leader" + Thread.currentThread().getName());
                    }

                    @Override
                    public void notLeader() {
                        System.out.println("I'am Not Leader" + Thread.currentThread().getName());
                    }
                });
                //调用该方法后才能开始参与选举（随机的）
                leaderLatch.start();
                //死等，　直到被选为Leader
                leaderLatch.await();

                System.out.println("Current Leader is : " + Thread.currentThread().getName());

                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    // 退出选举，如果自己是Leader，则放弃Leader位置，其它的成员就可以再次选举
                    leaderLatch.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}