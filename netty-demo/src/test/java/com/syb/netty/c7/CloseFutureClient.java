package com.syb.netty.c7;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * @Author: sun
 * @Date: 2023/12/03/20:15
 */
@Slf4j
public class CloseFutureClient {
    public static void main(String[] args) throws InterruptedException {
        // 带有future，promise的类型都是和异步方法配套使用，用来处理结果
        NioEventLoopGroup group = new NioEventLoopGroup(); //事件循环组
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(
                        new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                //连接到服务器 异步非阻塞，main发起调用，真正执行connect是nio线程
                .connect(new InetSocketAddress("localhost",8080)) ;

        Channel channel = channelFuture.sync().channel();
        new Thread(()->{
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();
                if ("q".equals(line)) {
                    log.debug("close");
                    channel.close(); //异步操作
                    break;
                }
                channel.writeAndFlush(line);
            }
        },"input").start();

        //获得CloseFuture对象，1）同步处理关闭 2）异步处理关闭
        ChannelFuture closeFuture = channel.closeFuture();
        //1)同步处理关闭
//        closeFuture.sync();
//        log.debug("处理关闭之后的操作");
        //2)异步处理关闭
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                log.debug("处理关闭之后的操作");
                group.shutdownGracefully();//关闭事件循环组
            }
        });
    }
}
