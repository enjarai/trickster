package dev.enjarai.trickster.fleck;

import dev.enjarai.trickster.Trickster;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;


public class LineFleckRenderer implements FleckRenderer<LineFleck> {
    static final Identifier LINE_TEXTURE = Trickster.id("textures/flecks/line.png");
    private static final float LINE_SEGMENT_WIDTH = 0.3f;
    private static final float LINE_SEGMENT_LENGTH = (float) (LINE_SEGMENT_WIDTH); //todo its not noticable, but it does stretch, probably because of the uv manip.
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

        drawSegment(prev, pos2, LINE_SEGMENT_WIDTH, matrices, buffer, argb, LightmapTextureManager.MAX_LIGHT_COORDINATE, camPos, prev.distance(pos2)/LINE_SEGMENT_LENGTH);
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

        uvfactor = 1f;
        /* todo figure out how to fix the uv's of the last segment so they arent squished

        idea: uvfactor = segment length / step_size
        if the segment length is half the step size, the uv's would be twice as squished

        so we sample from half the texture.
        sliding the top left and top right of the uv bounding box down the length of the texture toward the bottom left and bottom right.
        so when the segment length is half the stepsize, then bounding box "height" would be half the texture length

        but the aspect ratio of the sampled rectangle in the uv's widens, (since were making it shorter, it becomes wider relative to its length)
        so we need to narrow the quad itself (width *= uvfactor) (uvfactor is always <1.0f)

        but it also slides out to the side (X axis in this billboarded space). and i dunno why
         */
        var topLeftUV = new Vector2f(cornerOffset, 0).lerp(new Vector2f(1 - cornerOffset, 1), uvfactor);
        var topRightUV = new Vector2f(0, cornerOffset).lerp(new Vector2f(1, 1 - cornerOffset), uvfactor);
        width *= uvfactor;

        buffer.vertex(positionMatrix, width, 0, 0) // top left
                .color(argb)
                .texture(topLeftUV.x, topLeftUV.y)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(top, 0, 1, 0);

        buffer.vertex(positionMatrix, width, -distance, 0) //top right
                .color(argb)
                .texture(topRightUV.x, topRightUV.y)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(top, 0, 1, 0);

        buffer.vertex(positionMatrix, -width, -distance, 0) //bottom right
                .color(argb)
                .texture(cornerOffset, 0)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(top, 0, 1, 0);

        buffer.vertex(positionMatrix, -width, 0, 0)//bottom left
                .color(argb)
                .texture(0, cornerOffset)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(top, 0, 1, 0);

        matrices.pop();
    }
}
