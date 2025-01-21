package dev.enjarai.trickster.util;

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
        switch (precision) {
            case 10:
                return "Q";
            case 9:
                return "R";
            case 8:
                return "Y";
            case 7:
                return "Z";
            case 6:
                return "E";
            case 5:
                return "P";
            case 4:
                return "T";
            case 3:
                return "G";
            case 2:
                return "M";
            case 1:
                return "k";
            case 0:
                return "";
            case -1:
                return "m";
            default:
                throw new IllegalStateException("Unit must have a precision in the range of -1 and 10 but got " + precision);
        }
    }

    private String longNameStart() {
        switch (precision) {
            case 10:
                return "quetta";
            case 9:
                return "ronna";
            case 8:
                return "yotta";
            case 7:
                return "zetta";
            case 6:
                return "exa";
            case 5:
                return "peta";
            case 4:
                return "tera";
            case 3:
                return "giga";
            case 2:
                return "mega";
            case 1:
                return "kilo";
            case 0:
                return "";
            case -1:
                return "milli";
            default:
                throw new IllegalStateException("Unit must have a precision in the range of -1 and 10 but got " + precision);
        }
    }

    protected abstract String shortNameEnd();

    protected abstract String longNameEnd();

    public static Gandalf getGandalfUnit(float value) {
        return new Gandalf(kiloUsageToPrecision(value));
    }

    public static Merlin getMerlinUnit(float value) {
        return new Merlin(kiloUsageToPrecision(value));
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
