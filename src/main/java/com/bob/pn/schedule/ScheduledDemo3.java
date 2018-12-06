package com.bob.pn.schedule;

import org.springframework.stereotype.Component;

/**
 * @author chenbo@guworks.cc
 * @title xxx
 * @date 2018年12月01日
 * @since v1.0.0
 */
@Component
public class ScheduledDemo3 {

    //@Scheduled(fixedRate = 50000)
    public void demo() {
        System.out.println("ScheduledDemo1 demo3");
        System.out.println(Integer.valueOf("abcddd"));
    }

}
