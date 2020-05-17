package max.lab.rst.s06;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class C01SecurityConfigurer {
    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        var admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("secret")
                .roles("ADMIN")
                .build();
        var guest = User.withDefaultPasswordEncoder()
                .username("guest")
                .password("secret")
                .roles("GUEST")
                .build();
        return (new MapReactiveUserDetailsService(admin, guest));
    }

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange()
                .pathMatchers(HttpMethod.PUT, "/routed/**")
                .hasAnyRole("ADMIN")
                .pathMatchers(HttpMethod.POST, "/routed/**")
                .hasAnyRole("ADMIN")
                .pathMatchers("/routed/**")
                .authenticated()
                .pathMatchers("/routed-r2dbc/**").access(((mono, object) -> mono.map(auth -> {
                    var httpMethod = object.getExchange().getRequest().getMethod();
                    var granted = false;
                    if (httpMethod == HttpMethod.PUT
                            || httpMethod == HttpMethod.POST
                            || httpMethod == HttpMethod.DELETE) {
                        granted = auth.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .anyMatch("ROLE_ADMIN"::equals);
                    } else {
                        granted = auth.isAuthenticated();
                    }
                    return (new AuthorizationDecision(granted));
                }).switchIfEmpty(Mono.justOrEmpty(new AuthorizationDecision(false)))))
                .anyExchange()
                .permitAll()
                .and().httpBasic()
                .and().csrf().disable()
                .build();
    }
}
