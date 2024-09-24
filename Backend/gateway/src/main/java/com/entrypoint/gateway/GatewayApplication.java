package com.entrypoint.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.http.HttpMethod;


@EnableR2dbcRepositories
@SpringBootApplication(exclude={ReactiveSecurityAutoConfiguration.class})
public class GatewayApplication {

  @Autowired
  private AuthFilter authFilter;

  @Autowired
  private AuthFilterAdmin authFilterAdmin;

  @Autowired
  private AuthFilterEmployee authFilterEmployee;

  @Autowired
  private AuthFilterUser authFilterUser;

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

  @Bean
  public RouteLocator myRoutes(RouteLocatorBuilder builder, AuthFilterEmployee authFilterEmployee) {
    return builder.routes()

      .route("Admin", p -> p
        .path("/admin/**")
        .and()
        .method(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE)
        .filters(f -> {
          //f.rewritePath("/photos/(?<segment>.*)", "/${segment}");
          f.filter(authFilter);
          f.filter(authFilterAdmin);
          return f;
        })
        .uri("http://backend:8080/admin"))


      .route("Employee", p -> p
         .path("/employee/**")
         .and()
         .method(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE)
         .filters(f -> {
           //f.rewritePath("/photos/(?<segment>.*)", "/${segment}");
           f.filter(authFilter);
           f.filter(authFilterEmployee);
           return f;
         })
         .uri("http://backend:8080/employee"))


      .route("User", p -> p
         .path("/user/**")
         .and()
         .method(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE)
         .filters(f -> {
          //f.rewritePath("/photos/(?<segment>.*)", "/${segment}");
            f.filter(authFilter);
            f.filter(authFilterUser);
            return f;
         })
         .uri("http://backend:8080/user"))


        .route("auth/users app i mean, there's work to do", p -> p
          .path("/internal/auth")
          .uri("http://localhost:8081")
        )
      .build();
  }
}

