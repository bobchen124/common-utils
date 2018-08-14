package com.bob.pn.mq.rocket.demo;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.remoting.common.RemotingHelper;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author chenbo@guworks.cc
 * @title 在 Mq 源码的 example 子项目里面已经有了一个接受消息的 demo
 * @date 2018年08月12日
 * @since v1.0.0
 */
public class Consumer {

    public static void main(String[] args) throws InterruptedException, MQClientException {

        //创建消费实例 和 配置ns地址
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("my-consumer-group");
        consumer.setNamesrvAddr("127.0.0.1:9876");

        //消费属性配置
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        //订阅TopicTest topic下所有tag
        consumer.subscribe("TopicTest", "*");
        //注册回调
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for(MessageExt msg:msgs) {
                    String body="";
                    try {
                        body = new String(msg.getBody(),RemotingHelper.DEFAULT_CHARSET);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), body);

                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        /*
         *  Launch the consumer instance.
         */
        consumer.start();

        System.out.printf("Consumer Started.%n");
    }
}
