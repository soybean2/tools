package com.syb.netty.c5;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @Author: sun
 * @Date: 2023/12/03/22:32
 */
@Slf4j
public class EventLoopServer {
    public static void main(String[] args) {
        //细分2：创建一个独立的EventLoopGroup
        DefaultEventLoopGroup group = new DefaultEventLoopGroup();
        new ServerBootstrap()
                //细分1 ：boss和worker
                //boss负责ServerSocketChannel上accept事件，worker负责SocketChannel上的读写事件
                .group(new NioEventLoopGroup(),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast("handler1",new ChannelInboundHandlerAdapter(){
                            @Override   //Bytebuf
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf msg1 = (ByteBuf) msg;
                                log.debug(msg1.toString(Charset.defaultCharset()));
                                ctx.fireChannelRead(msg);//传递给下一个handler
                            }
                        }).addLast(group,"handler2",new ChannelInboundHandlerAdapter(){
                            @Override   //Bytebuf
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf msg1 = (ByteBuf) msg;
                                log.debug(msg1.toString(Charset.defaultCharset()));
                            }
                        });
                    }
                })
                .bind(8080);

    }
}
