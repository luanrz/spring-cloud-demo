package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class NacosConfigController {

    // 注入Nacos配置方式一：Value
    @Value("${switch.fun1:false}")
    private boolean fun1Switch;

    // 注入Nacos配置方式二：ConfigurationProperties
    @Autowired
    private FunSwitchProperties properties;

    @GetMapping("/value")
    public String value() {
        return dealFun1(fun1Switch);
    }

    @GetMapping("/properties")
    public String properties() {
        return dealFun1(properties.getFun1());
    }

    private String dealFun1(boolean theSwitch) {
        if (theSwitch) {
            return "do fun1";
        } else {
            return "do nothing";
        }
    }

    @ConfigurationProperties(prefix = "switch")
    @Component
    public static class FunSwitchProperties {
        private boolean fun1;

        public boolean getFun1() {
            return fun1;
        }

        public void setFun1(boolean fun1) {
            this.fun1 = fun1;
        }
    }
}
