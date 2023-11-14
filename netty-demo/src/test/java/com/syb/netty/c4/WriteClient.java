package com.syb.netty.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @Author: sun
 * @Date: 2023/11/14/15:46
 */
public class WriteClient {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8080));

        //接收数据
        int count = 0;
        while(true){
            ByteBuffer buffer = ByteBuffer.allocate(1024*1024);
            int read = sc.read(buffer);
            count += read;
            System.out.println(count);
            buffer.clear();
        }

    }
}
