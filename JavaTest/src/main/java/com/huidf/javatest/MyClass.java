package com.huidf.javatest;

import com.huidf.javatest.thread.RunnableClass;
import com.huidf.javatest.thread.ThreadClass;

import java.util.regex.Pattern;

public class MyClass {
    public static void main(String[] args) throws InterruptedException {
//        System.out.print("1到10递归阶乘" + dgjc10(10));
    }

    //递归阶乘
    private static Long dgjc10(int num){
        if(num == 1){
            return 1L;
        }
        return num * dgjc10(num-1);
    }


    private static boolean hsZero(String parent){

        Pattern mPattern = Pattern.compile("");
        boolean mMatcher = Pattern.matches(parent,"");
        return false;
    }
}
