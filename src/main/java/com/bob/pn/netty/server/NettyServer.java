package com.bob.pn.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author chenbo@guworks.cc
 * @title xxx
 * @date 2018年07月31日
 * @since v1.0.0
 */
final public class NettyServer {

    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));

    public static void main(String[] args) throws Exception {
        //（1.1）创建主从Reactor线程池
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //1.2创建启动类ServerBootstrap实例，用来设置客户端相关参数
            ServerBootstrap b = new ServerBootstrap();
            //1.2.1设置主从线程池组
            //1.2.2指定用于创建客户端NIO通道的Class对象
            //1.2.3设置客户端套接字参数
            //1.2.4设置日志handler
            //1.2.5设置用户自定义handler
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new NettyServerHandler());
                        }
                    });

            //1.3 启动服务器
            ChannelFuture f = b.bind(PORT).sync();
            System.out.println("----Server Started----");

            //1.4 同步等待服务socket关闭
            f.channel().closeFuture().sync();
        } finally {
            // 1.5优雅关闭线程池组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
