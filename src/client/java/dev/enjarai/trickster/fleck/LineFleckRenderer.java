package dev.enjarai.trickster.fleck;

import dev.enjarai.trickster.Trickster;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;


public class LineFleckRenderer implements FleckRenderer<LineFleck> {
static final Identifier LINE_TEXTURE = Trickster.id("textures/flecks/line.png");
private static final float LINE_SEGMENT_LENGTH = 1.0F;
private static final SimplexNoiseSampler sampler = new SimplexNoiseSampler(Random.create());

    @Override
    public void render(LineFleck fleck, @Nullable LineFleck lastFleck, WorldRenderContext context, ClientWorld world, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int color) {

        var pos1 = fleck.pos();
        var pos2 = fleck.pos2();
        var offset = pos2.sub(pos1, new Vector3f());
        var step = offset.normalize().mul(LINE_SEGMENT_LENGTH);

        var prev = pos1.get(new Vector3f());
        var cur = pos1.add(step, new Vector3f());

        drawSegment(context,pos1,pos2,0.1f,matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(LINE_TEXTURE)), color, LightmapTextureManager.MAX_LIGHT_COORDINATE);


        var buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(LINE_TEXTURE));
        while (pos1.distance(pos2) > cur.add(step).distance(pos2)) {
            drawSegment(context, prev, cur, 0.1F, matrices, buffer, color, LightmapTextureManager.MAX_LIGHT_COORDINATE);
            prev.add(step);
        }
        drawSegment(context, cur, pos2, 0.1F, matrices, buffer, color, LightmapTextureManager.MAX_LIGHT_COORDINATE);

//        IntStream.range(0, (int) Math.ceil(pos1.distance(pos2)/LINE_SEGMENT_LENGTH)).reduce((a,b) ->
//            drawSegment(
//                    context,
//                    offset.mul(a, prev),
//                    offset.mul(b, cur),
//                    0.1f,
//                    matrices,
//                    vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(LINE_TEXTURE)),
//                    0,
//                    LightmapTextureManager.MAX_LIGHT_COORDINATE)
//        ); it doesnt work :c
    }

    private static int drawSegment(WorldRenderContext context, Vector3fc start, Vector3fc end, float width, MatrixStack matrices, VertexConsumer buffer, int argb, int light) {

//        var time = MinecraftClient.getInstance().world.getTime();
//        var noise = Math.sin(2 * time) + Math.sin(Math.PI * time) + sampler.sample(start.x(), start.y(), start.z());
//        var dx = (float) Math.sin(noise);
//        var dy = (float) Math.cos(noise);

        var length = start.distance(end);
        var camPos = context.camera().getPos().toVector3f();

        matrices.push();
        matrices.translate(-camPos.x, -camPos.y, -camPos.z);
        matrices.multiplyPositionMatrix(new Matrix4f().billboardCylindrical(
                start, camPos,
                start.sub(end, new Vector3f()).mul(1/2f))
        );

        MatrixStack.Entry top = matrices.peek();
        Matrix4f positionMatrix = top.getPositionMatrix();

        //todo figure out how to fix the uv's of the last segment so they arent squished

        buffer.vertex(positionMatrix, width , 0, 0) //1,0
                .color(argb)
                .texture(0, 1)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(top, 0, 1, 0);

        buffer.vertex(positionMatrix, width, -1, 0) //1,1
                .color(argb)
                .texture(1,1)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(top, 0, 1, 0);

        buffer.vertex(positionMatrix, -width , -1, 0 ) //-1,1
                .color(argb)
                .texture(1, 0)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(top, 0, 1, 0);

        buffer.vertex(positionMatrix, -width , 0, 0)//,0
                .color(argb)
                .texture(0, 0)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(top, 0, 1, 0);

        matrices.pop();
        
        return 0;
    }
}
