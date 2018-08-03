package com.bob.pn.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chenbo@guworks.cc
 * @title xxx
 * @date 2018年07月31日
 * @since v1.0.0
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    /**
     * channelRead 函数是当服务端收到客户端发来的数据时候被回调，
     * 这里函数内部是首先读取客户端发来的数据并打印，然后向客户端写入一些数据。
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf message = (ByteBuf) msg;
        byte[] response = new byte[message.readableBytes()];
        message.readBytes(response);
        System.out.println(atomicInteger.getAndIncrement() + "receive client info: " + new String(response));

        String sendContent = "hello client ,im server";
        ByteBuf seneMsg = Unpooled.buffer(sendContent.length());
        seneMsg.writeBytes(sendContent.getBytes());

        ctx.writeAndFlush(seneMsg);
        System.out.println("send info to client:" + sendContent);
    }

    /**
     * channelActive 函数是当服务端监听到客户端连接，并且完成三次握手后回调。
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("--- accepted client---");
        ctx.fireChannelActive();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
