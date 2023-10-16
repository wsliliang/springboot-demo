
package com.liliang.demo.springboot.jaeger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SpringbootJaegerApplicationB {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootJaegerApplicationB.class, args);
    }

}
