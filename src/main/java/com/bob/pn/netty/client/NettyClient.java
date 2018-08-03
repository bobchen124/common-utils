package com.bob.pn.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author chenbo@guworks.cc
 * @title 客户端程序
 * @date 2018年07月30日
 * @since v1.0.0
 */
public class NettyClient {

    static final String HOST = System.getProperty("host", "127.0.0.1");

    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));

    public static void main(String[] args) throws Exception {
        //1.1 创建Reactor线程池，用来处理io请求，默认线程个数为内核cpu*2
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            //1.2 创建启动类Bootstrap实例，用来设置客户端相关参数
            Bootstrap b = new Bootstrap();
            //1.2.1设置线程池
            //1.2.2指定用于创建客户端NIO通道的Class对象
            //1.2.3设置客户端套接字参数
            //1.2.4设置用户自定义handler
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new NettyClientHandler());
                        }
                    });

            // 1.3启动链接
            ChannelFuture channelFuture = b.connect(HOST, PORT).sync();
            //1.4 同步等待链接断开
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 1.5优雅关闭线程池
            group.shutdownGracefully();
        }
    }

}
