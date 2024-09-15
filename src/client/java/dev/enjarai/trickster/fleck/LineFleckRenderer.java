package dev.enjarai.trickster.fleck;

import dev.enjarai.trickster.Trickster;
import io.wispforest.owo.ui.core.Color;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class LineFleckRenderer implements FleckRenderer<LineFleck> {
    static final Identifier LINE_TEXTURE = Trickster.id("textures/gui/line.png");
    private static final float LINE_SEGMENT_LENGTH = 1.0F;

    @Override
    public void render(LineFleck fleck, WorldRenderContext context, ClientWorld world, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int color) {
        var pos1 = fleck.pos1();
        var pos2 = fleck.pos2();


    }

    private static void drawSegment(WorldRenderContext context, Vector3fc start, Vector3fc end, float width, MatrixStack matrices, VertexConsumer buffer, Color color) {
        var length = start.distance(end);
        var offset = 0.0F; //TODO use tick delta to implement wobbling

        var camPos = context.camera().getPos().toVector3f();

        matrices.push();
        matrices.multiplyPositionMatrix(new Matrix4f().billboardCylindrical(
                start,
                camPos,
                start.sub(end, new Vector3f())
        ));

        MatrixStack.Entry top = matrices.peek();
        Matrix4f positionMatrix = top.getPositionMatrix();

        int argb = color.argb();

        //todo figure out how to fix the uv's of the last segment

        buffer.vertex(positionMatrix, width, 0, 0)
                .color(argb)
                .texture(offset, 1)
                .normal(top, 0, 1, 0);

        buffer.vertex(positionMatrix, width, 1, 0)
                .color(argb)
                .texture(-(length - offset), 1)
                .normal(top, 0, 1, 0);

        buffer.vertex(positionMatrix, -width, 1, 0)
                .color(argb)
                .texture(-(length - offset), 0)
                .normal(top, 0, 1, 0);

        buffer.vertex(positionMatrix, -width, 0, 0)
                .color(argb)
                .texture(offset, 0)
                .normal(top, 0, 1, 0);

        matrices.pop();
    }
}
