package com.syb.netty.c6;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * @Author: sun
 * @Date: 2023/12/04/19:10
 */
@Slf4j
public class TestNettyPromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //1.准备EventLoop对象
        EventLoop eventLoop = new NioEventLoopGroup().next();

        //2.主动创建promise对象,结果容器
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);

        //3.任意线程执行计算，计算完毕后向promise填充结果
        new Thread(() ->{
            log.debug("开始计算");
            try {
//                int i = 1/0;
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
                promise.setFailure(e);
            }
            promise.setSuccess(80);
        }).start();

        //4、接收结果的线程
        log.debug("等待结果");
        log.debug("{}",promise.getNow()); // 还没有结果
        log.debug("{}",promise.get()); // 阻塞等待结果
    }
}
