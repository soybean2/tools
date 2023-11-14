package com.syb.netty.c1;

import com.google.common.annotations.VisibleForTesting;

import java.nio.ByteBuffer;

/**
 * @Author: sun
 * @Date: 2023/11/11/17:24
 */
public class TestBufferRead {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        buffer.flip();
        System.out.println((char) buffer.get());
        System.out.println(buffer.get());
        buffer.mark();//加标记索引为2的位置
        System.out.println(buffer.get());
        System.out.println(buffer.get());
        buffer.reset();//重置到索引为2的位置
        System.out.println(buffer.get());
        System.out.println(buffer.get());

        //get(i) 不会改变position的位置
        System.out.println((char) buffer.get(3));
    }
}
