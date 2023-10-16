package com.liliang.demo.springboot.jaeger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private static Logger logger = LoggerFactory.getLogger(TestController.class);
    @Autowired
    private ServiceBClient serviceBClient;
    @Autowired
    UserRepository userRepository;

    @RequestMapping("echo")
    public String echo(String msg) {
        logger.info("I'm serviceA: {}", msg);
        userRepository.save(new User(msg));
        String echo = serviceBClient.echo(msg);
        logger.info("serviceB's response:{}", echo);
        return echo;
    }
}

