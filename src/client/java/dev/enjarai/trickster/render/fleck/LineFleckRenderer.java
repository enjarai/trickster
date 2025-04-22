package dev.enjarai.trickster.render.fleck;

import dev.enjarai.trickster.Trickster;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import dev.enjarai.trickster.fleck.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class LineFleckRenderer implements FleckRenderer<LineFleck> {
    static final Identifier LINE_TEXTURE = Trickster.id("textures/flecks/line.png");
    private static final float LINE_SEGMENT_WIDTH = 0.1f;
    private static final float LINE_SEGMENT_LENGTH = (float) Math.sqrt(1); //todo its not noticable, but it does stretch, probably because of the uv manip.
    private static final int LINE_ALPHA = 180;

    @Override
    public void render(LineFleck fleck, LineFleck lastFleck, WorldRenderContext context, ClientWorld world, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int color) {

        var pos1 = fleck.pos().get(new Vector3f());
        var pos2 = fleck.pos2().get(new Vector3f());

        if (lastFleck != null) {
            var oldPos1 = lastFleck.pos();
            var oldPos2 = lastFleck.pos2();
            oldPos1.lerp(pos1, tickDelta, pos1);
            oldPos2.lerp(pos2, tickDelta, pos2);
        }

        var offset = pos2.sub(pos1, new Vector3f());
        var step = offset.normalize().mul(LINE_SEGMENT_LENGTH);

        var prev = pos1.get(new Vector3f());
        var cur = pos1.add(step, new Vector3f());

        var buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(LINE_TEXTURE));
        var argb = ColorHelper.Argb.withAlpha(LINE_ALPHA, color);
        var camPos = context.camera().getPos().toVector3f();

        while (pos1.distance(pos2) >= pos1.distance(cur)) {
            drawSegment(prev, cur, LINE_SEGMENT_WIDTH, matrices, buffer, argb, LightmapTextureManager.MAX_LIGHT_COORDINATE, camPos, 1.0f);
            prev.add(step);
            cur.add(step);
        }

        drawSegment(prev, pos2, LINE_SEGMENT_WIDTH, matrices, buffer, argb, LightmapTextureManager.MAX_LIGHT_COORDINATE, camPos, prev.distance(pos2) / LINE_SEGMENT_LENGTH);
    }

    private static void drawSegment(Vector3fc start, Vector3fc end, float width, MatrixStack matrices, VertexConsumer buffer, int argb, int light, Vector3f camPos, float uvfactor) {
        matrices.push();
        matrices.translate(-camPos.x, -camPos.y, -camPos.z);
        matrices.multiplyPositionMatrix(new Matrix4f().billboardCylindrical(
                start, camPos,
                start.sub(end, new Vector3f()).normalize())
        );

        MatrixStack.Entry top = matrices.peek();
        Matrix4f positionMatrix = top.getPositionMatrix();

        var distance = start.distance(end);
        var cornerOffset = 3.0f / 16.0f;
        //distance from the corner of the texture to the corner of the part we tile. 3/16 = 3 pixels
        //if you change the line texture, make sure to change this.

        //                                     bottom left corner                  top left corner
        var topLeftUV = new Vector2f(0, cornerOffset).lerp(new Vector2f(1 - cornerOffset, 1), uvfactor);
        //                                     bottom right corner                 top right corner
        var topRightUV = new Vector2f(cornerOffset, 0).lerp(new Vector2f(1, 1 - cornerOffset), uvfactor);

        buffer.vertex(positionMatrix, width, 0, 0) // top left
                .color(argb)
                .texture(cornerOffset, 0)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(top, 0, 1, 0);

        buffer.vertex(positionMatrix, width, -distance, 0) //top right
                .color(argb)
                .texture(topRightUV.x, topRightUV.y)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(top, 0, 1, 0);

        buffer.vertex(positionMatrix, -width, -distance, 0) //bottom right
                .color(argb)
                .texture(topLeftUV.x, topLeftUV.y)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(top, 0, 1, 0);

        buffer.vertex(positionMatrix, -width, 0, 0)//bottom left
                .color(argb)
                .texture(0, cornerOffset)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(top, 0, 1, 0);

        matrices.pop();
    }
}
