package com.syb.netty.c3;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static com.syb.netty.c1.ByteBufferUtil.debugAll;

/**
 * @Author: sun
 * @Date: 2023/11/12/22:49
 */
@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        //使用nio来理解阻塞模式,单线程
        //1、创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ByteBuffer buffer = ByteBuffer.allocate(16);
        //2、绑定监听端口
        ssc.bind(new InetSocketAddress(8080));
        //3、accept阻塞方法，等待客户端连接,返回socketChannel
        List<SocketChannel> channels = new ArrayList<>();
        while (true) {
            log.debug("connecting...");
//            System.out.println("等待连接...");
            SocketChannel sc = ssc.accept();//阻塞方法，线程停止运行
            log.debug("connected...{}", sc);
            //4、有客户端连接后，为客户端生成一个socketChannel
            channels.add(sc);
//            System.out.println("有客户端连接了...");
            //5、接收客户端的数据
            for (SocketChannel channel : channels) {
                log.debug("before read...{}", channel);
                channel.read(buffer);//阻塞方法,线程停止运行
                buffer.flip();
                debugAll(buffer);
                buffer.clear();
                log.debug("after read...{}", channel);

            }
        }
    }
}
