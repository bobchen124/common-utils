package com.bob.pn.mq.rocket.boots;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenbo@guworks.cc
 * @title 基于 SpringBoot 搭建一个生产与消费的 demo
 * @date 2018年08月13日
 * @since v1.0.0
 */
@Configuration
//@ConditionalOnProperty(prefix = "spring.rocketmq", value = { "nameServer" })
public class Producer implements InitializingBean {

    public Producer() {
    }

    public String getNameSrvAdr() {
        return nameSrvAdr;
    }

    public void setNameSrvAdr(String nameSrvAdr) {
        this.nameSrvAdr = nameSrvAdr;
    }

    @Value("${spring.rocketmq.nameServer}")
    private String nameSrvAdr;

    private DefaultMQProducer producer = new DefaultMQProducer("jiaduo-producer-group");

    public SendResult send(Message msg) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return producer.send(msg);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        producer.setNamesrvAddr(nameSrvAdr);
        producer.start();
        System.out.printf("Producer Started.%n");
    }

}
