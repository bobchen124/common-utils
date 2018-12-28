package com.bob.pn.netty.reactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author chenbo@guworks.cc
 * @title 创建 Acceptor
 * @date 2018年12月25日
 * @since v1.0.0
 */
public class Acceptor implements Runnable {

    private Reactor reactor;

    public Acceptor(Reactor reactor) {
        this.reactor = reactor;
    }

    @Override
    public void run() {
        try {
            // 接受client连接请求
            SocketChannel socketChannel = reactor.serverSocketChannel.accept();
            System.out.println(socketChannel.socket().getRemoteSocketAddress().toString() + " is connected.");

            if (socketChannel != null) {
                socketChannel.configureBlocking(false);
                // SocketChannel向selector注册一个OP_READ事件，然后返回该通道的key
                SelectionKey selectionKey = socketChannel.register(reactor.selector, SelectionKey.OP_READ);
                // 使一个阻塞住的selector操作立即返回
                reactor.selector.wakeup();
                // 通过key为新的通道绑定一个附加的TCPHandler对象
                selectionKey.attach(new TCPHandler(selectionKey, socketChannel));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
