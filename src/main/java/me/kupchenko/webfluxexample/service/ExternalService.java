package me.kupchenko.webfluxexample.service;

import com.netflix.hystrix.HystrixCommandProperties;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.hystrix.HystrixCommands;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ExternalService {

    private static final Logger log = LoggerFactory.getLogger(ExternalService.class);

    private AtomicInteger requests = new AtomicInteger();
    private AtomicInteger fallback = new AtomicInteger();

    @Autowired
    private HystrixCommandProperties.Setter setter;

    @PostConstruct
    public void PostConstruct() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Requests: " + requests.get());
                System.out.println("Fallback: " + fallback.get());
            }
        }).start();
    }

    public Mono<String> call(String errorRate) {
        return HystrixCommands
                .from(businessLogic(Double.valueOf(errorRate)))
                .fallback(this::fallback)
                .commandName("ExternalServiceCall")
                .commandProperties(setter)
                .toMono();
    }

    private Publisher<String> fallback(Throwable throwable) {
        fallback.incrementAndGet();
        return Mono.just("Some fallback because of: " + throwable.getMessage());
    }

    private Mono<String> businessLogic(Double errorRate) {
        requests.incrementAndGet();
        log.debug("Calling with {}", errorRate);
        if (Math.random() < errorRate) {
            return Mono.error(new RuntimeException("Simulated, don't panic"));
        }
        return Mono.just("OK");
    }

}
