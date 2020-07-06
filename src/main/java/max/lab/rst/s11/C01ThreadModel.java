package max.lab.rst.s11;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.stream.Stream;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class C01ThreadModel {

    public static void main(String[] args) throws Exception {
    	Flux.fromStream(() -> {
    		System.out.printf("Producing stream in thread %s%n", 
    				Thread.currentThread().getName());
    		return Stream.of("max", "jack", "kate", "bill", "joe");
    	})
    	.parallel()
    	.runOn(Schedulers.parallel())
    	.map(n -> {
    		System.out.printf("#1 Mapping %s in thread %s%n", n,
    				Thread.currentThread().getName());
    		return n.toUpperCase();
    	})
//    	.publishOn(Schedulers.elastic())
    	.map(n -> {
    		System.out.printf("#2 Mapping %s in thread %s%n", n,
    				Thread.currentThread().getName());
    		return n + "_X";
    	})
    	.doOnNext(n -> {
    		System.out.printf("doOnNext %s in thread %s%n", n,
    				Thread.currentThread().getName());
    	}).subscribe();
//    	.subscribeOn(Schedulers.elastic())
//    	.subscribe();
    	
//    	Flux.fromStream(() -> {
//    		System.out.printf("Producing stream in thread %s%n", 
//    				Thread.currentThread().getName());
//    		return Stream.of(1,2,3,4,5);
//    	}).doOnNext(n -> {
//    		System.out.printf("doOnNext %d in thread %s%n", n,
//    				Thread.currentThread().getName());
//    	})
//    	.subscribe();
    	
    	TimeUnit.SECONDS.sleep(5L);
    }
}
