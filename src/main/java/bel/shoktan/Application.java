package bel.shoktan;

import bel.shoktan.config.WebConfig;
import bel.shoktan.twitch.api.Validate;
import bel.shoktan.web.HeadersGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@Slf4j
public class Application {


    public static final String API_VALIDATE = "https://id.twitch.tv/oauth2/validate";


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
    public CommandLineRunner run(RestTemplate restTemplate, HeadersGenerator config) throws Exception {
        return args -> {
            HttpEntity<Object> headers = config.entity();
            ResponseEntity<Validate> validate = restTemplate.exchange(API_VALIDATE, HttpMethod.GET, headers, Validate.class);
            log.info(validate.getBody().toString());
            config.setClientId(validate.getBody().getClientId());
        };
    }

}
