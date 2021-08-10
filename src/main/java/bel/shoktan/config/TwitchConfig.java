package bel.shoktan.config;

import bel.shoktan.twitch.Channel;
import bel.shoktan.twitch.TwitchBot;
import bel.shoktan.twitch.modules.bitsandsub.SubGoal;
import org.jibble.pircbot.IrcException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.io.IOException;

@Configuration
public class TwitchConfig {
    @Value("${twitch.pirc.url}")
    private String Address;

    @Value("${twitch.pirc.nick}")
    private String Nick;

    @Value("${twitch.pirc.port}")
    private int Port;

    @Value("${twitch.oauth}")
    private String OAuth;


    private String OAuth2;


    @Bean
    public TwitchBot bot() throws IOException, IrcException {
        TwitchBot bot = new TwitchBot(Nick);
        bot.setVerbose(true);
        bot.connect(Address, Port, "oauth:"+OAuth);
        bot.joinNewChannel("#wisthler_badin");
        bot.joinNewChannel("#raavalicious");
        bot.joinNewChannel("#fireballannie");
        bot.joinNewChannel("#malchemisttv");
        Channel malchemist = bot.get("#malchemisttv");
        malchemist.add(SubGoal.builder().subs(5).message("Look at old clips").build());
        malchemist.add(SubGoal.builder().subs(10).message("Look at old art").build());
        malchemist.add(SubGoal.builder().subs(15).message("Chat writes a tweet").build());
        malchemist.add(SubGoal.builder().subs(20).message("Meme no laugh challenge").build());
        malchemist.add(SubGoal.builder().subs(25).message("No aww cute challenge").build());
        malchemist.add(SubGoal.builder().subs(50).message("Chat chooses fashion will keep for a week").build());
        malchemist.add(SubGoal.builder().subs(75).message("Play Senua's Sacrifice").build());
        malchemist.add(SubGoal.builder().subs(100).message("Play Getting Over It").build());
        malchemist.add(SubGoal.builder().subs(125).message("Play Pubg W/ Challenges").build());
        malchemist.add(SubGoal.builder().subs(150).message("Play worst game on steam").build());
        malchemist.add(SubGoal.builder().subs(175).message("Gamble all gold live in GW2").build());
        malchemist.add(SubGoal.builder().subs(200).message("FMA or GW2 themed tattoo").build());
        malchemist.setStartTime("2021-08-15T04:00:00");
        return bot;
    }

    @PreDestroy
    public void onDestroy() throws Exception {
        System.out.println("Spring Container is destroyed!");
    }
}
