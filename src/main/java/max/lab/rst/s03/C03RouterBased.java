package max.lab.rst.s03;

import lombok.RequiredArgsConstructor;
import max.lab.rst.domain.Book;
import max.lab.rst.domain.InMemoryDataSource;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

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
        return C04ReactiveControllerHelper.requestBodyToMono(serverRequest, validator, 
                (t, errors) -> InMemoryDataSource.findBookMonoById(t.getIsbn())
                            .map((book -> {
                                errors.rejectValue("isbn", "already.exists", "Already exists");
                                return Tuples.of(book, errors);
                            }))
//                (t, errors) -> {
//                    Optional<Book> theBook = InMemoryDataSource.findBookById(t.getIsbn());
//                    if (theBook.isPresent()) {
//                        errors.rejectValue("isbn", "already.exists", "Already exists");
//                    }
//                    return Tuples.of(t, errors);
//                }
                , Book.class)
                .map(InMemoryDataSource::saveBook)
                .flatMap(book -> ServerResponse.created(
                    UriComponentsBuilder.fromHttpRequest(serverRequest.exchange().getRequest())
                        .path(PATH_PREFIX + "book").path(book.getIsbn()).build().toUri())
                        .build());

        // return C04ReactiveControllerHelper.requestBodyToMono(serverRequest, validator, Book.class)
        //         .map(InMemoryDataSource::saveBook)
        //         .flatMap(book -> ServerResponse.created(
        //                 UriComponentsBuilder.fromHttpRequest(serverRequest.exchange().getRequest())
        //                     .path(PATH_PREFIX + "book").path(book.getIsbn()).build().toUri())
        //                 .build());
    }
}
