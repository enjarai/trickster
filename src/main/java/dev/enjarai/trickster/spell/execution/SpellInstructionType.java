package dev.enjarai.trickster.spell.execution;

public enum SpellInstructionType {
    FRAGMENT(1), ENTER_SCOPE(2), EXIT_SCOPE(3);

    private final int id;

    SpellInstructionType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static SpellInstructionType fromId(int id) {
        return switch (id) {
            case 1 -> FRAGMENT;
            case 2 -> ENTER_SCOPE;
            case 3 -> EXIT_SCOPE;
            default -> throw new IllegalArgumentException("Unexpected spell instruction type id: " + id);
        };
    }

}
