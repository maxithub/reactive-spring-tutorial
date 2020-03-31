package max.lab.rst.domain;

import java.math.BigDecimal;

public final class InMemoryDataSource {
    public static final Book[] books = new Book[] {
        new Book("CS Book #1", BigDecimal.valueOf(19.99D), "CS"),
        new Book("CS Book #2", BigDecimal.valueOf(9.99D), "CS"),
        new Book("CS Book #3", BigDecimal.valueOf(39.99D), "CS"),

        new Book("Children Book #1", BigDecimal.valueOf(20.99D), "CHILDREN"),
        new Book("Children Book #2", BigDecimal.valueOf(25.99D), "CHILDREN"),
        new Book("Children Book #3", BigDecimal.valueOf(24.99D), "CHILDREN"),
        new Book("Children Book #4", BigDecimal.valueOf(10.99D), "CHILDREN"),

        new Book("Novel #1", BigDecimal.valueOf(6.99D), "NOVEL"),
        new Book("Novel #２", BigDecimal.valueOf(12.99D), "NOVEL"),
        new Book("Novel #３", BigDecimal.valueOf(8.99D), "NOVEL"),
        new Book("Novel #４", BigDecimal.valueOf(1.99D), "NOVEL")
    };
}