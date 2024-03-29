package com.scoutingalpha.gateway;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.cloud.discovery.enabled = false"})
public class GatewayApplicationTests {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    ReactiveJwtDecoder jwtDecoder;


    @Test
    public void testCorsConfiguration() {
        Jwt jwt = jwt();

        when(this.jwtDecoder.decode(anyString())).thenReturn(Mono.just(jwt));
        WebTestClient.ResponseSpec responseSpec = webTestClient.put().uri("/")
                .headers(addJwt(jwt))
                .header("Origin", "http://example.com")
                .exchange();

        responseSpec.expectHeader().valueEquals("Access-Control-Allow-Origin", "*");
    }

    private Consumer<HttpHeaders> addJwt(Jwt jwt) {
        return httpHeaders -> httpHeaders.setBearerAuth(jwt.getTokenValue());
    }

    private Jwt jwt() {
        return new Jwt("token", null, null, Map.of("alg", "none"), Map.of("sub", "betsy"));
    }
}
