package com.syb.client;

import com.syb.message.*;
import com.syb.protocol.MessageCodecSharable;
import com.syb.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG); // 日志打印
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable(); // 编解码
        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1); // 等待登录
        AtomicBoolean LOGIN = new AtomicBoolean(false); // 是否登录成功
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder()); // 协议解码器
                    // ch.pipeline().addLast(LOGGING_HANDLER); // 日志打印
                    ch.pipeline().addLast(MESSAGE_CODEC); //编解码器
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.debug("msg:{}",msg);
                            if(msg instanceof LoginResponseMessage){ //登录响应
                                LoginResponseMessage response = (LoginResponseMessage) msg;
                                if (response.isSuccess()) {
                                    // 如果登录成功
                                    LOGIN.set(true);
                                }
                            }
                            //唤醒system in 线程
                            WAIT_FOR_LOGIN.countDown();
                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            // 在连接建立后触发active事件
                            new Thread(()->{
                                //负责接收用户在控制台的输入，负责向服务器发送各种消息
                                Scanner sc = new Scanner(System.in);
                                System.out.println("请输入用户名：");
                                String username = sc.nextLine();
                                System.out.println("请输入密码：");
                                String password = sc.nextLine();
                                //构建登录消息对象
                                // TODO 校验用户名密码
                                LoginRequestMessage message = new LoginRequestMessage(username, password);
                                ctx.writeAndFlush(message);
                                System.out.println("等待后续操作");
                                try {
                                    WAIT_FOR_LOGIN.await(); // 等待登录响应
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                if (!LOGIN.get()) {
                                    // 如果没有登录成功，就直接关闭连接
                                    ctx.channel().close();
                                    return;
                                }
                                while (true){
                                    System.out.println("==================================");
                                    System.out.println("send [username] [content]");
                                    System.out.println("gsend [group name] [content]");
                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                    System.out.println("gmembers [group name]");
                                    System.out.println("gjoin [group name]");
                                    System.out.println("gquit [group name]");
                                    System.out.println("quit");
                                    System.out.println("==================================");
                                    String command = sc.nextLine();//输入命令
                                    String[] s = command.split(" ");
                                    switch (s[0]){
                                        case "send":
                                            //发送消息
                                            ChatRequestMessage chatRequestMessage = new ChatRequestMessage(username, s[1], s[2]);
                                            ctx.writeAndFlush(chatRequestMessage);//发送聊天消息
                                            break;
                                        case "gsend":
                                            //发送群聊消息
                                            GroupChatRequestMessage groupChatRequestMessage = new GroupChatRequestMessage(username, s[1], s[2]);
                                            ctx.writeAndFlush(groupChatRequestMessage);//发送群聊消息
                                            break;
                                        case "gcreate":
                                            //创建群聊
                                            Set<String> set = new HashSet<>(Arrays.asList(s[2].split(",")));
                                            GroupCreateRequestMessage groupCreateRequestMessage = new GroupCreateRequestMessage(s[1], set);
                                            ctx.writeAndFlush(groupCreateRequestMessage);//发送创建群聊消息
                                            break;
                                        case "gmembers":
                                            //查看群成员
                                            GroupMembersRequestMessage groupMembersRequestMessage = new GroupMembersRequestMessage(s[1]);
                                            ctx.writeAndFlush(groupMembersRequestMessage);//发送查看群成员消息
                                            break;
                                        case "gjoin":
                                            //加入群聊
                                            GroupJoinRequestMessage groupJoinRequestMessage = new GroupJoinRequestMessage(username, s[1]);
                                            ctx.writeAndFlush(groupJoinRequestMessage);//发送加入群聊消息
                                            break;
                                        case "gquit":
                                            GroupQuitRequestMessage groupQuitRequestMessage = new GroupQuitRequestMessage(username, s[1]);
                                            ctx.writeAndFlush(groupQuitRequestMessage);
                                            //退出群聊
                                            break;
                                        case "quit":
                                            //退出
                                            ctx.channel().close();
                                            return;
                                    }
                                }
                            },"system.in").start();
                        }
                    }); // 自定义客户端处理器
                }
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
