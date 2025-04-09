package dev.enjarai.trickster.imgui;

import imgui.ImFontAtlas;
import imgui.ImFontGlyphRangesBuilder;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class ImGuiEvents {
    public static final Event<SetupFontRangesEvent> SETUP_FONT_RANGES = EventFactory.createArrayBacked(SetupFontRangesEvent.class, events -> (builder) -> {
        for (SetupFontRangesEvent event : events) {
            event.onSetup(builder);
        }
    });

    public interface SetupFontRangesEvent {
        void onSetup(ImFontGlyphRangesBuilder builder);
    }

    public static final Event<SetupFontsEvent> SETUP_FONTS = EventFactory.createArrayBacked(SetupFontsEvent.class, events -> (atlas, glyphRanges) -> {
        for (SetupFontsEvent event : events) {
            event.onSetup(atlas, glyphRanges);
        }
    });

    public interface SetupFontsEvent {
        void onSetup(ImFontAtlas atlas, short[] glyphRanges);
    }
}
