package com.example.eurekaconsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableDiscoveryClient(autoRegister = false)
public class EurekaConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaConsumerApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @RestController
    class HelloController {
        @Autowired
        private DiscoveryClient discoveryClient;

        @Autowired
        private RestTemplate restTemplate;

        private String serviceName = "eureka-provider";

        @GetMapping("/info")
        public String info() {
            String serviceInfo = discoveryClient.getServices().toString();
            String instanceInfo = discoveryClient.getInstances(serviceName).stream().map(instance -> "InstanceId: " + instance.getInstanceId() + "<br>ServiceId: " + instance.getServiceId() + "<br>Host: " + instance.getHost() + "<br>Port: " + instance.getPort() + "<br>").collect(Collectors.joining());
            return serviceInfo + "<br><br>" + instanceInfo;
        }

        @GetMapping("/hello")
        public String hello() {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
            ServiceInstance serviceInstance = instances.stream().findAny().orElseThrow(() -> new IllegalStateException("no " + serviceName + " instance available"));
            return restTemplate.getForObject("http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/echo?name=eureka",String.class);
        }
    }
}
