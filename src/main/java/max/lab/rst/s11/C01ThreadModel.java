package max.lab.rst.s11;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.stream.Stream;

public class C01ThreadModel {

    public static void main(String[] args) throws InterruptedException {
        Flux.fromStream(() -> {
            System.out.printf("Producing the names in thread %s%n",
                    Thread.currentThread().getName());
                return Stream.of("jack", "joel", "joseph", "mac", "max", "micheal");
            })
//                .parallel()
                .map(n -> {
                    System.out.printf("Mapping#1 %s in thread %s%n", n,
                            Thread.currentThread().getName());
                    return n.toUpperCase();
                })
//                .publishOn(Schedulers.elastic())
                .map(n -> {
                    System.out.printf("Mapping#2 %s in thread %s%n", n,
                            Thread.currentThread().getName());
                    return n + "_X";
                })
//                .publishOn(Schedulers.elastic())
//                .subscribeOn(Schedulers.elastic())
                .doOnNext(n -> {
                    System.out.printf("doOnNext %s in thread %s%n", n,
                            Thread.currentThread().getName());
                })
//                .subscribeOn(Schedulers.elastic())
                .parallel().runOn(Schedulers.elastic()).subscribe();
        Thread.sleep(5000L);
//                .subscribe();
//                .runOn(Schedulers.parallel()).subscribe();

//                .publishOn(Schedulers.elastic())
//                .blockLast();

    }
}
