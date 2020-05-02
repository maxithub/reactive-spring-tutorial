package max.lab.rst.s04;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import max.lab.rst.domain.Book;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

/**
 * https://medium.com/@filia.aleks/microservice-performance-battle-spring-mvc-vs-webflux-80d39fd81bf0
 * https://medium.com/@kalpads/configuring-timeouts-in-spring-reactive-webclient-4bc5faf56411
 */
public class C01WebClientShowcases {
    public static void main(String[] args) {
        var book = Book.builder().isbn(String.valueOf(System.currentTimeMillis()))
                .category("TEST")
                .title("Book from Webclient")
                .price(BigDecimal.valueOf(23.99))
                .build();

        var webClient = WebClient.create("http://localhost:8080/routed");
        webClient.post().uri("/book")
            .body(Mono.just(book), Book.class)
            .exchange()
            .doOnNext(
                clientResponse -> System.out.println(">>>>>>>> POST RESPONSE STATUS CODE: " + clientResponse.statusCode())
            ).block();

        webClient.get().uri("/book/{isbn}", book.getIsbn())
            .retrieve()
            .bodyToMono(Book.class)
            .doOnNext(aBook -> System.out.println(">>>>>>> GET BOOK: " + aBook))
            .block();

        book.setPrice(BigDecimal.valueOf(39.99));
        webClient.put().uri("/book/{isbn}", book.getIsbn())
            .body(Mono.just(book), Book.class)
            .exchange()
            .doOnNext(
                clientResponse -> System.out.println(">>>>>>>> PUT RESPONSE STATUS CODE: " + clientResponse.statusCode())
            ).block();

        webClient.get().uri("/books")
            .retrieve()
            .bodyToFlux(Book.class)
            .doOnNext(aBook -> System.out.println(">>>>>>> GET BOOKS: " + aBook))
            .blockLast();

        webClient.delete().uri("/book/{isbn}", book.getIsbn())
            .exchange()
            .doOnNext(
                clientResponse -> System.out.println(">>>>>>>> DELETE RESPONSE STATUS CODE: " + clientResponse.statusCode())
            ).block();

        webClient.post().uri("/book")
            .body(Mono.just(book), Book.class)
            .exchange()
            .flatMap(clientResponse -> {
                if (clientResponse.statusCode() != HttpStatus.CREATED) {
                    return clientResponse.createException().flatMap(Mono::error);
                }
                System.out.println(">>>>>>>> POST RESPONSE STATUS CODE: " + clientResponse.statusCode());
                return Mono.just(clientResponse);
            })
            .retryBackoff(3, Duration.ofSeconds(1))
            .block();

        var httpClient = HttpClient.create()
                            .tcpConfiguration(
                                tcpClient -> {
                                    tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 500)
                                        .doOnConnected(
                                            connection -> connection.addHandlerLast(new ReadTimeoutHandler(5, TimeUnit.SECONDS))
                                        );
                                    return tcpClient;
                                }
                            );
        var connector = new ReactorClientHttpConnector(httpClient);                    
        var webClientWithHttpTimeout = WebClient.builder()
                                        .clientConnector(connector)
                                        .build();
    }
}
