package dev.enjarai.trickster.item.component;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.mana.ManaPool;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public record ManaComponent(ManaPool pool, float naturalRechargeMultiplier, boolean rechargeable) {
    public static final Codec<ManaComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EndecTomfoolery.toCodec(ManaPool.ENDEC).fieldOf("pool").forGetter(ManaComponent::pool),
            Codec.FLOAT.fieldOf("natural_recharge_multiplier").forGetter(ManaComponent::naturalRechargeMultiplier),
            Codec.BOOL.fieldOf("rechargable").forGetter(ManaComponent::rechargeable)
    ).apply(instance, ManaComponent::new));

    public ManaComponent(ManaPool pool) {
        this(pool, 1, true);
    }

    public ManaComponent(ManaPool pool, float naturalRechargeMultiplier) {
        this(pool, naturalRechargeMultiplier, true);
    }

    public ManaComponent with(ManaPool pool) {
        return new ManaComponent(pool, naturalRechargeMultiplier(), rechargeable());
    }

    public static float tryRecharge(World world, Vec3d pos, ItemStack stack) {
        var component = stack.get(ModComponents.MANA);
        if (component == null || component.naturalRechargeMultiplier() <= 0) {
            return 0;
        }

        var pool = component.pool();
        if (pool.get() >= pool.getMax()) {
            return 0;
        }

        // Require nighttime skylight access
        if (world.getLightLevel(LightType.SKY, BlockPos.ofFloored(pos)) < 15 || world.isDay()) {
            return 0;
        }

        var moonSize = world.getMoonSize();
        // Recharge most strongly at new and full moons, with no recharging halfway inbetween
        var chargeMultiplier = Math.max(moonSize * 2 - 1, 1 - moonSize * 2);
        chargeMultiplier *= component.naturalRechargeMultiplier();

        var newPool = pool.makeClone();
        newPool.refill(chargeMultiplier);
        stack.set(ModComponents.MANA, component.with(newPool));

        return chargeMultiplier;
    }
}
