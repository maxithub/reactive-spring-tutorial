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
                .route("demo-http", p ->
                        p.path("/gw/simple/**")

                                .filters(f ->
                                        f.rewritePath("/gw/simple/(?<path>.*)", "/$\\{path}")
                                )
                                .uri("http://localhost:8091/")
                )
                .route("demo-rb", p ->
                        p.path("/gw/rb/**")
                            .filters(f ->
                                    f.rewritePath("/gw/rb/(?<path>.*)", "/$\\{path}")
                            )
                            .uri("lb://demo-client")
                )
                .route("demo-rb-fb", p ->
                        p.path("/gw/rb-fb/**")
                                .filters(f ->
                                        f.rewritePath("/gw/rb-fb/(?<path>.*)", "/$\\{path}")
                                                .circuitBreaker(c -> c.setName("gw-rb-fb").setFallbackUri("/fallback"))
                                )
                                .uri("lb://demo-client")
                )
                .build();
    }

    @Bean("gwRouters")
    public RouterFunction<ServerResponse> routers() {
        return RouterFunctions.route().GET("/fallback", request ->
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .bodyValue("this is fallback value"))
                .build();
    }
}
