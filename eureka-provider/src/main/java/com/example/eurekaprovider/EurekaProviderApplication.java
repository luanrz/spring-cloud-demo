package com.example.eurekaprovider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class EurekaProviderApplication {

    @Autowired
    Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(EurekaProviderApplication.class, args);
    }

    @RestController
    class EchoController {
        @GetMapping("/echo")
        public String echo(HttpServletRequest request) {
            return "echo:" + request.getParameter("name");
        }

        @GetMapping("/serverIpPort")
        public String info() throws UnknownHostException {
            return InetAddress.getLocalHost().getHostAddress() + ":" + environment.getProperty("server.port");
        }
    }

}
