package com.liliang.demo.springboot.jaeger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private static Logger logger = LoggerFactory.getLogger(TestController.class);

    @RequestMapping("echo")
    public String echo(String msg) {
        logger.info("I'm serviceB: {}", msg);
        return "B:" + msg;
    }
}

