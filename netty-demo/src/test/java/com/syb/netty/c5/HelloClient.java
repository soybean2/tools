package com.syb.netty.c5;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * @Author: sun
 * @Date: 2023/12/03/20:15
 */
public class HelloClient {
    public static void main(String[] args) throws InterruptedException {
        // 1.启动器，负责组装netty组件，启动客户端
        new Bootstrap()
                // 2.添加EventLoop
                .group(new NioEventLoopGroup())
                // 3.选择客户端channel实现
                .channel(NioSocketChannel.class)
                // 4.添加处理器handler
                .handler(
                        // 5.连接到服务器
                        new ChannelInitializer<NioSocketChannel>() {
                    @Override // 在连接建立后被调用
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 6.添加具体handler
                        ch.pipeline().addLast(new StringEncoder()); // 将字符串转换为ByteBuf
                    }
                })
                .connect(new InetSocketAddress("localhost",8080)) // 7.连接到服务器
                .sync() // 阻塞方法，直到连接建立
                .channel() // 代表和服务器通信的通道
                // 8.向服务器发送数据
                .writeAndFlush("hello,world");

    }
}
