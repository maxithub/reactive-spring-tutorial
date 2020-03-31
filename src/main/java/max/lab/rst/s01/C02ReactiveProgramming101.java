package max.lab.rst.s01;

import max.lab.rst.domain.Book;
import max.lab.rst.domain.InMemoryDataSource;
import reactor.core.publisher.Flux;
import java.util.Comparator;

public class C02ReactiveProgramming101 {
    // 返回一个包含每种类别中最贵的书的列表, 响应式编程
    public static Flux<Book> getMostExpensiveBooksByCategoryReactive(Flux<Book> books) {
        return books.collectMultimap(Book::getCategory)
                .flatMapMany(m -> Flux.fromIterable(m.entrySet()))
                .flatMap(e -> Flux.fromIterable(e.getValue())
                                .sort(Comparator.comparing(Book::getPrice).reversed())
                                .next());
    }

    public static void main(String[] args) {
        var pipeline = getMostExpensiveBooksByCategoryReactive(Flux.just(InMemoryDataSource.books));
        pipeline = pipeline.doOnNext(System.out::println);
        System.out.println("什么都不会发生，直到pipeline开始");
        pipeline.subscribe();
    }
}