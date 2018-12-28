package com.bob.pn.demo;

import java.nio.ByteBuffer;

/**
 * @author chenbo@guworks.cc
 * @title xxx
 * @date 2018年12月22日
 * @since v1.0.0
 */
public class BufferDemo {

    public static void main(String[] args) {
        //分配一块1024Bytes的堆外内存(直接内存)
        //allocateDirect方法内部调用的是DirectByteBuffer
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        System.out.println("-----" + buffer.capacity());

        //向堆外内存中读写数据
        buffer.putInt(0, 2018);
        System.out.println("=======" + buffer.getInt(0));
    }

}
