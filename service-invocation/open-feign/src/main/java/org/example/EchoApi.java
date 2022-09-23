package org.example;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "eureka-provider") // url = "http://localhost:8081"
interface EchoApi {
    @GetMapping("/echo")
    String echo(@RequestParam("name") String name);
}
