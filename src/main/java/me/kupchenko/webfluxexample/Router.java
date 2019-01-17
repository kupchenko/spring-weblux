package me.kupchenko.webfluxexample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@EnableWebFlux
public class Router {
    @Bean
    public RouterFunction<ServerResponse> c3Route() {
        return route(GET("/home"), this::getRedirect)
                .andRoute(POST("/home").and(contentType(MediaType.APPLICATION_JSON)), this::postHandle);
    }

    private Mono<ServerResponse> getRedirect(ServerRequest serverRequest) {
        Rendering view = Rendering.view("home.html").build();
        return null;
    }

    private Mono<ServerResponse> postHandle(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Form.class).flatMap(this::buildOkResponse);
    }

    private Mono<ServerResponse> buildOkResponse(Form form) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromObject(form));
    }
}
