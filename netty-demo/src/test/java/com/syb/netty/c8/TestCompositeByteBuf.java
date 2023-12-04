package com.syb.netty.c8;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * @Author: sun
 * @Date: 2023/12/04/22:22
 */
public class TestCompositeByteBuf {
    public static void main(String[] args) {
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer();
        buf1.writeBytes(new byte[]{1,2,3,4,5});

        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();
        buf2.writeBytes(new byte[]{6,7,8,9,10});

//        ByteBuf buf3 = ByteBufAllocator.DEFAULT.buffer();
//        buf3.writeBytes(buf1).writeBytes(buf2); //进行了数据的内存复制操作
//        log(buf3);

        //使用CompositeByteBuf
        //CompositeByteBuf 是一个组合的 ByteBuf，它内部维护了一个 Component 数组，
        // 每个 Component 管理一个 ByteBuf，记录了这个 ByteBuf 相对于整体偏移量等信息，代表着整体中某一段的数据。
        CompositeByteBuf buf = ByteBufAllocator.DEFAULT.compositeBuffer();
        //true 表示增加新的 ByteBuf 自动递增 write index, 否则 write index 会始终为 0
        buf.addComponents(true,buf1,buf2);
        log(buf);


    }

    private static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index:").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(NEWLINE);
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf);
    }
}
