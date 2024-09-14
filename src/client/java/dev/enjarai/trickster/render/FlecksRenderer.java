package dev.enjarai.trickster.render;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.fleck.Fleck;
import dev.enjarai.trickster.fleck.LineFleck;
import dev.enjarai.trickster.fleck.SpellFleck;
import dev.enjarai.trickster.fleck.TextFleck;
import dev.enjarai.trickster.spell.SpellPart;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3dc;

public class FlecksRenderer {
    private static final Random colorsRandom = new LocalRandom(0xba115);

    public static void render(WorldRenderContext worldRenderContext) {
        var player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }

        player.getComponent(ModEntityCumponents.FLECKS).getFlecks().forEach((id, pair) -> {
            Fleck fleck = pair.getFirst();
            // TODO delegate to the renderer
        });
    }
}
