package bel.shoktan.twitch.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class Validate {

    @JsonProperty("client_id")
    private String clientId;

    private String login;

    private String[] scopes;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("expires_in")
    private String expiresIn;
}
