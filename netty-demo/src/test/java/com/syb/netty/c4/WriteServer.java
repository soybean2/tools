package com.syb.netty.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @Author: sun
 * @Date: 2023/11/14/15:37
 */
public class WriteServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        Selector selector = Selector.open();
        SelectionKey skey = ssc.register(selector, 0, null);
        skey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));

        while(true){
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()){
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    //向客户端发送大量数据
                    SelectionKey scKey = sc.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 30000000; i++) {
                        sb.append("a");
                    }
                    //返回值为实际写入的字节数
                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());
                    int write = sc.write(buffer);
                    System.out.println(write);

                    // 3、判断是否还有剩余内容
                    if (buffer.hasRemaining()) {
                        // 4、关注可写事件
                         sc.register(selector, SelectionKey.OP_WRITE, null);
                         //关注可写事件
                         scKey.interestOps(scKey.interestOps()+SelectionKey.OP_WRITE);
                         //5.把未写完的数据挂到skey上
                         scKey.attach(buffer);
                    }

                // 6、可写事件
                }else if (key.isWritable()){
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel channel = (SocketChannel) key.channel();
                    int write = channel.write(buffer);
                    System.out.println(write);
                    // 7、清理操作
                    if (!buffer.hasRemaining()){
                        key.attach(null);
                        //取消关注可写事件
                        key.interestOps(key.interestOps()-SelectionKey.OP_WRITE);
                    }
                }

            }
        }
    }
}
