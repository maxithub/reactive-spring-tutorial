package max.lab.rst.s10;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class C01SimpleReactiveTesting {
	@Test
	public void testSimpleReactiveCode() {
		var flux = Flux.just("hello", "world");
		StepVerifier.create(flux)
			.expectNext("hello")
			.expectNext("world")
			.expectComplete()
			.verify();
	}
	
	@Test
	public void testWithMockTimer() {
//		var flux = Flux.just("hello", "world")
//				.delayElements(Duration.ofSeconds(3L));
		StepVerifier.withVirtualTime(() -> Flux.just("hello", "world")
				.delayElements(Duration.ofSeconds(3L)))
		.expectSubscription()
		.expectNoEvent(Duration.ofSeconds(3L))
		.expectNext("hello")
		.expectNoEvent(Duration.ofSeconds(3L))
		.expectNext("world")
		.expectComplete()
		.verify();
	}
	
}
