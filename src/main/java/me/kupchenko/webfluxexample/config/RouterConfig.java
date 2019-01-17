package me.kupchenko.webfluxexample.config;

import me.kupchenko.webfluxexample.model.Form;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return route(GET("/home"), this::getHandle)
                .andRoute(POST("/form").and(contentType(MediaType.APPLICATION_JSON)), this::postHandle);
    }

    private Mono<ServerResponse> getHandle(ServerRequest serverRequest) {
        Map<String, Object> view = Rendering.view("home").modelAttribute("form", new Form()).build().modelAttributes();
        return ServerResponse.ok().contentType(MediaType.TEXT_HTML).render("home", view);
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
