package com.bob.pn.mq.rocket.boots;

import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.common.RemotingHelper;
import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenbo@guworks.cc
 * @title xxx
 * @date 2018年08月13日
 * @since v1.0.0
 */
@RestController
public class App {

    @Autowired
    private Producer producer;

    @RequestMapping("/home")
    String home() {
        return "Hello World!";
    }

    @RequestMapping("/sendmsg")
    String sendmsg() {
        try {
            Message msg = new Message("TopicTest", "TagA",
                    ("Hello RocketMQ ").getBytes(RemotingHelper.DEFAULT_CHARSET)
            );

            return JSON.toJSONString( producer.send(msg));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "error!";
    }

}
