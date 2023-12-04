package com.syb.netty.c5;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Author: sun
 * @Date: 2023/12/03/22:18
 */
@Slf4j
public class TestEventLoop {
    public static void main(String[] args) {
        // 线程数 = cpu核数 * 2
        NioEventLoopGroup group = new NioEventLoopGroup(2); //io 事件、普通任务、定时任务
//        DefaultEventLoop defaultEventLoop = new DefaultEventLoop(); //普通任务、定时任务
        //2.获取下一个事件循环对象
        System.out.println(group.next()); //1
        System.out.println(group.next()); //2
        System.out.println(group.next()); //1

        //3.执行普通任务
        /**
        group.next().execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.debug("ok");
        });

        log.debug("main");
         **/

        //4.执行定时任务
        /**
        group.next().scheduleAtFixedRate(() -> {
            log.debug("ok");
        }, 0, 1, TimeUnit.SECONDS);

        log.debug("main");
         **/

        //
    }
}
