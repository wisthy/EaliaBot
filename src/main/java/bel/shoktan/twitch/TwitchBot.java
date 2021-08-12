package bel.shoktan.twitch;

import bel.shoktan.twitch.modules.UserNotice;
import org.jibble.pircbot.PircBot;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class TwitchBot extends PircBot {
    private Map<String, Channel> channels = new HashMap<>();


    private final String requestedNick;
    private final Set<String> admins = new TreeSet<>();

    private String realNick;
    private String realServer;

    public TwitchBot(String nick) {
        this.requestedNick = nick;
        setName(this.requestedNick);
        setLogin(this.requestedNick);
        admins.add("wisthler_badin");
        admins.add("malchemisttv");
    }

    public void joinNewChannel(String name){
        if(channels.get(name) == null) {
            Channel channel = new Channel(this, name);
            this.joinChannel(name);
            channels.put(name, channel);
        }
    }

    private void leaveChannel(String name) {
        Channel channel = channels.get(name);
        if(channel != null) {
            this.partChannel(name);
            channels.remove(name);
        }
    }

    @Override
    protected void onConnect() {
        super.onConnect();
        System.out.println("Connected!");

        // Sending special capabilities.
        sendRawLine("CAP REQ :twitch.tv/membership");
        sendRawLine("CAP REQ :twitch.tv/commands");
        sendRawLine("CAP REQ :twitch.tv/tags");
    }

    @Override
    protected void handleLine(String line) {
        super.handleLine(line);

        if (line.startsWith(":")) {
            String[] recvLines = line.split(" ");

            // First message is 001, extract logged in information.
            if (recvLines[1].equals("001")) {
                this.realServer = recvLines[0].substring(1);
                this.realNick = recvLines[2];
                System.out.println("realServer: " + this.realServer);
                System.out.println("realNick: " + this.realNick);
            }
            if(recvLines.length == 3){
                if(Objects.equals(recvLines[1], "JOIN")){
                    String userInfo = recvLines[0].split("[:!]")[1];
                    handleJoin(userInfo, recvLines[2]);
                }
            }
        }

        if(line.startsWith("@")){
            String[] recvLines = line.split(" ", 5);
            Map<String, String> flags = Stream.of(recvLines[0].split(";")).map(str -> str.split("=")).collect(toMap(str -> str[0], str -> str.length>1?str[1]:""));
            String userInfo = recvLines[1].split("[:!]")[1];
            String action = recvLines[2];
            String destination = recvLines[3];
            if(recvLines.length == 4){
                action = recvLines[2];
                destination = recvLines[3];
                if(Objects.equals(action, "USERNOTICE")){
                    handleUserNotice(destination, flags);
                    return;
                }
            }
            if(recvLines.length > 4) {
                String message = recvLines[4];
                if (Objects.equals(action, "PRIVMSG")) {
                    Channel channel = channels.get(destination);
                    if(channel != null){
                        channel.handleChatMessage(flags, userInfo, message);
                    }

                    return;
                }
                if (Objects.equals(action, "WHISPER")) {
                    handleWhisper(flags, userInfo, destination, message);
                    return;
                }
            }
        }


    }

    private void handleJoin(String userInfo, String channelName) {
        Channel channel = channels.get(channelName);
        if(channel != null){
            channel.handleJoin(userInfo);
        }
    }

    private void handleUserNotice(String destination, Map<String, String> values){
        Channel channel = channels.get(destination);
        if(channel != null){
            channel.handleUserNotice(values);
        }
    }


    private void handleWhisper(Map<String, String> flags, String userInfo, String destination, String message) {
        System.out.printf("%s whispers \"%s\" on #%s%n", userInfo, message, destination);
        if(admins.contains(userInfo)){
            if(message.startsWith(":add_so")){
                String[] data = message.split(" ");
                String user = data[1];
                String channel = data[2];
                return;
            }

            if(message.startsWith(":stop")){
                shutDown();
            }
            if(message.startsWith(":join")){
                String[] data = message.split(" ");
                String channel = data[1];
                System.out.printf("asking to join a new channel %s%n", channel);
                joinNewChannel(channel);
            }
            if(message.startsWith(":leave")){
                String[] data = message.split(" ");
                String channel = data[1];
                System.out.printf("asking to leave channel %s%n", channel);
                leaveChannel(channel);
            }
            if(message.startsWith(":bit")){
                String[] data = message.split(" ");
                String channel = data[1];
                String action = data[2];
                String what = data[3];
                String count = data[4];
                Channel where = channels.get(channel);
                if(where != null){
                    where.addBitAndSub(what, action, count);
                }
            }
            if(message.startsWith(":setBits")){
                String[] data = message.split(" ");
                String channel = data[1];
                String count = data[2];
                Channel where = channels.get(channel);
                if(where != null){
                    where.setBits(count);
                }
            }
            if(message.startsWith(":setStart")){
                String[] data = message.split(" ");
                String channel = data[1];
                String value = data[2];
                Channel where = channels.get(channel);
                if(where != null){
                    where.setStartTime(value);
                }
            }

        }
    }

    private void shutDown() {
        System.out.println("bye!");
        disconnect();
        dispose();
    }


    @Override
    protected void onJoin(String channel, String sender, String login, String hostname) {
        super.onJoin(channel, sender, login, hostname);
        if (sender.equals(this.realNick)){
            System.out.println("Successfully joined: " + channel);
        }
    }

    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        if (message.equalsIgnoreCase("time")) {
            String time = new java.util.Date().toString();
            sendMessage(channel, sender + ": The time is now " + time);
        }
    }

    public static void main(String[] args){
        String line = "@badges=premium/1;color=#1E90FF;display-name=wisthler_badin;emotes=;message-id=3;thread-id=418578073_713847137;turbo=0;user-id=418578073;user-type= :wisthler_badin!wisthler_badin@wisthler_badin.tmi.twitch.tv WHISPER ealiabot :add_so test ttest2";
        String[] recvLines = line.split(" ", 5);
        for(int i = 0; i < recvLines.length; i++){
            System.out.printf("[%d] %s%n", i, recvLines[i]);
        }

    }

    @PreDestroy
    public void onDestroy() throws Exception {
        shutDown();
    }

    public Channel get(String name) {
        return channels.get(name);
    }
}