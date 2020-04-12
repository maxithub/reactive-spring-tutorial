package max.lab.rst.s02;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Stream;

/***
 * Reactor的API非常丰富和功能齐全，大家在coding的时候要搜索和查阅资料，尽量用这些API来实现程序的流转/返回/异常处理等。
 */
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

    private static void createFluxProgrammatically() {
        var generateFlux = Flux.generate(() -> 1, (state, sink) -> {
            sink.next("message #" + state);
            if (state == 10) {
                sink.complete();
            }
            return state + 1;
        });
        subscribeFlux("generateFlux", generateFlux);
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

    private static void useThenForFlow() {
        var thenMono = Mono.just("world")
                .map(n -> "hello " + n)
                .doOnNext(System.out::println)
                .thenReturn("do something else");
        blockMono("thenMono",thenMono);
    }

    private static void monoFluxInterchange() {
        var monoFlux = Mono.just(1).flux();
        subscribeFlux("monoFlux", monoFlux);
        var fluxMono = Flux.just(1, 2, 3).collectList();
        blockMono("fluxMono", fluxMono);
    }

    private static void zipMonoOrFlux() {
        var userId = "max";
        var monoProfile = Mono.just(userId + "的详细信息");
        var monoLatestOrder = Mono.just(userId + "的最新订单");
        var monoLatestReview = Mono.just(userId + "的最新评论");
        var zipMono = Mono.zip(monoProfile, monoLatestOrder, monoLatestReview)
                .doOnNext(t -> System.out.printf("%s的主页，%s, %s, %s%n", userId, t.getT1(), t.getT2(), t.getT3()));
        blockMono("zipMono", zipMono);
    }

    private static void errorHandling() {
        var throwExceptionFlux = Flux.range(1, 10).map(i -> {
            if (i > 5) {
                throw (new RuntimeException("Something wrong"));
            }
            return "item #" + i;
        });
        subscribeFlux("throwExceptionFlux", throwExceptionFlux);

        var errorFlux = Flux.range(1, 10).flatMap(i -> {
            if (i > 5) {
                return Mono.error(new RuntimeException("Something wrong"));
            }
            return Mono.just("item #" + i);
        });
        subscribeFlux("errorFlux", errorFlux);
    }

    public static void main(String[] args) {
        createFluxFromExistingData();
        createMonoFromExistingData();
        createFluxProgrammatically();
        createMonoAsync();
        mapVsFlatMap();
        monoFluxInterchange();
        useThenForFlow();
        zipMonoOrFlux();
        errorHandling();
    }

}
