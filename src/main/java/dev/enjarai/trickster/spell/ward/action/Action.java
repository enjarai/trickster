package dev.enjarai.trickster.spell.ward.action;

import dev.enjarai.trickster.spell.Source;

public abstract class Action<T extends Target> {
    public final Source source;

    public Action(Source source) {
        this.source = source;
    }

    public abstract ActionType<?> type();

    public abstract T target();

    public abstract float cost();
}
