package max.lab.rst.s07;

import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
@EnableDiscoveryClient
public class C01ReactiveLoadBalancer {

    @Bean
    public WebClient.Builder loadBalancedWebClientBuilder(ReactorLoadBalancerExchangeFilterFunction filter) {
        return WebClient.builder().filter(filter).baseUrl("http://demo-client");
    }

//    @LoadBalanced
//    @Bean
//    public WebClient.Builder loadBalancedWebClientBuilder() {
//        return WebClient.builder().baseUrl("http://demo-client");
//    }

    @Bean
    public CommandLineRunner commandLineRunner(WebClient.Builder builder) {
        return (args -> builder.build().get().uri("hello").retrieve().bodyToMono(String.class)
                .onErrorReturn("Failed to call hello endpoint")
                .doOnNext(s -> System.out.println(">>>>>>>>>>>>>> Server Response: " + s))
                .delayElement(Duration.ofMillis(500))
                .repeat(3)
                .subscribe()
        );
    }

}
