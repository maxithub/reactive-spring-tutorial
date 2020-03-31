package max.lab.rst.s01;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import max.lab.rst.domain.Book;
import max.lab.rst.domain.InMemoryDataSource;

public class C01FunctionalProgramming101 {

    // 返回一个包含每种类别中最贵的书的列表, 非函数式编程
    public static List<Book> getMostExpensiveBooksByCategory() {
        var map = new HashMap<String, Book>();
        for (Book book : InMemoryDataSource.books) {
            var aBook = map.get(book.getCategory());
            if (aBook != null) {
                if (book.getPrice().compareTo(aBook.getPrice()) > 0) {
                    map.put(book.getCategory(), book);
                }
            } else {
                map.put(book.getCategory(), book);
            }
        }
        return (new ArrayList<>(map.values()));
    }

    // 返回一个包含每种类别中最贵的书的列表, 函数式编程
    public static List<Book> getMostExpensiveBooksByCategoryFunctional() {
        return Stream.of(InMemoryDataSource.books)
                    .collect(Collectors.groupingBy(Book::getCategory))
                    // .collect(Collectors.groupingByConcurrent(Book::getCategory)) // 通过修改一行代码就可以编程并行代码
                    .entrySet()
                    .stream()
                    // .parallelStream() // 通过修改一行代码就可以编程并行代码
                    .map(e -> e.getValue()
                                .stream()
                                // .parallelStream() // 通过修改一行代码就可以编程并行代码
                                .sorted(Comparator.comparing(Book::getPrice).reversed())
                                .findFirst()
                                .get())
                    .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        var books1 = getMostExpensiveBooksByCategory();
        books1.stream().forEach(System.out::println);

        System.out.printf("%n%n");

        var books2 = getMostExpensiveBooksByCategoryFunctional();
        books2.stream().forEach(System.out::println);
    }

}