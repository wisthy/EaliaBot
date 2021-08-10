package bel.shoktan.twitch.modules.usermanagement;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
public class UserChannel {
    private String userName;
    private LocalDateTime joinAt;
    private LocalDateTime followAt;
}
