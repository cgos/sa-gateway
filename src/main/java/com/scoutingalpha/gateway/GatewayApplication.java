package com.scoutingalpha.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.security.oauth2.gateway.TokenRelayGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@EnableEurekaClient
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("sa-player-info", r -> r.path("/players")
                        .uri("lb://sa-player-info"))
                .route("sa-player-info", r -> r.path("/players")
                        .filters(f -> f.hystrix(c -> c.setName("playersFallback")
                                .setFallbackUri("forward:/players-fallback")))
                        .uri("lb://sa-player-info/players"))
                .build();
    }

    // TODO Add token refresh: https://github.com/spring-cloud/spring-cloud-security/issues/175
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, TokenRelayGatewayFilterFactory filterFactory) {
        return builder.routes()
                .route("sa-player-info", r -> r.path("/players")
                        .filters(f -> f.filters(filterFactory.apply()))
                        .uri("lb://sa-player-info/players"))
                .build();
    }
}


@RestController
class PlayersFallback {
	@GetMapping("/players-fallback")
	public Flux<String> noPlayers() {
		return Flux.empty();
	}
}
