package com.entrypoint.gateway;

import io.jsonwebtoken.Jwts;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthFilterUser implements GatewayFilter{
  /**
   * This method MUST BE used after auth filter is done with normal users.
   * Using this before AuthFilter.filter will result with bunch of 500 errors.
   * Cookie checks are not happening here!
   */

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String token = exchange.getRequest().getCookies().getFirst("JWT").getValue();

    String role = (String) Jwts.parser().verifyWith(JWTUtil.getSecretKey()).build().parseSignedClaims(token).getPayload().get("Role");

    if (role.equals("USER")) {
      return chain.filter(exchange);
    } else {
      ServerHttpResponse response = exchange.getResponse();
      response.setStatusCode(HttpStatus.FORBIDDEN);
      return response.setComplete();
    }

  }
}
