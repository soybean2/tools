package com.syb.netty.c1;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.syb.netty.c1.ByteBufferUtil.debugAll;

/**
 * @Author: sun
 * @Date: 2023/11/11/17:28
 */
public class TestByteBufferString {
    public static void main(String[] args) {
        //1.字符串转为ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put("hello".getBytes());
        debugAll(buffer);

        //2.Charset
        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode("hello");
        debugAll(buffer1);

        //3.wrap
        ByteBuffer buffer2 = ByteBuffer.wrap("hello".getBytes());
        debugAll(buffer2);

        //4.转成字符串
        String str1 = StandardCharsets.UTF_8.decode(buffer1).toString();
        System.out.println(str1);

    }
}
