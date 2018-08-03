package com.bob.pn.netty.client;

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
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private final byte[] request;

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    /**
     * 创建一个客户端 handler.
     */
    public NettyClientHandler() {
        request = "hello server,im a client".getBytes();
    }

    /**
     *  channelActive 函数是当客户端与服务器端链接建立完毕后被回调的函数，
     *  这里函数里面把 hello server,im a client 二进制化后发送到了服务器，并且刷新缓存让数据立刻发送到服务器端。
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("--- client already connected----");

        ByteBuf message = null;
        for (int i = 0; i < 1; ++i) {
            message = Unpooled.buffer(request.length);
            message.writeBytes(request);
            ctx.writeAndFlush(message);
        }
    }

    /**
     * channelRead 函数是当客户端接受 buffer 里面数据就绪后被回调的函数，这里函数里面从 buffer 里面读取服务端发来的数据并打印。
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf message = (ByteBuf) msg;
        byte[] response = new byte[message.readableBytes()];
        message.readBytes(response);

        System.out.println(atomicInteger.getAndIncrement() + ";receive from server:" + new String(response));
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
