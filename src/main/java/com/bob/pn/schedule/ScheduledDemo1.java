package com.bob.pn.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author chenbo@guworks.cc
 * @title xxx
 * @date 2018年12月01日
 * @since v1.0.0
 */
@Component
public class ScheduledDemo1 {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledDemo1.class);

    @Scheduled(fixedRate = 5000)
    public void demo() {
        //System.out.println("ScheduledDemo1 demo1");
        logger.info("ScheduledDemo1 demo1");
    }

}
