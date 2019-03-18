package me.kupchenko.webfluxexample.config;

import com.netflix.hystrix.HystrixCommandProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HystrixConfig {
    @Bean
    public HystrixCommandProperties.Setter getSetter() {
        HystrixCommandProperties.Setter setter = HystrixCommandProperties.defaultSetter();

        setter.withExecutionTimeoutEnabled(true);
        setter.withExecutionTimeoutInMilliseconds(10_000);

        return setter;
    }
}
