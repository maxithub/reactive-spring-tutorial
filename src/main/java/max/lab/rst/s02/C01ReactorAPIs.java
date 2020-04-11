package max.lab.rst.s02;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Stream;

public class C01ReactorAPIs {

    private static void createFluxFromExistingData() {
        var justFlux = Flux.just(1, 2, 3, 4, 5, 6);
        subscribeFlux("justFlux", justFlux);
        var arrayFlux = Flux.fromArray(new Integer[] { 1, 2, 3, 4, 5, 6 });
        subscribeFlux("arrayFlux", arrayFlux);
        var iterableFlux = Flux.fromIterable(Arrays.asList(1, 2, 3, 4, 5, 6));
        subscribeFlux("iterableFlux", iterableFlux);
        var streamFlux = Flux.fromStream(Stream.of(1, 2, 3, 4, 5, 6));
        subscribeFlux("streamFlux", streamFlux);
        var rangeFlux = Flux.range(1, 6);
        subscribeFlux("rangeFlux", rangeFlux);
    }

    private static void subscribeFlux(String varName, Flux<?> flux) {
        flux.doOnSubscribe(s -> System.out.print(varName + ": "))
                .doOnNext(e -> System.out.print(e + ", "))
                .doOnComplete(System.out::println)
                .subscribe();
    }

    private static void createMonoAsync() {
        var callableMono = Mono.fromCallable(() -> Thread.currentThread().getName() + " @ " + LocalDateTime.now())
                .publishOn(Schedulers.elastic());
        blockMono("callableMono", callableMono);
        var runnableMono = Mono.fromRunnable(() -> System.out.println(Thread.currentThread().getName() + " @ " + LocalDateTime.now()))
                .publishOn(Schedulers.elastic());
        blockMono("runnableMono", runnableMono);
        var supplierMono = Mono.fromSupplier(() -> Thread.currentThread().getName() + " @ " + LocalDateTime.now())
                .publishOn(Schedulers.elastic());
        blockMono("supplierMono", supplierMono);
    }

    private static void createMonoFromExistingData() {
        var justMono = Mono.just(1);
        blockMono("justMono", justMono);
    }

    private static void blockMono(String varName, Mono<?> mono) {
        mono.doOnSubscribe(s -> System.out.print(varName + ": "))
                .doOnNext(e -> System.out.println(e + ", "))
                .block();
    }

    private static void mapVsFlatMap() {
        var mapFlux = Flux.just(1, 2, 3).map(i -> "id #" + i);
        subscribeFlux("mapFlux", mapFlux);
        var flatMapFlux = Flux.just(1, 2, 3).flatMap(i -> Mono.just("id #" + i));
        subscribeFlux("flatMapFlux", flatMapFlux);
    }

    private static void next() {

    }

    public static void main(String[] args) {
        createFluxFromExistingData();
        createMonoFromExistingData();
        createMonoAsync();
        mapVsFlatMap();
    }

}
