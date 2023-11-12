package com.syb.netty.c1;

import lombok.extern.slf4j.Slf4j;
import sun.rmi.runtime.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Author: sun
 * @Date: 2023/11/11/16:11
 */
@Slf4j
public class TestByteBuffer {
    public static void main(String[] args) {
        // FileChannel
        // 1.输入输出流 2.RandomAccessFile
        try(FileChannel channel = new FileInputStream("netty-demo/data.txt").getChannel()) {
            //准备缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);//10个字节
            while (true){
                int len = channel.read(buffer);
                log.debug("读到字节数：{}", len);
                if (len == -1){
                    break;
                }
                buffer.flip();//切换到读模式
                while (buffer.hasRemaining()) {
                    byte b = buffer.get();
                    log.debug("实际字节 {}",(char)b);
//                    System.out.println((char)b);
                }
                buffer.clear();
            }



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
