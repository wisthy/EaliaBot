package bel.shoktan.twitch;

import bel.shoktan.twitch.modules.bitsandsub.BitsAndSubModule;
import bel.shoktan.twitch.modules.bitsandsub.SubGoal;
import bel.shoktan.twitch.modules.usermanagement.UserManagementModule;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
public class Channel {
    private String name;
    private Set<String> cheers = new TreeSet<>();
    private Set<String> admin = new TreeSet<>();
    private TwitchBot parent;
    private UserManagementModule userManagementModule = new UserManagementModule();
    private BitsAndSubModule bitsAndSubModule;
    private boolean auto_so_enabled = false;

    public Channel(TwitchBot bot, String name) {
        this.name = name;
        this.parent = bot;
        bitsAndSubModule = new BitsAndSubModule(this);
        admin.add("418578073");
    }

    public String getName() {
        return name;
    }

    protected void handleChatMessage(Map<String, String> flags, String sender, String message) {
        System.out.printf("%s wrote \"%s\" on %s%n", sender, message, name);
        bitsAndSubModule.checkDeadline();
        String senderId = flags.get("user-id");
        if(admin.contains(senderId) && message.startsWith("::")){
            handleAdminMessage(message);
            return;
        }
        String bits = flags.get("bits");
        if(bits != null && !bits.equals("")){
            bitsAndSubModule.handleBits(bits, flags);
        }
        if (auto_so_enabled) {
            if (!cheers.contains(sender)) {
                System.out.printf("first message of the day for %s%n", sender);
                cheers.add(sender);
                parent.sendMessage(name, String.format("Hello %s", sender));
            } else {
                System.out.printf("not the first message of the day for %s, no need to cheer%n", sender);
            }
        }
    }

    private void handleAdminMessage(String message) {
        System.out.println("it's an admin, check admin command");
        if(message.startsWith("::auto_so")){
            String[] values = message.split(" ");
            if(values.length >= 2){
                switch (values[1]) {
                    case "enable" : auto_so_enabled = true; break;
                    case "disable": auto_so_enabled = false; break;
                }
            }
            return;
        }
        if(message.startsWith("::user")){
            String[] values = message.split(" ");
            if(values.length >= 2){
                parent.sendMessage(name, String.format("/user %s", values[1]));
            }
            return;
        }
        if(message.startsWith("::collected")){
            parent.sendMessage(name, bitsAndSubModule.display());
            return;
        }
    }

    private String extract_flag(String tag, String[] flags) {
        if(flags != null && flags.length > 0){
            for(String flag : flags){
                String[] values = flag.split("=");
                if(Objects.equals(values[0], tag)){
                    return values[1];
                }
            }
        }
        return null;
    }

    public void handleJoin(String userInfo) {
        userManagementModule.handleJoin(userInfo);
    }

    public void handleUserNotice(Map<String, String> values) {
        bitsAndSubModule.handleNotice(values);
    }

    public void addBitAndSub(String what, String action, String count) {
        bitsAndSubModule.addBitAndSub(what, action, count);
    }

    public void setBits(String count) {
        bitsAndSubModule.setBitsANdSub(count);
    }

    public void setStartTime(String value) {
        bitsAndSubModule.setStartTime(value);
    }

    public void echo(String message) {
        if(parent != null && parent.isConnected()) {
            parent.sendMessage(name, message);
        }else{
            log.warn("not connected, message to send was [{}]", message);
        }
    }

    public boolean add(SubGoal subGoal) {
        return bitsAndSubModule.add(subGoal);
    }
}
