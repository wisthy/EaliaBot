package bel.shoktan.twitch.modules.bitsandsub;

import bel.shoktan.twitch.Channel;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
public class BitsAndSubModule {
    private final Channel channel;
    private static final Set<String> SUBS = new TreeSet<>(Arrays.asList("sub", "resub", "subgift", "anonsubgift"));
    private LocalDateTime start = LocalDateTime.now().minus(2, ChronoUnit.DAYS);
    private LocalDateTime endLimit = start.plus(1, ChronoUnit.DAYS);
    private Set<SubGoal> subGoals = new TreeSet<>();
    private boolean deadlineCommunicated = false;

    public BitsAndSubModule(Channel channel) {
        this.channel = channel;
        countDown();
    }

    public boolean add(SubGoal subGoal) {
        return subGoals.add(subGoal);
    }

    private long bits = 0L;
    private long sub1 = 0L;
    private long sub2 = 0L;
    private long sub3 = 0L;

    public void handleNotice(Map<String, String> data) {
        String action = data.get("msg-id");
        String plan = data.get("msg-param-sub-plan");
        if(action != null && SUBS.contains(action) && plan != null){
            switch(plan){
                case "Prime":
                case "1000": sub1++; break;
                case "2000": sub2++; break;
                case "3000": sub3++; break;
            }
            display();
        }
    }

    public void handleBits(String bits, Map<String, String> flags) {
        long value = 0L;
        try{
            value = Long.parseLong(bits);
        }catch (NumberFormatException e){
            log.error("unable to parse {}", bits, e);
        }
        this.bits += value;
        display();
    }

    public String display() {
        boolean something = false;
        String message = "";
        if(bits != 0L){
            message += String.format("%d bit(s)", bits);
            something = true;
        }
        if(sub1 > 0L){
            message += (something?", ":"") + String.format("%d tier 1 sub(s) (+prime)", sub1);
            something = true;
        }
        if(sub2 > 0L){
            message += (something?", ":"") + String.format("%d tier 2 sub(s)", sub2);
            something = true;
        }
        if(sub3 > 0L){
            message += (something?", ":"") + String.format("%d tier 3 sub(s)", sub3);
            something = true;
        }
        if(something){
            message = String.format("you've collected %s so far", message);
        }else{
            message = "you have collected nothing so far :(";
        }
        log.info("{} - {}", channel.getName(), message);
        countDown();
        checkGoal();
        return message;
    }

    private void checkGoal() {
        long subs = sub1+sub2+sub3;
        subGoals.stream().filter(x -> !x.isCommunicated()).filter(x -> x.getSubs() <= subs).forEach(this::echoGoal);
    }

    private void echoGoal(SubGoal goal){
        goal.setCommunicated(true);
        channel.echo(String.format("@%s, goal \"%s\" has been reached", channel.getName().replace("#",""), goal.getMessage()));
    }

    public void setBitsANdSub(String what){
        String[] data = what.split("/");
        if(data.length != 4){
            return;
        }
        try{
            this.bits = Long.parseLong(data[0]);
        }catch(NumberFormatException e){
            log.error("unable to parse", e);
        }
        try{
            this.sub1 = Long.parseLong(data[1]);
        }catch(NumberFormatException e){
            log.error("unable to parse", e);
        }
        try{
            this.sub2 = Long.parseLong(data[2]);
        }catch(NumberFormatException e){
            log.error("unable to parse", e);
        }
        try{
            this.sub3 = Long.parseLong(data[3]);
        }catch(NumberFormatException e){
            log.error("unable to parse", e);
        }
    }

    public void addBitAndSub(String what, String action, String count) {
        if("reset".equals(action)){
            this.bits = 0;
            this.sub1 = 0;
            this.sub2 = 0;
            this.sub3 = 0;
            return;
        }
        long amount = 0L;
        try{
            amount = Long.parseLong(count);
        }catch(NumberFormatException e){
            log.error("unable to parse", e);
        }
        if("sub".equals(action)){
            amount = amount * -1L;
        }

        switch (what){
            case "bit": this.bits += amount; break;
            case "t1": this.sub1 += amount; break;
            case "t2": this.sub2 += amount; break;
            case "t3": this.sub3 += amount; break;
        }
        display();
    }

    public void setStartTime(String time){
        start = LocalDateTime.parse(time);
        endLimit = start.plus(1, ChronoUnit.DAYS);
        log.info("Stream start at {}, end at the earliest at {} at the latest at {} (CEST)", start, start.plus(12, ChronoUnit.HOURS), start.plus(24, ChronoUnit.HOURS));
    }

    private Optional<LocalDateTime> deadline(){
        LocalDateTime end = start.plus(this.bits * 5, ChronoUnit.MINUTES);
        end = end.plus(this.sub1 * 10, ChronoUnit.MINUTES);
        end = end.plus(this.sub2 * 15, ChronoUnit.MINUTES);
        end = end.plus(this.sub3 * 20, ChronoUnit.MINUTES);
        end = end.plus(12, ChronoUnit.HOURS);
        if(end.isAfter(endLimit)){
            return Optional.empty();
        }else{
            return Optional.of(end);
        }
    }

    public String countDown(){
        Optional<LocalDateTime> endOption = deadline();
        String reached ="";
        LocalDateTime end;
        if(endOption.isPresent()) {
            end = endOption.get();
        }else{
            log.info("deadline reached");
            end = endLimit;
            reached = " (24h deadline reached)";
        }
        Duration duration = Duration.between(LocalDateTime.now(), end);
        log.info(duration.toString());
        String prettyPrint = duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
        ZonedDateTime endLocal = end.atZone(ZoneId.of("Europe/Brussels"));
        ZonedDateTime endUs = endLocal.withZoneSameInstant(ZoneId.of("America/Chicago"));
        String message = String.format("Stream end at %s - %s%s, so in %s", endUs.format(DateTimeFormatter.ofPattern("hh:mm:ss(z)")), endLocal.format(DateTimeFormatter.ofPattern("hh:mm:ss(z)")), reached, prettyPrint);
        log.info(String.format("%s - %s", channel.getName(), message));
        return message;
    }

    public void checkDeadline() {
        LocalDateTime end = deadline().orElse(endLimit);
        if(!deadlineCommunicated && end.isAfter(LocalDateTime.now())){
            channel.echo("the end of the stream has been reached");
        }
    }
}
