package dev.enjarai.trickster.compat.pehkui;

import dev.enjarai.trickster.spell.tricks.Tricks;

public class PehkuiCompat {
    public static final GetScaleTrick GET_SCALE = Tricks.register("get_scale", new GetScaleTrick());
    public static final SetScaleTrick SET_SCALE = Tricks.register("set_scale", new SetScaleTrick());

    public static void init() {

    }
}
