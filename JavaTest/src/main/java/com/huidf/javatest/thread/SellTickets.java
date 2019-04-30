package com.huidf.javatest.thread;

/**
 * 作者：ZhuTao
 * 创建时间：2019/3/15 : 17:28
 * 描述：实现Runnable接口实现卖票功能（分别实现：1、同步方法，2、同步代码块）
 */
public class SellTickets implements Runnable {

    private int ticketNum = 10;

    public SellTickets() {
        System.out.println("初始化售票系统");
    }

    //添加售票员
    public void addTickets(String threadName){
         new Thread(this,threadName).start();
    }
    @Override
    public void run() {
        while (ticketNum > 0 ){
            //同步代码块 start
            synchronized (this){
                System.out.println(Thread.currentThread().getName() + "查询余票" + ticketNum);
                if(ticketNum >0){
                    System.out.println(Thread.currentThread().getName() + "卖出一张，余票" + --ticketNum);
                }else{
                    System.out.println(Thread.currentThread().getName() + "余票" + ticketNum);
                }
                try {
                    Thread.currentThread().sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //同步代码块 end
//            sellTickets();
        }
    }

    //同步方法
    private synchronized void sellTickets(){
        if(ticketNum >0){
            System.out.println(Thread.currentThread().getName() + "卖出一张，余票" + --ticketNum);
        }else{
            System.out.println(Thread.currentThread().getName() + "余票" + ticketNum);
        }
        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
