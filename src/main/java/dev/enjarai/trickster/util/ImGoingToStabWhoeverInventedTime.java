package dev.enjarai.trickster.util;

import java.util.concurrent.TimeUnit;

public class ImGoingToStabWhoeverInventedTime {
    public static String howLongIsThisQuestionMark(long milliseconds) {
        // Java what the fuck is this
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);

        // Good enough, idc
        if (hours > 1) {
            return "%d hours".formatted(hours);
        } else if (hours > 0) {
            return "%d hour".formatted(hours);
        } else if (minutes > 1) {
            return "%d minutes".formatted(minutes);
        } else if (minutes > 0) {
            return "%d minute".formatted(minutes);
        } else if (seconds > 1) {
            return "%d seconds".formatted(seconds);
        } else if (seconds > 0) {
            return "%d second".formatted(seconds);
        } else {
            return "<1 second";
        }
    }
}
