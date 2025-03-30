package dev.enjarai.trickster.util;

import java.util.concurrent.TimeUnit;

public class ImGoingToStabWhoeverInventedTime {
    public static String howLongIsThisQuestionMark(long milliseconds) {
        // Java what the fuck is this
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliseconds));
        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);

        // Good enough, idc
        if (days > 365 * 2) {
            return "%d years".formatted(days / 365);
        } else if (days > 365) {
            return "%d year".formatted(days / 365);
        } else if (days > 1) {
            return "%d days".formatted(days);
        } else if (days > 0) {
            return "%d day".formatted(days);
        } else if (hours > 1) {
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
