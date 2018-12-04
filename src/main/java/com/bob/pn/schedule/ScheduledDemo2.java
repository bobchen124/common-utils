package com.bob.pn.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author chenbo@guworks.cc
 * @title xxx
 * @date 2018年12月01日
 * @since v1.0.0
 */
@Component
public class ScheduledDemo2 {

    @Scheduled(fixedRate = 5000)
    public void demo() {
        System.out.println("ScheduledDemo1 demo2");
    }

}
