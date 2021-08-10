package bel.shoktan.web.controllers;

import bel.shoktan.config.WebConfig;
import bel.shoktan.twitch.api.User;
import bel.shoktan.twitch.api.Users;
import bel.shoktan.twitch.api.Validate;
import bel.shoktan.web.HeadersGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class HelloController {
    public static final String API_USER = "https://api.twitch.tv/helix/users?login={username}";

    @Autowired
    private HeadersGenerator generator;

    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }


    @GetMapping("/info")
    public Map<String, Object> getUserAttributes(@AuthenticationPrincipal OAuth2User user) {
        return user.getAttributes();
    }

    @GetMapping("/authorities")
    public Collection<GrantedAuthority> getAuthorities(
            @AuthenticationPrincipal OAuth2AuthenticationToken token
    ) {
        return token.getAuthorities();
    }


    @GetMapping("/user/{name}")
    public Users getUserInfo(@PathVariable("name") String username, RestTemplate restTemplate){
        HttpEntity<Object> headers = generator.entity(false);
        ResponseEntity<Users> user = restTemplate.exchange(API_USER, HttpMethod.GET, headers, Users.class, username);
        return user.getBody();
    }

}
