package me.kupchenko.webfluxexample.config;

import me.kupchenko.webfluxexample.model.ErrorResponse;
import me.kupchenko.webfluxexample.model.Form;
import me.kupchenko.webfluxexample.service.ExternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@EnableConfigurationProperties(ThymeleafProperties.class)
public class RouterConfig {

    @Autowired
    private ExternalService externalService;

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return route(GET("/home"), this::handleFormGet)
                .andRoute(POST("/form").and(contentType(MediaType.APPLICATION_JSON)), this::handleFormPost)
                .andRoute(GET("/hystrix/{errorRate}"), this::handleHystrixPost);
    }

    private Mono<ServerResponse> handleFormGet(ServerRequest serverRequest) {
        Map<String, Object> view = Rendering.view("home").modelAttribute("form", new Form()).build().modelAttributes();
        return ServerResponse.ok().contentType(MediaType.TEXT_HTML).render("home", view);
    }

    private Mono<ServerResponse> handleFormPost(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Form.class).flatMap(this::buildOkResponse);
    }

    private Mono<ServerResponse> handleHystrixPost(ServerRequest serverRequest) {
        String errorRate = serverRequest.pathVariable("errorRate");
        return Mono.just(errorRate)
                .flatMap(externalService::call)
                .flatMap(this::buildOkResponse)
                .onErrorResume(this::build500Response);
    }

    private Mono<ServerResponse> buildOkResponse(String string) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromObject(string));
    }

    private Mono<ServerResponse> build500Response(Throwable throwable) {
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromObject(new ErrorResponse("code", throwable.getMessage())));
    }

    private Mono<ServerResponse> buildOkResponse(Form form) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromObject(form));
    }
}
