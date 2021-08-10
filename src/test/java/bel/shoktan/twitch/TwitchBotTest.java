package bel.shoktan.twitch;

import bel.shoktan.twitch.modules.bitsandsub.SubGoal;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.*;

class TwitchBotTest {

    @Test
    void whatever() {
        String test = "@badge-info=subscriber/13;badges=vip/1,subscriber/12;color=#FF69B4;display-name=DemonsAndDust;emotes=;flags=;id=b64cfeba-13a6-4020-94a9-6724ea7645ba;login=demonsanddust;mod=0;msg-id=subgift;msg-param-gift-months=1;msg-param-months=6;msg-param-origin-id=c9\\sb3\\s2f\\s62\\sb0\\s13\\s4e\\s28\\s87\\s28\\s48\\s21\\s21\\s94\\sa9\\se7\\s1a\\s60\\sbf\\sc3;msg-param-recipient-display-name=Ceithlenn;msg-param-recipient-id=27608820;msg-param-recipient-user-name=ceithlenn;msg-param-sender-count=34;msg-param-sub-plan-name=State\\sAlchemist\\sMajor\\s(malchemisttv);msg-param-sub-plan=1000;room-id=44400034;subscriber=1;system-msg=DemonsAndDust\\sgifted\\sa\\sTier\\s1\\ssub\\sto\\sCeithlenn!\\sThey\\shave\\sgiven\\s34\\sGift\\sSubs\\sin\\sthe\\schannel!;tmi-sent-ts=1628577861083;user-id=35051213;user-type= :tmi.twitch.tv USERNOTICE #malchemisttv";
        test = "@badge-info=subscriber/3;badges=subscriber/3,premium/1;color=#0000FF;display-name=skye_teevee;emotes=;flags=;id=a6950f96-154a-4ab2-8a6e-574780fd9853;login=skye_teevee;mod=0;msg-id=resub;msg-param-cumulative-months=3;msg-param-months=0;msg-param-multimonth-duration=0;msg-param-multimonth-tenure=0;msg-param-should-share-streak=1;msg-param-streak-months=2;msg-param-sub-plan-name=Channel\\sSubscription\\s(raavalicious);msg-param-sub-plan=1000;msg-param-was-gifted=false;room-id=152718681;subscriber=1;system-msg=skye_teevee\\ssubscribed\\sat\\sTier\\s1.\\sThey've\\ssubscribed\\sfor\\s3\\smonths,\\scurrently\\son\\sa\\s2\\smonth\\sstreak!;tmi-sent-ts=1628588186664;user-id=674518756;user-type= :tmi.twitch.tv USERNOTICE #raavalicious :IMBIGFAN streak continues :3 UwU";
        String[] split = test.split(" ");
        Map<String, String> tmp = Stream.of(split[0].split(";")).map(str -> str.split("=")).collect(toMap(str -> str[0], str -> str.length>1?str[1]:""));
        Channel channel = new Channel(null, "#Test");
        channel.handleUserNotice(tmp);//2007-12-03T10:15:30
        channel.setStartTime("2021-08-10T09:01:02");
        channel.addBitAndSub("t3", "add", "1");
        channel.add(SubGoal.builder().subs(5).message("Look at old clips").build());
        channel.add(SubGoal.builder().subs(10).message("Look at old art").build());
        channel.addBitAndSub("t1", "add", "4");
        channel.addBitAndSub("t1", "add", "1");

        //:setStart #wisthler_badin 2021-08-10T09:01:02
    }

    @Test
    void bits(){
        String test = "@badge-info=;badges=vip/1,bits-leader/2;bits=25;color=#8A0000;display-name=DarkerCrimson;emotes=;flags=;id=a7fed666-772c-4609-af66-89ae0cd6c79a;mod=0;room-id=44400034;subscriber=0;tmi-sent-ts=1628579653322;turbo=0;user-id=63043447;user-type= :darkercrimson!darkercrimson@darkercrimson.tmi.twitch.tv PRIVMSG #malchemisttv :uni25";
        String[] split = test.split(" ");
        for(String line : split){
            System.out.println(line);
        }
        Map<String, String> tmp = Stream.of(split[0].split(";")).map(str -> str.split("=")).collect(toMap(str -> str[0], str -> str.length>1?str[1]:""));
        for(String key : tmp.keySet()){
            System.out.printf("%s => %s%n", key, tmp.get(key));
        }
    }

    @Test
    void dummy(){
        Channel wisthler = new Channel(null, "#wisthler_badin");
        wisthler.add(SubGoal.builder().subs(5).message("Look at old clips").build());
        wisthler.add(SubGoal.builder().subs(10).message("Look at old art").build());
        wisthler.add(SubGoal.builder().subs(15).message("Chat writes a tweet").build());
        wisthler.add(SubGoal.builder().subs(20).message("Meme no laugh challenge").build());
        wisthler.add(SubGoal.builder().subs(25).message("No aww cute challenge").build());
        wisthler.add(SubGoal.builder().subs(50).message("Chat chooses fashion will keep for a week").build());
        wisthler.add(SubGoal.builder().subs(75).message("Play Senua's Sacrifice").build());
        wisthler.add(SubGoal.builder().subs(100).message("Play Getting Over It").build());
        wisthler.add(SubGoal.builder().subs(125).message("Play Pubg W/ Challenges").build());
        wisthler.add(SubGoal.builder().subs(150).message("Play worst game on steam").build());
        wisthler.add(SubGoal.builder().subs(175).message("Gamble all gold live in GW2").build());
        wisthler.add(SubGoal.builder().subs(200).message("FMA or GW2 themed tattoo").build());
        wisthler.enableBitAndSubs();
        wisthler.setStartTime(ZonedDateTime.now().minus(1, ChronoUnit.HOURS).toString());
        wisthler.setBits("42/12/1/1");
    }

    @Test
    void timezone(){
        LocalDateTime time = LocalDateTime.now();
        System.out.println(time);
        ZonedDateTime zdt = time.atZone(ZoneId.of("Europe/Brussels"));
        System.out.println(zdt);
        System.out.println(zdt.format(DateTimeFormatter.ofPattern("HH:mm:ss(z)")));

    }
}