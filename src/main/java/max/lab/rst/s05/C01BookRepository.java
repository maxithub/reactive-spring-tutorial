package max.lab.rst.s05;

import lombok.RequiredArgsConstructor;
import max.lab.rst.domain.Book;
import max.lab.rst.domain.BookQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.relational.core.query.Criteria.where;

@RequiredArgsConstructor
@Repository
public class C01BookRepository {
    private final DatabaseClient dbClient;

    public Mono<Void> insert(Book book) {
        return dbClient.insert().into(Book.class)
                .using(book)
                .then();
    }

    public Mono<Void> update(Book book) {
        return dbClient.update().table(Book.class)
                .using(book)
                .then();
    }

    public Mono<Book> findById(String isbn) {
        return dbClient.execute("select * from book where isbn = :isbn")
                .bind("isbn", isbn)
                .as(Book.class)
                .fetch()
                .one();
    }

    public Flux<Book> findAll() {
        return dbClient.select().from(Book.class).fetch().all();
    }

    public Mono<Void> delete(String isbn) {
        return dbClient.delete().from(Book.class)
                .matching(where("isbn").is(isbn))
                .then();
    }

    public Flux<Book> findBooksByQuery(BookQuery bookQuery) {
        Criteria criteria = Criteria.empty();
        if (!StringUtils.isEmpty(bookQuery.getTitle())) {
            criteria = criteria.and(where("title").like(bookQuery.getTitle()));
        }
        if (bookQuery.getMinPrice() != null) {
            criteria = criteria.and(where("price").greaterThanOrEquals(bookQuery.getMinPrice()));
        }
        if (bookQuery.getMaxPrice() != null) {
            criteria = criteria.and(where("price").lessThanOrEquals(bookQuery.getMinPrice()));
        }
        Pageable pageable = PageRequest.of(bookQuery.getPage(), bookQuery.getSize());
        return dbClient.select().from(Book.class)
                .matching(criteria)
                .page(pageable)
                .fetch()
                .all();
    }
}
