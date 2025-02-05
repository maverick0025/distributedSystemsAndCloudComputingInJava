package Demos;/*
 *  MIT License
 *
 *  Copyright (c) 2019 Michael Pogrebinsky - Distributed Systems & Cloud Computing with Java
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * Watchers, Triggers and Introduction to Failure Detection
 * https://www.udemy.com/course/distributed-systems-cloud-computing-with-java
 */
public class WatchersDemo implements Watcher {
    private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
    private static final int SESSION_TIMEOUT = 3000;
    private static final String TARGET_ZNODE = "/target_znode";
    private ZooKeeper zooKeeper;

    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        WatchersDemo watchersDemo = new WatchersDemo();
        watchersDemo.connectToZookeeper();
        watchersDemo.watchTargetZnode();
        watchersDemo.run();
        watchersDemo.close();
    }

    public void connectToZookeeper() throws IOException {
        this.zooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, SESSION_TIMEOUT, this);
    }

    public void run() throws InterruptedException {
        synchronized (zooKeeper) {
            zooKeeper.wait();
        }
    }

    public void close() throws InterruptedException {
        zooKeeper.close();
    }

    public void watchTargetZnode() throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(TARGET_ZNODE, this); //znode creation, deletion
        if (stat == null) {
            return;
        }

        byte[] data = zooKeeper.getData(TARGET_ZNODE, this, stat); //any changes in znode data
        List<String> children = zooKeeper.getChildren(TARGET_ZNODE, this);
        //registering for changes in the getChildren method for target znode's children

        System.out.println("Data : " + new String(data) + ", children : " + children);
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("Successfully connected to Zookeeper");
                } else {
                    synchronized (zooKeeper) {
                        System.out.println("Disconnected from Zookeeper event");
                        zooKeeper.notifyAll();
                    }
                }
                break;
            case NodeDeleted: //exists method watcher
                System.out.println(TARGET_ZNODE + " was deleted");
                break;
            case NodeCreated: //exists method watcher
                System.out.println(TARGET_ZNODE + " was created");
                break;
            case NodeDataChanged: //any changes in znode data
                System.out.println(TARGET_ZNODE + " data changed");
                break;
            case NodeChildrenChanged: //any changes to the children
                System.out.println(TARGET_ZNODE + " children changed");
                break;
        }

        try {
            watchTargetZnode(); //to get all the up-to-date data after the changes and print it to the screen and
            //all events are one time trigger so we need to re register them again
        } catch (KeeperException e) {
        } catch (InterruptedException e) {
        }
    }
}
