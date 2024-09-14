package dev.enjarai.trickster.render;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.fleck.Fleck;
import dev.enjarai.trickster.fleck.LineFleck;
import dev.enjarai.trickster.fleck.SpellFleck;
import dev.enjarai.trickster.fleck.TextFleck;
import dev.enjarai.trickster.item.component.ModComponents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3dc;

import java.util.Optional;
import java.util.UUID;

public class FlecksRenderer {
    private static final Random colorsRandom = new LocalRandom(0xba115);
    private final ClientPlayerEntity player = MinecraftClient.getInstance().player;

    public static void render(WorldRenderContext worldRenderContext) {
        assert player != null;
        player.getComponent(ModEntityCumponents.FLECKS).getFlecks().forEach(
                (id, pair) -> {
                    Fleck fleck = pair.getFirst();
                    switch (fleck) {
                        case LineFleck(Vector3dc pos1, Vector3dc pos2) -> render_line(pos1, pos2);
                        case TextFleck(Vector3dc pos, Vector3dc facing, Text text) -> render_text(pos, facing, text);
                        case
                    }
                }

        );
    }

}
