package com.bob.pn.netty.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author chenbo@guworks.cc
 * @title 客户端程序
 * @date 2018年07月29日
 * @since v1.0.0
 */
public class NioClient {

    /**
     *(1)创建发送和接受缓冲区
     */
    private static ByteBuffer sendbuffer = ByteBuffer.allocate(1024);

    private static ByteBuffer receivebuffer = ByteBuffer.allocate(1024);

    public static void main(String[] args) throws IOException {
        // (2) 获取一个客户端socket通道
        SocketChannel socketChannel = SocketChannel.open();
        // （3）设置socket为非阻塞方式
        socketChannel.configureBlocking(false);
        // （4）获取一个选择器
        Selector selector = Selector.open();
        // （5）注册客户端socket到选择器
        SelectionKey selectionKey = socketChannel.register(selector, 0);
        // （6）发起连接
        boolean isConn = socketChannel.connect(new InetSocketAddress("127.0.0.1", 7001));
        // (7)如果连接没有马上建立成功，则设置对链接完成事件感兴趣
        if (!isConn) {
            selectionKey.interestOps(SelectionKey.OP_CONNECT);
        }

        int num = 0;
        while (true) {
            // (8) 选择已经就绪的网络IO操作，阻塞方法
            int selectCount = selector.select();
            System.out.println(num + ",selectCount:" + selectCount);
            // （9）返回已经就绪的通道的事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            //(10)处理所有就绪事件
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            SocketChannel client;

            while (iterator.hasNext()) {
                //(10.1)获取一个事件，并从集合移除
                selectionKey = iterator.next();
                iterator.remove();

                //(10.2)获取事件类型
                int readyOps = selectionKey.readyOps();
                //(10.3)判断是否是OP_CONNECT事件
                if ((readyOps & SelectionKey.OP_CONNECT) != 0) {
                    //（10.3.1）等待客户端socket完成与服务器端的链接
                    client = (SocketChannel)selectionKey.channel();
                    if (!client.finishConnect()) {
                        throw new Error();
                    }

                    System.out.println("--- client already connected----");
                    //(10.3.2)设置要发送给服务端的数据
                    sendbuffer.clear();
                    sendbuffer.put("hello server,im a client".getBytes());
                    sendbuffer.flip();

                    //(10.3.3)写入输入。
                    client.write(sendbuffer);
                    //(10.3.4)设置感兴趣事件，读事件
                    selectionKey.interestOps(SelectionKey.OP_READ);
                }
                //(10.4)判断是否是OP_READ事件
                else if ((readyOps & SelectionKey.OP_READ) != 0) {
                    client = (SocketChannel)selectionKey.channel();
                    //（10.4.1）读取数据并打印
                    receivebuffer.clear();

                    int count = client.read(receivebuffer);
                    if (count > 0) {
                        String temp = new String(receivebuffer.array(), 0, count);
                        System.out.println(num++ + ",receive from server:" + temp);
                    }
                }
            }
        }
    }

}
