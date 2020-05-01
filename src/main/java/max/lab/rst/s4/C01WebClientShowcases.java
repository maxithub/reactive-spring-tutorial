package max.lab.rst.s4;

import max.lab.rst.domain.Book;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;

public class C01WebClientShowcases {
    public static void main(String[] args) {
        var webclient = WebClient.create("http://localhost:8080/routed/");
        var book = Book.builder().isbn(String.valueOf(System.currentTimeMillis()))
                .category("TEST")
                .title("Book from Webclient")
                .price(BigDecimal.valueOf(23.99))
                .build();

//        webclient.post().uri("/book")
//                .body(Mono.just(book), Book.class)
//                .exchange()
//                .doOnNext(clientResponse -> System.out.println(">>>>>>> POST RESPONSE: " + clientResponse.statusCode()))
//                .doOnError(e -> System.err.println("<<<<<< Failed to post book" + e.getMessage()))
//                .block();
//
//        webclient.get().uri("/book/{isbn}", book.getIsbn())
//                .retrieve()
//                .bodyToMono(Book.class)
//                .doOnNext(b -> System.out.println(">>>>>>> GET RESPONSE: " + b))
//                .doOnError(e -> System.err.println("<<<<<< Failed to get book" + e.getMessage()))
//                .block();
//
//        book.setPrice(BigDecimal.valueOf(49.99));
//        webclient.put().uri("/book/{isbn}", book.getIsbn())
//                .body(Mono.just(book), Book.class)
//                .exchange()
//                .doOnNext(clientResponse -> System.out.println(">>>>>>> PUT RESPONSE: " + clientResponse.statusCode()))
//                .doOnError(e -> System.err.println("<<<<<< Failed to get book" + e.getMessage()))
//                .block();
//
//        webclient.get().uri("/books")
//                .retrieve()
//                .bodyToFlux(Book.class)
//                .doOnNext(b -> System.out.println(">>>>>>> GET RESPONSE: " + b))
//                .doOnError(e -> System.err.println("<<<<<< Failed to get book" + e.getMessage()))
//                .blockLast();

// https://medium.com/@kalpads/configuring-timeouts-in-spring-reactive-webclient-4bc5faf56411
                webclient.post().uri("/book")
                        .body(Mono.just(book), Book.class)
                        .exchange()
                        .flatMap(clientResponse -> {
                            if (clientResponse.statusCode() != HttpStatus.CREATED) {
                                return clientResponse.createException().flatMap(Mono::error);
                            }
                            System.out.println(">>>>>>> Book created!");
                            return Mono.just(clientResponse);
                        })
                        .retryBackoff(3, Duration.ofSeconds(1))
                        .doOnError(e -> System.err.println(">>>>>>> Failed to post book" + e.getMessage()))
                        .block();

    }
}
