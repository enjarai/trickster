package dev.enjarai.trickster.screen;

import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ModHandledScreens {
    public static void register() {
        HandledScreens.register(ModScreenHandlers.SCROLL_AND_QUILL, ScrollAndQuillScreen::new);
    }
}
