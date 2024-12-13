package dev.enjarai.trickster.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ProtectedBlockParticle extends SpriteBillboardParticle {
    public static float QUARTER = (float) (Math.PI / 2);

    protected ProtectedBlockParticle(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z);
        maxAge = 10;
        scale = 1 / 16f * 8.5f;
        alpha = 0.6f;
        red = 0.6f;
        green = 0.6f;
        blue = 1f;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        var scale = getSize(tickDelta);
        var rotation = new Quaternionf();
        renderFace(vertexConsumer, camera, rotation, tickDelta, 0, 0, scale);
        renderFace(vertexConsumer, camera, rotation.rotateLocalY(QUARTER), tickDelta, scale, 0, 0);
        renderFace(vertexConsumer, camera, rotation.rotateLocalY(QUARTER), tickDelta, 0, 0, -scale);
        renderFace(vertexConsumer, camera, rotation.rotateLocalY(QUARTER), tickDelta, -scale, 0, 0);
        renderFace(vertexConsumer, camera, rotation.rotateLocalZ(QUARTER), tickDelta, 0, -scale, 0);
        renderFace(vertexConsumer, camera, rotation.rotateLocalZ(QUARTER * 2), tickDelta, 0, scale, 0);
    }

    protected void renderFace(
            VertexConsumer vertexConsumer, Camera camera, Quaternionf quaternionf, float tickDelta, float offsetX, float offsetY,
            float offsetZ
    ) {
        Vec3d vec3d = camera.getPos();
        float g = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - vec3d.getX() + offsetX);
        float h = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - vec3d.getY() + offsetY);
        float i = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - vec3d.getZ() + offsetZ);
        this.renderFaceVertices(vertexConsumer, quaternionf, g, h, i, tickDelta);
    }

    protected void renderFaceVertices(VertexConsumer vertexConsumer, Quaternionf quaternionf, float f, float g, float h, float tickDelta) {
        float j = this.getSize(tickDelta);
        float k = this.getMinU();
        float l = this.getMaxU();
        float m = this.getMinV();
        float n = this.getMaxV();
        int o = this.getBrightness(tickDelta);
        float alpha = getAlpha(tickDelta);
        this.renderVertex(vertexConsumer, quaternionf, f, g, h, 1.0F, -1.0F, j, l, n, o, alpha);
        this.renderVertex(vertexConsumer, quaternionf, f, g, h, 1.0F, 1.0F, j, l, m, o, alpha);
        this.renderVertex(vertexConsumer, quaternionf, f, g, h, -1.0F, 1.0F, j, k, m, o, alpha);
        this.renderVertex(vertexConsumer, quaternionf, f, g, h, -1.0F, -1.0F, j, k, n, o, alpha);
    }

    private void renderVertex(
            VertexConsumer vertexConsumer, Quaternionf quaternionf, float f, float g, float h, float i, float j, float k, float l, float m, int n,
            float alpha
    ) {
        Vector3f vector3f = new Vector3f(i, j, 0.0F).rotate(quaternionf).mul(k).add(f, g, h);
        vertexConsumer.vertex(vector3f.x(), vector3f.y(), vector3f.z()).texture(l, m).color(this.red, this.green, this.blue, alpha).light(n);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public float getAlpha(float tickDelta) {
        return Math.max(0, alpha - ((age + tickDelta) / (float) (maxAge) * alpha));
    }

    @Override
    protected int getBrightness(float tint) {
        return LightmapTextureManager.MAX_LIGHT_COORDINATE;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(
                SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h,
                double i
        ) {
            var particle = new ProtectedBlockParticle(clientWorld, d, e, f);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }
}
