package com.syb.netty.c4;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

import static com.syb.netty.c1.ByteBufferUtil.debugAll;


/**
 * @Author: sun
 * @Date: 2023/11/14/13:26
 */
@Slf4j
public class ServerTest {
    public static void main(String[] args) throws IOException {
        //创建一个selector
        Selector selector = Selector.open();
        //创建一个ServerSocketChannel
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //设置非阻塞状态
        ssc.configureBlocking(false);
        //注册到selector上，关注连接事件

        SelectionKey selectionKey = ssc.register(selector, 0, null);
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
        //绑定端口
        ssc.bind(new InetSocketAddress(8080));
        SocketAddress address = ssc.getLocalAddress(); //获取绑定的端口
        while (true) {

            //阻塞等待事件发生
            selector.select();
            System.out.println("事件发生了");
            //如果发生事件就获取所有事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            //遍历所有事件
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                handle(key);
            }
        }

    }

    private static void handle(SelectionKey key) throws IOException {
        if (key.isAcceptable()){
            //如果是Accept事件
            System.out.println("连接事件");
            SocketChannel sc;
            try (ServerSocketChannel channel = (ServerSocketChannel) key.channel()) {
                //处理事件
                sc = channel.accept();
            }
            sc.configureBlocking(false);
            ByteBuffer buffer = ByteBuffer.allocate(16);
            //将buffer作为附件关联到ssc上
            SelectionKey scKey = sc.register(key.selector(), 0, buffer);
            scKey.interestOps(SelectionKey.OP_READ);
        } else if (key.isReadable()) {
            //如果是Read事件
            System.out.println("读事件");
            try {
                SocketChannel channel = (SocketChannel) key.channel();
                //获取之前注册的buffer
                ByteBuffer buffer = (ByteBuffer) key.attachment();

                int read = channel.read(buffer);
                if (read == -1){
                    key.cancel();//正常断开，返回-1

                }else{
                    split(buffer);
                    if (buffer.position() == buffer.limit()){//如果buffer满了，就扩容
                        ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                        buffer.flip();
                        newBuffer.put(buffer);
                        key.attach(newBuffer);
                    }
                }
                buffer.clear();
            }catch (IOException e){
                e.printStackTrace();
                key.cancel();
            }
        }
    }

    public static void split(ByteBuffer source) {
        source.flip();//切换到读模式
        for (int i = 0; i < source.limit(); i++) {
            if (source.get(i) == '\n') { //遍历每个字节，如果遇到\n就说明是一个完整的消息
                int length = i + 1 - source.position(); //获取消息的长度 12+1-0=13
                System.out.println(length);
                ByteBuffer target = ByteBuffer.allocate(length); // 把这条消息存入新的ByteBuffer
                //从source读，向target写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        source.compact();
    }
}
