package com.huidf.javatest.thread;

/**
 * 作者：ZhuTao
 * 创建时间：2019/3/15 : 14:23
 * 描述：直接
 */
public class ThreadClass extends Thread {

    private String threadName;
//    private Thread thread;

    public ThreadClass(String theadName) {
        this.threadName = theadName;
        System.out.println("新建线程" + threadName);
    }

    public void startThread(){
        System.out.println("启动线程 isAlive() ：" + isAlive() +  "  " + threadName);
        setName(threadName);
        start();

    }

    /** 线程进入join状态，将会优先执行，阻塞进程中的其他线程 */
    public void setJoin(){
        System.out.println("执行线程等待，该线程会优先执行" + threadName);
         try {
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("运行线程" + threadName);
        for (int i = 0; i < 5; i++) {
            try {
                //让线程睡一会
                this.sleep(300);
                if(i==3){
                    System.out.println("中断线程" + threadName);
                    interrupt();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                if(isInterrupted()){    //判断线程中断后，需要跳出循环，否则再次调用时会报异常
                    System.out.println("线程中断 isAlive() ：" + isAlive() +  "  " + threadName + i);
//                    break;
                }
                System.out.println("运行线程 isAlive() ：" + isAlive() +  "  " + threadName + i);
            }
        }
        System.out.println("线程运行结束 isAlive() ：" + isAlive() +  "  " + threadName);
    }

}
