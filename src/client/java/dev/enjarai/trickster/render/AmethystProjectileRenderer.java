package dev.enjarai.trickster.render;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.entity.AmethystProjectile;
import dev.enjarai.trickster.entity.SpellRunningState;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.RotationCalculator;
import net.minecraft.util.math.Vec3d;

public class AmethystProjectileRenderer extends ProjectileEntityRenderer<AmethystProjectile> {

    private final SpellCircleRenderer spellCircleRenderer;

    public AmethystProjectileRenderer(EntityRendererFactory.Context context) {
        super(context);
        spellCircleRenderer = new SpellCircleRenderer(false, 1);
    }

    @Override
    public Identifier getTexture(AmethystProjectile entity) {
        return Trickster.id("textures/entity/projectile/shard_projectile.png");
    }

    @Override
    public void render(AmethystProjectile projectile, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(projectile, f, g, matrixStack, vertexConsumerProvider, i);

        if (projectile.getRunningState() instanceof SpellRunningState.Running running) {
            matrixStack.push();
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(g, projectile.prevYaw, projectile.getYaw())));
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-MathHelper.lerp(g, projectile.prevPitch, projectile.getPitch())));
            matrixStack.translate(0,  0, -AmethystProjectile.dimensions);

            spellCircleRenderer.renderPart(matrixStack, vertexConsumerProvider, running.spellPart(), 0, 0, 1/3.0, 0, 1.0f, (depth) -> 1.0f, new Vec3d(0, 0, 1));
            matrixStack.pop();
        }
    }
}
