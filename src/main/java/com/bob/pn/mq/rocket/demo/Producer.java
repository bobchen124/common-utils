package com.bob.pn.mq.rocket.demo;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.common.RemotingHelper;

/**
 * @author chenbo@guworks.cc
 * @title 在 Mq 源码的 example 子项目里面已经有了一个发送消息的 demo
 * @date 2018年08月12日
 * @since v1.0.0
 */
public class Producer {

    public static void main(String[] args) throws MQClientException, InterruptedException {

        //创建生产者实例
        DefaultMQProducer producer = new DefaultMQProducer("jiaduo-producer-group");
        //设置nameserver地址，多个可以使用;分隔
        producer.setNamesrvAddr("127.0.0.1:9876");

        //启动生产者
        producer.start();

        for (int i = 0; i < 1; i++) {
            try {
                //创建消息体,topic为TopicTest，tag为TagA
                Message msg = new Message("TopicTest", "TagA",
                        ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET)
                );

                //发送消息
                SendResult sendResult = producer.send(msg);

                System.out.printf("%s%n", sendResult);
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(1000);
            }
        }

        //关闭
        producer.shutdown();
    }
}
