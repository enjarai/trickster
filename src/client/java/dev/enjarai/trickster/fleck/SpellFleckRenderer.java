package dev.enjarai.trickster.fleck;

import dev.enjarai.trickster.render.SpellCircleRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class SpellFleckRenderer implements FleckRenderer<SpellFleck> {
    private final SpellCircleRenderer renderer = new SpellCircleRenderer(false, 1);

    @Override
    public void render(SpellFleck fleck, WorldRenderContext context, ClientWorld world, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int color) {
        matrices.push();

        var position = fleck.pos();
        var facing = fleck.facing();
        var spell = fleck.spell();

        var target_position = position.sub(context.camera().getPos().toVector3f(), new Vector3f());
        var yaw = (float) Math.atan2(facing.x(), facing.z());
        var pitch = (float) (Math.asin(-facing.y()) + Math.PI);

        matrices.translate(target_position.x(), target_position.y(), target_position.z()); //translate over to the position of the spellfleck.
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotation(pitch));

        //TODO WOBBLE

        renderer.renderPart(
            matrices,
            vertexConsumers,
            spell,
            0,
            0,
            0.5,
            0,
            tickDelta,
            size -> 1.0f,
            new Vec3d(facing.x(), facing.y(), facing.z())
        );

        matrices.pop();
    }
}
