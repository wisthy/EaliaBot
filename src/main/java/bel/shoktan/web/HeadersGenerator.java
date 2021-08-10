package bel.shoktan.web;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Slf4j
public class HeadersGenerator {
    @Value("${twitch.oauth}")
    private String OAuth;

    @Setter
    private String clientId;

    public HttpEntity<Object> entity(){
        return entity(true);
    }

    public HttpEntity<Object> entity(boolean oath){
        HttpHeaders headers = new HttpHeaders();
        String tag = (oath)?"OAuth":"Bearer";
        headers.set("Authorization", tag +" " + OAuth);
        if(clientId != null){
            headers.set("Client-Id", clientId);
        }
        log.info(headers.toString());
        return new HttpEntity<>(headers);
    }
}
