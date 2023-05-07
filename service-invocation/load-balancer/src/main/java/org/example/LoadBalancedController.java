package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Order(2)
class LoadBalancedController {
    private final String serviceName = "eureka-provider";

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/loadBalanced")
    public String loadBalanced() {
        return restTemplate.getForObject(String.format("http://%s/info", serviceName), String.class);
    }

}
