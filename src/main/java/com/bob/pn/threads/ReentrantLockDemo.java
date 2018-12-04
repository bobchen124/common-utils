package com.bob.pn.threads;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author chenbo@guworks.cc
 * @title xxx
 * @date 2018年11月23日
 * @since v1.0.0
 */
public class ReentrantLockDemo {

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();//(1)
        Condition condition = lock.newCondition();//(2)

        lock.lock();//(3)
        try {
            System.out.println("begin wait");
            condition.await();//(4)
            System.out.println("end wait");

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            lock.unlock();//(5)
        }

        lock.lock();//(6)
        try {
            System.out.println("begin signal");
            condition.signal();//(7)
            System.out.println("end signal");
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            lock.unlock();//(8)
        }

    }

}
