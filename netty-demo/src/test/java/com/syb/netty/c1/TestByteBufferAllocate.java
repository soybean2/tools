package com.syb.netty.c1;

import java.nio.ByteBuffer;

/**
 * @Author: sun
 * @Date: 2023/11/11/17:15
 */
public class TestByteBufferAllocate {
    public static void main(String[] args) {
        // 1.分配一个指定大小的缓冲区
        ByteBuffer buffer1 = ByteBuffer.allocate(10); //堆内存，读写效率低，受到GC影响
        System.out.println(buffer1);
        // 2.包装一个现有的数组
        byte[] bytes = new byte[10];
        ByteBuffer buffer2 = ByteBuffer.wrap(bytes);
        System.out.println(buffer2);
        // 3.分配一个指定大小的缓冲区，但是是直接缓冲区，相比非直接缓冲区，速度更快，不安全
        ByteBuffer buffer3 = ByteBuffer.allocateDirect(10); //直接内存，读写效率高，不受GC影响
        System.out.println(buffer3);
    }
}
