package com.syb.netty.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @Author: sun
 * @Date: 2023/11/14/13:48
 */
public class ClientTest {
    public static void main(String[] args) throws IOException {
        //新建一个客户端

        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8080));
        sc.write(Charset.defaultCharset().encode("hello99999999world\n"));
        System.out.println("waiting...");
    }
}
