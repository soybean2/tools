package com.syb.netty.c2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @Author: sun
 * @Date: 2023/11/12/21:48
 */
public class TestFileChannelTransferTo {

    public static void main(String[] args) {
        try (FileChannel from = new FileInputStream("netty-demo/data.txt").getChannel();
             FileChannel to = new FileOutputStream("netty-demo/to.txt").getChannel();){
            // 效率高，底层会利用操作系统的零拷贝进行优化 最多传输2G
            long size = from.size();
            for (long left = size; left > 0; ) {
                System.out.println("position=" + (size - left) + " count=" + left);
                left -= from.transferTo((size - left), left, to);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
