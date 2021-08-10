package bel.shoktan.twitch.modules;

import java.util.Map;

public interface UserNotice {
    void handleNotice(Map<String, String> data, String channel);
}
