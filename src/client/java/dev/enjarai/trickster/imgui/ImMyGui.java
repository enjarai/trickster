package dev.enjarai.trickster.imgui;

import imgui.*;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import nl.enjarai.cicada.Cicada;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ImMyGui {
    private static boolean initialized = false;
    private static boolean errored = false;
    private static ImGuiImplGl3 imguiGl3;
    private static ImGuiImplGlfw imguiGlfw;

    private static ImFont font;

    @ApiStatus.Internal
    public static void init(long window) {
        if (initialized && !errored) {
            return;
        }

        try {
            imguiGl3 = new ImGuiImplGl3();
            imguiGlfw = new ImGuiImplGlfw();

            ImGui.createContext();
            var io = ImGui.getIO();
            io.setIniFilename(null);
            io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
//            io.setGetClipboardTextFn(new ImStrSupplier() {
//                @Override
//                public String get() {
//                    return MinecraftClient.getInstance().keyboard.getClipboard();
//                }
//            });
//            io.setSetClipboardTextFn(new ImStrConsumer() {
//                @Override
//                public void accept(String s) {
//                    MinecraftClient.getInstance().keyboard.setClipboard(s);
//                }
//            });
//            io.setWantCaptureKeyboard(true);

            imguiGlfw.init(window, true);
            imguiGl3.init();

            ImFontAtlas fontAtlas = io.getFonts();
            ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated object, should be explicitly destroyed
            ImFontGlyphRangesBuilder glyphRangesBuilder = new ImFontGlyphRangesBuilder();

            glyphRangesBuilder.addRanges(fontAtlas.getGlyphRangesDefault());
            glyphRangesBuilder.addChar('…');

            ImGuiEvents.SETUP_FONT_RANGES.invoker().onSetup(glyphRangesBuilder);

            fontConfig.setOversampleH(2);
            fontConfig.setOversampleV(2);

            fontConfig.setName("Inter (Medium), 16px");
            fontConfig.setGlyphOffset(0, 0);

            var glyphRanges = glyphRangesBuilder.buildRanges();

            // Don't you even dare look at this code funny or it WILL break
            font = fontAtlas.addFontFromMemoryTTF(
                    loadFromResources("/font/inter-medium.ttf"), 16,
                    fontConfig, glyphRanges
            );

            ImGuiEvents.SETUP_FONTS.invoker().onSetup(fontAtlas, glyphRanges);

            fontAtlas.build();
            fontConfig.destroy();

            imguiGl3.updateFontsTexture();

            initialized = true;
        } catch (Throwable e) {
            Cicada.LOGGER.error("Failed to load ImGui. Are we missing platform binaries? Some dependent mods may not work as expected.", e);
            errored = true;
        }
    }

    public static String unpackFile(String path) {
        try {
            //noinspection DataFlowIssue
            return Files.copy(
                    Paths.get(ImMyGui.class.getResource(path).toURI()),
                    Files.createTempFile("cicada-", ""),
                    StandardCopyOption.REPLACE_EXISTING
            ).toString();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] loadFromResources(String name) {
        try {
            //noinspection DataFlowIssue
            return Files.readAllBytes(Paths.get(ImMyGui.class.getResource(name).toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @ApiStatus.Internal
    public static void destroy() {
        if (!initialized) {
            return;
        }

        imguiGl3.dispose();
        imguiGlfw.dispose();

        initialized = false;
    }

    public static void render(ImGuiThing thing) {
        if (!initialized || errored) {
            return;
        }

        try {
            imguiGlfw.newFrame();
            ImGui.newFrame();
            ImGui.pushFont(font);

            thing.render();

            ImGui.popFont();
            ImGui.render();
            imguiGl3.renderDrawData(ImGui.getDrawData());

            if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
                long backupCurrentContext = GLFW.glfwGetCurrentContext();
                ImGui.updatePlatformWindows();
                ImGui.renderPlatformWindowsDefault();
                GLFW.glfwMakeContextCurrent(backupCurrentContext);
            }
        } catch (Throwable e) {
            Cicada.LOGGER.error("Failed to render ImGui. Will stop trying for now. Some dependent mods may not work as expected.", e);
            errored = true;
        }
    }

    public static boolean shouldCancelGameKeyboardInputs() {
        return ImGui.isAnyItemActive() || ImGui.isAnyItemFocused();
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static boolean isErrored() {
        return errored;
    }
}
