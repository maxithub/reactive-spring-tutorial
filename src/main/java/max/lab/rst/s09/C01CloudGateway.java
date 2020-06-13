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
                        .route("gw-simple", p -> 
                                p.path("/gw/simple/**")
                                        .filters(f -> f.rewritePath("/gw/simple/(?<path>.*)", "/$\\{path}"))
                                        .uri("http://192.168.31.164:8091/")
                        )
                        .route("gw-lb", p -> 
                                p.path("/gw/lb/**")
                                        .filters(f -> f.rewritePath("/gw/lb/(?<path>.*)", "/$\\{path}"))
                                        .uri("lb://demo-client")
                        )
                        // .route("gw-fb", p -> 
                        //         p.path("/gw/fb/**")
                        //                 .filters(f -> 
                        //                         f.rewritePath("/gw/fb/(?<path>.*)", "/$\\{path}")
                        //                                 .circuitBreaker(c -> c.setName("gw-fb")
                        //                                         .               setFallbackUri("/fb"))
                        //                 )
                        //                 .uri("lb://demo-client")
                        // )
                        .build();
        }

        @Bean("gwRouter")
        public RouterFunction<ServerResponse> router() {
                return RouterFunctions.route().GET("/fb", request -> 
                                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Sorry!!!"))
                        .build();
        }
}
