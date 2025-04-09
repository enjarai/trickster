package dev.enjarai.trickster.imgui;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

public class ImGuiThings {
    private static final List<ImGuiThing> things = new ArrayList<>();

    public static void add(ImGuiThing thing) {
        things.add(thing);
    }

    @ApiStatus.Internal
    public static void renderAll() {
        ImMyGui.render(() -> things.forEach(ImGuiThing::render));
    }
}
