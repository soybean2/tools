package com.syb.netty.c1;

import java.nio.ByteBuffer;

import static com.syb.netty.c1.ByteBufferUtil.debugAll;

/**
 * @Author: sun
 * @Date: 2023/11/12/21:32
 * 处理粘包半包问题
 */
public class TestByteBufferExam {
    public static void main(String[] args) {
        /**
         * 网络上有多条数据发送给服务端，数据之间使用 \n 进行分隔
         * 但是由于某种原因，在接收时只收到了一部分数据，剩下的数据会在下一次接收到的时候发送过来
         * 例如：
         * 客户端发送了三条数据：
         * 1.hello,world\n
         * 2.I'm Zhangsan\n
         * 3.How are you?\n
         *
         * 即可能出现以下情况：
         * hello,world\nI'm Zhangsan\nHo
         * w are you?\n
         *
         */
        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("hello,world\nI'm Zhangsan\nHo".getBytes());
        split(source);
        source.put("w are you?\n".getBytes());
        split(source);

    }

    private static void split(ByteBuffer source) {
        source.flip();
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
