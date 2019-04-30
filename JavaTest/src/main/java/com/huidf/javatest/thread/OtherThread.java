package com.huidf.javatest.thread;

/**
 * 作者：ZhuTao
 * 创建时间：2019/3/15 : 18:40
 * 描述：
 */
public class OtherThread implements Runnable {
    private int ticket = 5;

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
//使用同步代码块进行同步
            synchronized (this) {
                if (this.ticket > 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + "卖票，余票：" + (--this.ticket));
                }
            }
        }
    }

    public static void main(String[] args) {
        OtherThread ot = new OtherThread();
        new Thread(ot, "一号窗口").start();
        new Thread(ot, "二号窗口").start();
        new Thread(ot, "三号窗口").start();
    }
}
