package max.lab.rst.s05;

import max.lab.rst.domain.Book;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * https://medium.com/@filia.aleks/microservice-performance-battle-spring-mvc-vs-webflux-80d39fd81bf0
 * https://medium.com/@kalpads/configuring-timeouts-in-spring-reactive-webclient-4bc5faf56411
 */
public class C04WebClientR2dbc {
    public static void main(String[] args) {
        var book = Book.builder().isbn(String.valueOf(System.currentTimeMillis()))
                .category("TEST")
                .title("Book from Webclient")
                .price(BigDecimal.valueOf(23.99))
                .build();

        var book2 = Book.builder().isbn(String.valueOf(System.currentTimeMillis()))
                .category("TEST")
                .title("Book from Webclient #2")
                .price(BigDecimal.valueOf(55.99))
                .build();      
                  

        var webClient = WebClient.create("http://localhost:8080/routed-r2dbc");
        webClient.post().uri("/books")
            .body(Flux.just(book, book2), Book.class)
            .exchange()
            .doOnNext(
                clientResponse -> System.out.println(">>>>>>>> POST RESPONSE STATUS CODE: " + clientResponse.statusCode())
            ).block();

        // webClient.get().uri("/book/{isbn}", book.getIsbn())
        //     .retrieve()
        //     .bodyToMono(Book.class)
        //     .doOnNext(aBook -> System.out.println(">>>>>>> GET BOOK: " + aBook))
        //     .block();

        // book.setPrice(BigDecimal.valueOf(39.99));
        // webClient.put().uri("/book/{isbn}", book.getIsbn())
        //     .body(Mono.just(book), Book.class)
        //     .exchange()
        //     .doOnNext(
        //         clientResponse -> System.out.println(">>>>>>>> PUT RESPONSE STATUS CODE: " + clientResponse.statusCode())
        //     ).block();

        webClient.get().uri("/books")
            .retrieve()
            .bodyToFlux(Book.class)
            .doOnNext(aBook -> System.out.println(">>>>>>> GET BOOKS: " + aBook))
            .blockLast();

        // webClient.delete().uri("/book/{isbn}", book.getIsbn())
        //     .exchange()
        //     .doOnNext(
        //         clientResponse -> System.out.println(">>>>>>>> DELETE RESPONSE STATUS CODE: " + clientResponse.statusCode())
        //     ).block();
    }
}
