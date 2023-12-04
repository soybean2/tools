package com.syb.netty.c5;

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

/**
 * @Author: sun
 * @Date: 2023/12/03/20:15
 */
@Slf4j
public class ChannelFutureClient {
    public static void main(String[] args) throws InterruptedException {
        // 带有future，promise的类型都是和异步方法配套使用，用来处理结果
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
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

        // 2.1 使用sync方法同步处理结果
//        channelFuture.sync(); // 阻塞当前线程，直到nio线程连接建立
//        Channel channel = channelFuture.channel();// 代表和服务器通信的通道
//        log.debug("{}",channel);
//        channel.writeAndFlush("hello,world");

        // 2.2 使用addListener方法异步处理结果
        channelFuture.addListener(new ChannelFutureListener() {
            // 在nio线程连接建立好之后，会调用operationComplete
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                // 连接建立完成以后执行
                Channel channel = channelFuture.channel();
                log.debug("{}",channel);
                channel.writeAndFlush("hello,world");
            }
        });
    }
}
