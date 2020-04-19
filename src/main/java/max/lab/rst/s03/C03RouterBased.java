package max.lab.rst.s03;

import lombok.RequiredArgsConstructor;
import max.lab.rst.domain.Book;
import max.lab.rst.domain.InMemoryDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Configuration
public class C03RouterBased {
    private static final String PATH_PREFIX = "/routed/";

    private final Validator validator;

    @Bean
    public RouterFunction<ServerResponse> routers() {
        return RouterFunctions.route()
                .POST(PATH_PREFIX + "book", this::create)
                .build();
    }

    private Mono<ServerResponse> create(ServerRequest serverRequest) {
        return C04ReactiveControllerHelper.requestBodyToMono(serverRequest, validator, Book.class)
                .map(InMemoryDataSource::saveBook)
                .flatMap(book -> ServerResponse.created(
                        UriComponentsBuilder.fromHttpRequest(serverRequest.exchange().getRequest())
                            .path(PATH_PREFIX + "book").path(book.getIsbn()).build().toUri())
                        .build());
    }
}
