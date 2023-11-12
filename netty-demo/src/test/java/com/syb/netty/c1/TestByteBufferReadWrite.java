package com.syb.netty.c1;

import java.nio.ByteBuffer;

import static com.syb.netty.c1.ByteBufferUtil.debugAll;

/**
 * @Author: sun
 * @Date: 2023/11/11/17:08
 */
public class TestByteBufferReadWrite {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 0x61);//a
        debugAll(buffer);
        buffer.put(new byte[]{0x62, 0x63, 0x64});//bcd
        debugAll(buffer);
        buffer.flip();
        System.out.println(buffer.get());
        debugAll(buffer);
        buffer.compact();
        debugAll(buffer);
        buffer.put(new byte[]{0x65, 0x66});
        debugAll(buffer);
    }
}
