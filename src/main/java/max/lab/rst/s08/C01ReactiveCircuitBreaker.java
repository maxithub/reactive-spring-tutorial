package max.lab.rst.s08;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class C01ReactiveCircuitBreaker {
//    @Bean
//    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
//        return factory -> {
//            factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
//                    .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
//                    .timeLimiterConfig(TimeLimiterConfig.custom())
//            );
//        }
//    }
}
