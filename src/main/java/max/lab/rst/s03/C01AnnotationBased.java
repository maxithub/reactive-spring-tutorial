package max.lab.rst.s03;

import max.lab.rst.domain.Book;
import max.lab.rst.domain.InMemoryDataSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Optional;

@RequestMapping("/annotated")
@RestController
public class C01AnnotationBased {
    @GetMapping("books")
    public Flux<Book> findAll() {
        return Flux.fromIterable(InMemoryDataSource.findAllBooks());
    }

    @PostMapping("book")
    public Mono<ResponseEntity<?>> create(@Valid @RequestBody Book book,
                                          UriComponentsBuilder ucb) {
        InMemoryDataSource.saveBook(book);
        return Mono.just(ResponseEntity.created(
                ucb.path("/annotated/book/").path(book.getIsbn()).build().toUri()
        ).build());
    }

//    @GetMapping("book/{isbn}")
//    public Mono<Book> find(@PathVariable String isbn) {
//        return Mono.justOrEmpty(InMemoryDataSource.findBookById(isbn));
//    }

    @GetMapping("book/{isbn}")
    public Mono<ResponseEntity<Book>> find(@PathVariable String isbn) {
        Optional<Book> book = InMemoryDataSource.findBookById(isbn);
        if (!book.isPresent()) {
            return Mono.just(ResponseEntity.notFound().build());
        }
        return Mono.just(ResponseEntity.ok(book.get()));
    }

    @PutMapping("book/{isbn}")
    public Mono<ResponseEntity<?>> update(@PathVariable String isbn,
                                          @RequestBody Book book) {
        Optional<Book> theBook = InMemoryDataSource.findBookById(isbn);
        if (!theBook.isPresent()) {
            return Mono.just(ResponseEntity.notFound().build());
        }
        InMemoryDataSource.saveBook(book);
        return Mono.just(ResponseEntity.ok().build());
    }

    @DeleteMapping("book/{isbn}")
    public Mono<ResponseEntity<?>> remove(@PathVariable String isbn) {
        Optional<Book> book = InMemoryDataSource.findBookById(isbn);
        if (!book.isPresent()) {
            return Mono.just(ResponseEntity.notFound().build());
        }

        InMemoryDataSource.removeBook(book.get());
        return Mono.just(ResponseEntity.ok().build());
    }
}
