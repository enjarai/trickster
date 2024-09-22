package dev.enjarai.trickster.fleck;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;

import java.util.function.Function;

public class TextFleckRenderer implements FleckRenderer<TextFleck> {
    @Override
    public void render(TextFleck fleck, TextFleck lastFleck, WorldRenderContext context, ClientWorld world, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int color) {

    }
}
