package com.liliang.demo.springboot.jaeger;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "serviceB", url = "http://localhost:8082")
public interface ServiceBClient {

    @GetMapping("/echo")
    String echo(@RequestParam("msg") String msg);

}