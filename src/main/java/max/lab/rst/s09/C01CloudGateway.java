package max.lab.rst.s09;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class C01CloudGateway {
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("demo-service", predicateSpec ->
                        predicateSpec.path("/demo/**")
                            .filters(f ->
                                    f.rewritePath("/demo/(?<path>.*)", "/$\\{path}")
                                        .circuitBreaker(c -> c.setName("gw-demo").setFallbackUri("/fallback"))
                            )
                            .uri("lb://demo-client")
                ).build();
    }

    @Bean("gwRouters")
    public RouterFunction<ServerResponse> routers() {
        return RouterFunctions.route().GET("/fallback", request ->
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .bodyValue("this is fallback value"))
                .build();
    }
}
