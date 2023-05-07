package org.example;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

@Configuration
public class LoadBalancerConfiguration {
    private final String serviceName = "eureka-provider";

    /* 引入LoadBalanced */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate () {
        return new RestTemplate();
    }

    /* 可选项：引入LoadBalancerClient自定义配置 */
    @Configuration
    @LoadBalancerClient(value = serviceName, configuration = MyLoadBalancerClientConfiguration.class)
    class LoadBalanceConfiguration {
    }

    public class MyLoadBalancerClientConfiguration {
        /**
         * {@link org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientConfiguration}
         */
        @Bean
        public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(Environment environment, LoadBalancerClientFactory loadBalancerClientFactory) {
            String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
            return new RandomLoadBalancer(loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class), name);
        }
    }

    public static class RandomLoadBalancer implements ReactorServiceInstanceLoadBalancer {
        private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
        private final String serviceId;
        private final Random random;

        public RandomLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId) {
            this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
            this.serviceId = serviceId;
            this.random = new Random();
        }

        @Override
        public Mono<Response<ServiceInstance>> choose(Request request) {
            ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);
            return supplier.get().next().map(this::getInstanceResponse);
        }

        private Response<ServiceInstance> getInstanceResponse(
                List<ServiceInstance> instances) {
            if (instances.isEmpty()) {
                return new EmptyResponse();
            }
            ServiceInstance serviceInstance = instances.get(random.nextInt(instances.size()));
            return new DefaultResponse(serviceInstance);
        }
    }

}
