package dev.enjarai.trickster.screen;

import dev.enjarai.trickster.Trickster;
import io.wispforest.owo.serialization.CodecUtils;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandlers {
    public static final ExtendedScreenHandlerType<ScrollAndQuillScreenHandler, ScrollAndQuillScreenHandler.InitialData> SCROLL_AND_QUILL = new ExtendedScreenHandlerType<>(
        ScrollAndQuillScreenHandler::new, CodecUtils.toPacketCodec(ScrollAndQuillScreenHandler.InitialData.ENDEC));
    public static final ScreenHandlerType<ScrollContainerScreenHandler> SCROLL_CONTAINER = new ScreenHandlerType<>(ScrollContainerScreenHandler::new, FeatureSet.empty());

    public static void register() {
        Registry.register(Registries.SCREEN_HANDLER, Trickster.id("scroll_and_quill"), SCROLL_AND_QUILL);
        Registry.register(Registries.SCREEN_HANDLER, Trickster.id("scroll_container"), SCROLL_CONTAINER);
    }
}
