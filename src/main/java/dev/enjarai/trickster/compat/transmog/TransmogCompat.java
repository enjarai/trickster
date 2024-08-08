package dev.enjarai.trickster.compat.transmog;

import dev.enjarai.trickster.spell.trick.Tricks;

public class TransmogCompat {
    public static final TransmogTrick TRANSMOG = Tricks.register("transmog", new TransmogTrick());
    public static final HiddenTransmogTrick HIDDEN_TRANSMOG = Tricks.register("hidden_transmog", new HiddenTransmogTrick());
    public static final RemoveTransmogTrick REMOVE_TRANSMOG = Tricks.register("remove_transmog", new RemoveTransmogTrick());
    public static final GetTransmogTrick GET_TRANSMOG = Tricks.register("get_transmog", new GetTransmogTrick());

    public static void init() {

    }
}
