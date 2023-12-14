package com.syb.server.handler;

import com.syb.message.ChatRequestMessage;
import com.syb.message.ChatResponseMessage;
import com.syb.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author: sun
 * @Date: 2023/12/07/15:54
 */
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ChatRequestMessage msg) throws Exception {
        String msgTo = msg.getTo();
        Channel channel = SessionFactory.getSession().getChannel(msgTo);
        //在线
        if (channel != null) {
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(),msg.getContent()));
        }
        //离线
        else {
            //返回一个提示
            channelHandlerContext.writeAndFlush(new ChatResponseMessage(false, "对方用户不存在或者不在线"));

        }
    }
}
