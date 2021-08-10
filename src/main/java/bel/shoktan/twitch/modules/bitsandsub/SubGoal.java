package bel.shoktan.twitch.modules.bitsandsub;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubGoal implements Comparable<SubGoal> {
    private long subs;
    private String message;
    private boolean communicated = false;

    @Override
    public int compareTo(SubGoal o) {
        return Long.compare(this.subs, o.subs);
    }
}
