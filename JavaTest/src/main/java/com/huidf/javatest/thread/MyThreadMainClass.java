package com.huidf.javatest.thread;

/**
 * 作者：ZhuTao
 * 创建时间：2019/3/15 : 16:15
 * 描述：线程测试类
 */
public class MyThreadMainClass {

    public  static void main(String[] args){
        //卖票
        SellTickets ticket = new SellTickets();
        ticket.addTickets("A");
        ticket.addTickets("B");
        ticket.addTickets("C");
    }
}
