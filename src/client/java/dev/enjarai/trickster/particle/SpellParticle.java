package dev.enjarai.trickster.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SpellParticle extends SpriteBillboardParticle {
    protected SpellParticle(ClientWorld world, double x, double y, double z, double dx, double dy, double dz, int color) {
        super(world, x, y, z);
        //        maxAge = 10;
        //        alpha = 0.6f;
        red = ColorHelper.Argb.getRed(color) / 255f;
        green = ColorHelper.Argb.getGreen(color) / 255f;
        blue = ColorHelper.Argb.getBlue(color) / 255f;
        velocityX = dx;
        velocityY = dy;
        velocityZ = dz;
        maxAge = 40 + (int) (4.0F / (random.nextFloat() * 0.9F + 0.1F));
        scale = 0.2f;
    }

    @Override
    public void tick() {
        super.tick();
        scale = 0.2f * ((maxAge - (float) age) / maxAge);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getBrightness(float tint) {
        return LightmapTextureManager.MAX_LIGHT_COORDINATE;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SpellParticleOptions> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SpellParticleOptions options, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            var particle = new SpellParticle(clientWorld, d, e, f, g, h, i, options.color);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }
}
