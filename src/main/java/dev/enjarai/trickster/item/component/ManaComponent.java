package dev.enjarai.trickster.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.mana.ManaPool;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

public record ManaComponent(ManaPool pool, float naturalRechargeMultiplier) {
    public static final Codec<ManaComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EndecTomfoolery.toCodec(ManaPool.ENDEC).fieldOf("pool").forGetter(ManaComponent::pool),
            Codec.FLOAT.fieldOf("natural_recharge_multiplier").forGetter(ManaComponent::naturalRechargeMultiplier)
    ).apply(instance, ManaComponent::new));

    public ManaComponent(ManaPool pool) {
        this(pool, 0);
    }

    public ManaComponent with(ManaPool pool) {
        return new ManaComponent(pool, naturalRechargeMultiplier());
    }

    public static float tryRecharge(ServerWorld world, Vec3d pos, ItemStack stack) {
        var component = stack.get(ModComponents.MANA);
        if (component == null || component.naturalRechargeMultiplier() <= 0) {
            return 0;
        }

        var pool = component.pool();
        if (pool.get(world) >= pool.getMax(world)) {
            return 0;
        }

        // Require nighttime skylight access
        var noSky = world.getLightLevel(LightType.SKY, BlockPos.ofFloored(pos)) < 15;
        if (noSky) {
            return 0;
        }

        if (world.isDay() && !world.isThundering()) {
            return 0;
        }

        var moonSize = world.getMoonSize();
        // Recharge most strongly at new and full moons, with no recharging halfway inbetween
        var chargeMultiplier = Math.max(Math.max(moonSize * 2 - 1, 1 - moonSize * 2), world.isThundering() ? 4 : 0);
        chargeMultiplier *= component.naturalRechargeMultiplier();

        var newPool = pool.makeClone(world);
        newPool.refill(chargeMultiplier, world);
        stack.set(ModComponents.MANA, component.with(newPool));

        var particleVelocity = world.isThundering() ? 4 : 1;

        var recharged = chargeMultiplier - world.random.nextFloat();
        while (recharged > 0) {
            world.spawnParticles(
                    ModParticles.SPELL_WHITE,
                    pos.getX(), pos.getY(), pos.getZ(), 0,
                    (world.random.nextFloat() * 0.005f - 0.0025f) * particleVelocity,
                    (world.random.nextFloat() * 0.02f + 0.01f) * particleVelocity,
                    (world.random.nextFloat() * 0.005f - 0.0025f) * particleVelocity,
                    1
            );
            recharged -= world.random.nextFloat();
        }

        return chargeMultiplier;
    }
}
