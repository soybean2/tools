package com.syb.netty.c3;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.syb.netty.c1.ByteBufferUtil.debugAll;

/**
 * @Author: sun
 * @Date: 2023/11/13/8:17
 */
@Slf4j
public class Server1 {
    // 非阻塞，单线程
    public static void main(String[] args) throws IOException {
        // 1.创建selector，管理多个channel
        Selector selector = Selector.open();
        ByteBuffer buffer = ByteBuffer.allocate(16);

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);//非阻塞模式

        // 2.注册到selector，监听连接事件
        //selectKey 将来事件发生后用过它可以知道哪个事件以及哪个channel
        SelectionKey ssckey = ssc.register(selector, 0,null);
        // accept 会在有连接请求时触发

        // connect 会在连接建立后触发
        // read 会在有数据可读时触发
        // write 会在有空间可写时触发
        ssckey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register key:{}", ssckey);
        ssc.bind(new InetSocketAddress(8080));
        List<SocketChannel> channels = new ArrayList<>();
        while (true) {
            //3.select方法，阻塞线程
            // 未处理事件，select不会阻塞 可以将事件取消
            // 事件发生后要不处理，要不取消
            int select = selector.select();//阻塞方法，等待事件发生，每次返回有事件发生的channel数量
            log.debug("select:{}", select);
            //4.处理事件，selectedKeys 内部包含了所有发生的事件以及channel
            Set<SelectionKey> selectKeys = selector.selectedKeys();//所有发生的事件
            Iterator<SelectionKey> iter = selectKeys.iterator();
            while (iter.hasNext()){
                SelectionKey key = iter.next();//拿到selectKey sscKey
                log.debug("selectKeys:{}", selectKeys);
                // 处理sscKey
                if (key.isAcceptable()){
                    ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                    SocketChannel sc = channel.accept();
                    log.debug("socketChannel:{}", sc);
                    sc.configureBlocking(false);//非阻塞
                    SelectionKey scKey = sc.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);// 读事件
                    log.debug("scKey:{}", scKey);
                } else if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel)key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                    channel.read(byteBuffer);
                    buffer.flip();
                    debugAll(buffer);

                }


                iter.remove();//移除当前的selectKey
                key.cancel();
            }
        }
    }
}
