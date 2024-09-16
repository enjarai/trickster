package dev.enjarai.trickster.fleck;

import dev.enjarai.trickster.Trickster;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.stream.IntStream;

public class LineFleckRenderer implements FleckRenderer<LineFleck> {
    static final Identifier LINE_TEXTURE = Trickster.id("textures/line.png");
    private static final float LINE_SEGMENT_LENGTH = 1.0F;

    @Override
    public void render(LineFleck fleck, LineFleck lastFleck, WorldRenderContext context, ClientWorld world, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int color) {

        var pos1 = fleck.pos();
        var pos2 = fleck.pos2();
        var offset = pos2.sub(pos1, new Vector3f());

        var prev = new Vector3f();
        var cur = new Vector3f();

        IntStream.range(0, (int) Math.ceil(pos1.distance(pos2))).reduce((a,b) ->
            drawSegment(context,offset.mul(a, prev),offset.mul(b, cur), 0.1f, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(LINE_TEXTURE)), 0)
        );
    }

    private static int drawSegment(WorldRenderContext context, Vector3fc start, Vector3fc end, float width, MatrixStack matrices, VertexConsumer buffer, int argb) {
        var length = start.distance(end);
        var offset = 0.0F; //TODO use implement wobbling

        var camPos = context.camera().getPos().toVector3f();

        matrices.push();
        matrices.multiplyPositionMatrix(new Matrix4f().billboardCylindrical(
                start, camPos,
                start.sub(end, new Vector3f())
        ));

        MatrixStack.Entry top = matrices.peek();
        Matrix4f positionMatrix = top.getPositionMatrix();

        //todo figure out how to fix the uv's of the last segment so they arent squished

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
        
        return 0;
    }
}
