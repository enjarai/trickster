package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.Trickster;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandlers {
    public static final ScreenHandlerType<ScrollAndQuillScreenHandler> SCROLL_AND_QUILL =
            new ScreenHandlerType<>(ScrollAndQuillScreenHandler::new, FeatureSet.empty());

    public static void register() {
        Registry.register(Registries.SCREEN_HANDLER, Trickster.id("scroll_and_quill"), SCROLL_AND_QUILL);
    }
}
