package max.lab.rst.s4;

import max.lab.rst.domain.Book;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

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

        var webclient = WebClient.create("http://localhost:8080/routed/");
        // HttpClient httpClient = HttpClient.create()
        //         .tcpConfiguration(tcpClient -> {
        //             tcpClient = tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 500);
        //             tcpClient = tcpClient.doOnConnected(
        //                 conn -> conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
        //             );
        //             return tcpClient;
        //         });
        // ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        // var webClientWithTimeout = WebClient.builder().clientConnector(connector).build();
        
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
