package com.bob.pn.mq.rocket.boots;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author chenbo@guworks.cc
 * @title Consumer 为消费端代码
 * @date 2018年08月13日
 * @since v1.0.0
 */
@Configuration
//@ConditionalOnProperty(prefix = "spring.rocketmq", value = { "nameServer" })
public class Consumer implements InitializingBean {

    private DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("my-consumer-group");

    public String getNameSrvAdr() {
        return nameSrvAdr;
    }

    public void setNameSrvAdr(String nameSrvAdr) {
        this.nameSrvAdr = nameSrvAdr;
    }

    @Value("${spring.rocketmq.nameServer}")
    private String nameSrvAdr;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 创建消费实例 和 配置ns地址
        consumer.setNamesrvAddr(nameSrvAdr);
        // 订阅TopicTest topic下所有tag
        consumer.subscribe("TopicTest", "*");
        // 注册listen
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgs) {
                    String body = "";
                    try {
                        body = new String(msg.getBody(), RemotingHelper.DEFAULT_CHARSET);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), body);

                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        /*
         * Launch the consumer instance.
         */
        consumer.start();
        System.out.printf("Consumer Started.%n");
    }

}
