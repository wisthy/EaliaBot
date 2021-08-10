package bel.shoktan.twitch.api;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class Users {
    private User[] data;
}
