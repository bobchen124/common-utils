package com.bob.pn.netty.reactor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author chenbo@guworks.cc
 * @title 创建 Reactor
 * @date 2018年12月25日
 * @since v1.0.0
 */
public class Reactor implements Runnable {

    public final Selector selector;

    public final ServerSocketChannel serverSocketChannel;

    public Reactor(int port) throws IOException {
        // 创建选择器
        selector = Selector.open();
        // 打开监听信道
        serverSocketChannel = ServerSocketChannel.open();
        // 与本地端口绑定
        InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost(), port);
        System.out.println(InetAddress.getLocalHost());

        serverSocketChannel.socket().bind(inetSocketAddress);
        // 设置为非阻塞模式,只有非阻塞信道才可以注册选择器,否则异步IO就无法工作
        serverSocketChannel.configureBlocking(false);

        // 向selector注册该channel
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        // 利用selectionKey的attache功能绑定Acceptor
        selectionKey.attach(new Acceptor(this));
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                System.out.println("等待....");
                // 阻塞等待某信道就绪
                selector.select();
                // 取得已就绪事件的key集合，遍历每一个注册的通道(本文作为举例，只有一个通道)
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                //Selector如果发现channel有OP_ACCEPT或READ事件发生，下列遍历就会进行。
                while (it.hasNext()) {
                    SelectionKey selectionKey = it.next();
                    dispatch(selectionKey);

                    it.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void dispatch(SelectionKey key) {
        Runnable r = (Runnable)key.attachment();

        if (r != null) {
            //调度事件对应的处理流程
            r.run();
        }
    }

    public static void main(String[] args) {
        try {
            Reactor reactor = new Reactor(12345);
            reactor.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
