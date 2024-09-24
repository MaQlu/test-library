package com.entrypoint.gateway;

import maqlulibrary.entities.User;
import maqlulibrary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@ComponentScan(basePackages = {"maqlu"})
@RestController
public class AuthController {

  @Value("${TARGET:not_dev}")
  private String appTarget;

  private final UserRepository userRepository;

  AuthController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  // DEV
  @PostMapping("/auth/login")
  public Optional<ResponseEntity<String>> login(@RequestBody User user, ServerHttpResponse response) {
    return userRepository.findById(user.getUserId()).map(u -> {
      if (u.getPassword().equals(user.getPassword())) {
        String token = JWTUtil.generateToken(u.getUserId(), u.getRole());
        ResponseCookie responseCookie = ResponseCookie.from("JWT", token)
                .httpOnly(true)
                .secure(!appTarget.equals("DEV")) // !appTarget.equals("DEV")
                .sameSite("Lax")
                .maxAge(JWTUtil.getJWTExpiration())
                .path("/")
                .build();

        response.addCookie(responseCookie);
        // response.setComplete();
        return ResponseEntity.ok("Logged in");
      } else {
        throw new BadCredentialsException("Invalid username and/or password");
      }
    }); //.switchIfEmpty(Mono.error(new BadCredentialsException("Invalid username and/or password")));
  }

  @GetMapping("/auth/logout")
  public Mono<Void> logout(ServerHttpResponse response) {
    ResponseCookie responseCookie = ResponseCookie.from("JWT", "")
            .httpOnly(true)
            .secure(!appTarget.equals("DEV"))
            .sameSite("Lax")
            .maxAge(0)
            .path("/")
            .build();

    response.addCookie(responseCookie);
    return response.setComplete();
  }

  @GetMapping("/auth/login")
  public Flux<User> getAllUsers() {
    return (Flux<User>) userRepository.findAll();
  }
}
