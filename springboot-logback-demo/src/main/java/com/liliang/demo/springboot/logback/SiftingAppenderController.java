package com.liliang.demo.springboot.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SiftingAppenderController {
    private static final Logger logger = LoggerFactory.getLogger(SiftingAppenderController.class);

    @RequestMapping("log")
    public String testLog(String env) {
        MDC.put("env", "env");
        logger.info("测试输出,env:{}", env);
        return "success";
    }
}

