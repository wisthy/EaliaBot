package bel.shoktan.twitch.modules.usermanagement;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class UserManagementModule {
    private Map<String, UserChannel> data = new HashMap<>();
    private static final LocalDateTime START = LocalDateTime.now();


    public void handleJoin(String userInfo) {
        UserChannel user = data.get(userInfo);
        if(user == null){
            user = UserChannel.builder().userName(userInfo).joinAt(LocalDateTime.now()).build();
            data.put(userInfo, user);
        }
    }



    
}
