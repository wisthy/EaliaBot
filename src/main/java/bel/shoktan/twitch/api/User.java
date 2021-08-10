package bel.shoktan.twitch.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class User {
    @JsonProperty("created_at")
    private String createdAt;

    private String id;

    @JsonProperty("display_name")
    private String displayName;
}
