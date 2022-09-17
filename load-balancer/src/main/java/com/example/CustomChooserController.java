package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.ServiceInstanceChooser;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;

@RestController
@Order(1)
class CustomChooserController {
    private final String serviceName = "eureka-provider";

    @Autowired
    private RandomServiceInstanceChooser randomServiceInstanceChooser;
    private final RestTemplate restTemplate = new RestTemplate();


    @GetMapping("/customChooser")
    public String customChooser() {
        ServiceInstance serviceInstance = randomServiceInstanceChooser.choose(serviceName);
        return restTemplate.getForObject(String.format("http://%s:%s/serverIpPort", serviceInstance.getHost(), serviceInstance.getPort()), String.class);
    }

    @Component
    static class RandomServiceInstanceChooser implements ServiceInstanceChooser {
        private final DiscoveryClient discoveryClient;
        private final Random random;

        public RandomServiceInstanceChooser(DiscoveryClient discoveryClient) {
            this.discoveryClient = discoveryClient;
            this.random = new Random();
        }

        @Override
        public ServiceInstance choose(String serviceId) {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
            return instances.get(random.nextInt(instances.size()));
        }
    }

}
