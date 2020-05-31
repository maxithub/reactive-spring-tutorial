package max.lab.rst.s08;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Configuration
public class C01ReactiveCircuitBreaker {
    private static String PROP_NAME = "circuitBreaker.%s.%s";

    // https://resilience4j.readme.io/docs/circuitbreaker
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer(Environment env) {
        return factory -> factory.configureDefault(id -> {
            log.info(">>>>>>>>>>> R4J_ID: {}", id);
            return new Resilience4JConfigBuilder(id)
                            .circuitBreakerConfig(
                                CircuitBreakerConfig.custom()
                                    .failureRateThreshold(getCircuitBreakerProperty(env, id,"failureRateThreshold", Float.class, 50F))
                                    .minimumNumberOfCalls(getCircuitBreakerProperty(env, id,"minimumNumberOfCalls", Integer.class, 100))
                                    .slidingWindowSize(getCircuitBreakerProperty(env, id,"slidingWindowSize", Integer.class, 100))
                                    .waitDurationInOpenState(getCircuitBreakerProperty(env, id,"waitDurationInOpenState", Duration.class, Duration.ofSeconds(60L)))
                                    .build()
                            ).timeLimiterConfig(
                                TimeLimiterConfig.custom()
                                    .cancelRunningFuture(getCircuitBreakerProperty(env, id,"cancelRunningFuture", Boolean.class, Boolean.TRUE))
                                    .timeoutDuration(getCircuitBreakerProperty(env, id,"timeoutDuration", Duration.class, Duration.ofSeconds(5L)))
                                    .build()
                            ).build();
                }
        );
    }

    private <T> T getCircuitBreakerProperty(Environment env, String id, String name, Class<T> clz, T defaultValue) {
        var propName = String.format(PROP_NAME, id, name);
        var defaultPropName = String.format(PROP_NAME, "default", name);
        var value = env.getProperty(propName, clz, env.getProperty(defaultPropName, clz, defaultValue));
        log.info(">>>>>>>>>>>>> {} or {}: {}", propName, defaultPropName, value);
        return value;
    }

    @Bean("r4jRouter")
    public RouterFunction<ServerResponse> routers(WebClient.Builder webClientBuilder,
                                                  ReactiveCircuitBreakerFactory cbFactory) {
        return RouterFunctions.route()
                .GET("/r4j/demo", request -> {
                    var hello = webClientBuilder.build().get()
                            .uri("hello").retrieve()
                            .bodyToMono(String.class);
                    var r4jHello = cbFactory.create("hello")
                            .run(hello, t -> {
                                log.error("Failed to call hello endpoint: {}", t.getMessage());
                                return Mono.just("sorry, ;-(");
                            });
                    return ServerResponse.ok().body(r4jHello, String.class);
                })
                .build();
    }

}
