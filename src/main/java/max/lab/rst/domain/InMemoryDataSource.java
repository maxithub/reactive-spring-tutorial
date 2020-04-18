package max.lab.rst.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryDataSource {
    public static final Book[] books = new Book[] {
        new Book("000001", "CS Book #1", BigDecimal.valueOf(19.99D), "CS"),
        new Book("000002", "CS Book #2", BigDecimal.valueOf(9.99D), "CS"),
        new Book("000003", "CS Book #3", BigDecimal.valueOf(39.99D), "CS"),

        new Book("000004", "Children Book #1", BigDecimal.valueOf(20.99D), "CHILDREN"),
        new Book("000005", "Children Book #2", BigDecimal.valueOf(25.99D), "CHILDREN"),
        new Book("000006", "Children Book #3", BigDecimal.valueOf(24.99D), "CHILDREN"),
        new Book("000007", "Children Book #4", BigDecimal.valueOf(10.99D), "CHILDREN"),

        new Book("000008", "Novel #1", BigDecimal.valueOf(6.99D), "NOVEL"),
        new Book("000009", "Novel #２", BigDecimal.valueOf(12.99D), "NOVEL"),
        new Book("000010", "Novel #３", BigDecimal.valueOf(8.99D), "NOVEL"),
        new Book("000011", "Novel #４", BigDecimal.valueOf(1.99D), "NOVEL")
    };

    private static final Map<String, Book> booksMap = new ConcurrentHashMap<>();

    public static Book saveBook(Book book) {
        booksMap.put(book.getIsbn(), book);
        return book;
    }

    public static Optional<Book> findBookById(String isbn) {
        return Optional.ofNullable(booksMap.get(isbn));
    }

    public static Collection<Book> findAllBooks() {
        return booksMap.values();
    }

    public static void removeBook(Book book) {
        booksMap.remove(book.getIsbn());
    }
}