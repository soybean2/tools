package com.syb.netty.c3;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @Author: sun
 * @Date: 2023/11/13/8:29
 */
public class Client1 {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080)) {
            System.out.println(socket);
            ByteBuffer hello = Charset.defaultCharset().encode("hello");
//            socket.getOutputStream().write("world".getBytes());
            socket.getOutputStream().write(hello.array());
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
