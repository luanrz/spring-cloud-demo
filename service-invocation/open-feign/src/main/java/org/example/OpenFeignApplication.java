package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@EnableFeignClients
@SpringBootApplication
public class OpenFeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenFeignApplication.class, args);
    }

    @RestController
    class HelloController {
        @Autowired
        EchoApi echoApi;

        @GetMapping("/hello")
        public String hello() {
            return echoApi.echo("nacos");
        }
    }

}



