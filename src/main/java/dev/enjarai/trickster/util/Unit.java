package dev.enjarai.trickster.util;

import net.minecraft.util.Util;

import java.util.function.Function;

public abstract class Unit {
    protected final int precision;

    public Unit(int precision) {
        this.precision = precision;

        if (precision < -1 || precision > 10) {
            throw new IllegalStateException("Unit must have a precision in the range of -1 and 10 but got " + precision);
        }
    }

    public float correct(float value) {
        return (float) (value / Math.pow(1000, precision - 1));
    }

    public String shortName() {
        return shortNameStart() + shortNameEnd();
    }

    public String longName() {
        return longNameStart() + longNameEnd();
    }

    private String shortNameStart() {
        return switch (precision) {
            case 10 -> "Q";
            case 9 -> "R";
            case 8 -> "Y";
            case 7 -> "Z";
            case 6 -> "E";
            case 5 -> "P";
            case 4 -> "T";
            case 3 -> "G";
            case 2 -> "M";
            case 1 -> "k";
            case 0 -> "";
            case -1 -> "m";
            default ->
                    throw new IllegalStateException("Unit must have a precision in the range of -1 and 10 but got " + precision);
        };
    }

    private String longNameStart() {
        return switch (precision) {
            case 10 -> "quetta";
            case 9 -> "ronna";
            case 8 -> "yotta";
            case 7 -> "zetta";
            case 6 -> "exa";
            case 5 -> "peta";
            case 4 -> "tera";
            case 3 -> "giga";
            case 2 -> "mega";
            case 1 -> "kilo";
            case 0 -> "";
            case -1 -> "milli";
            default ->
                    throw new IllegalStateException("Unit must have a precision in the range of -1 and 10 but got " + precision);
        };
    }

    protected abstract String shortNameEnd();

    protected abstract String longNameEnd();

    public static Gandalf getGandalfUnit(float value) {
        return Gandalf.cache.apply(kiloUsageToPrecision(value));
    }

    public static Merlin getMerlinUnit(float value) {
        return Merlin.cache.apply(kiloUsageToPrecision(value));
    }

    private static int kiloUsageToPrecision(float value) {
        int precision = 10;
        double d = Math.abs(value) / Math.pow(1000, precision - 1);

        while (precision > -1 && d < 10) {
            precision -= 1;
            d *= 1000;
        }

        return precision;
    }

    public static class Gandalf extends Unit {
        static final Function<Integer, Gandalf> cache = Util.memoize(Gandalf::new);

        public Gandalf(int precision) {
            super(precision);
        }

        @Override
        protected String shortNameEnd() {
            return "G";
        }

        @Override
        protected String longNameEnd() {
            return "Gandalf";
        }
    }

    public static class Merlin extends Unit {
        static final Function<Integer, Merlin> cache = Util.memoize(Merlin::new);

        public Merlin(int precision) {
            super(precision);
        }

        @Override
        protected String shortNameEnd() {
            return "M";
        }

        @Override
        protected String longNameEnd() {
            return "Merlin";
        }
    }
}
