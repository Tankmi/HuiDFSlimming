package com.huidf.javatest.thread;

/**
 * 作者：ZhuTao
 * 创建时间：2019/3/15 : 14:11
 * 描述：继承Runnable实现子线程
 */
public class RunnableClass implements Runnable {

    private String threadName;
    private Thread thread;

    public RunnableClass(String theadName) {
        this.threadName = theadName;
        System.out.println("新建线程" + threadName);
    }

    public void start(){
        System.out.println("启动线程" + threadName);
        //新建一个带有name的子线程
        if(thread == null){
            thread = new Thread(this,threadName);
        }
        thread.start();
    }


    @Override
    public void run() {
        System.out.println("运行线程" + threadName);
        for (int i = 0; i < 5; i++) {
            try {
                //让线程睡一会
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                System.out.println("运行线程" + threadName + i);
            }
        }
        System.out.println("线程运行结束" + threadName);
    }
}
