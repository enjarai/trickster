package dev.enjarai.trickster.item;

import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.effects.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class SpellInkItem extends Item {
    public SpellInkItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public SoundEvent getEatSound() {
        return SoundEvents.ENTITY_GENERIC_DRINK;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient()) {
            if (
                !user.addStatusEffect(
                        new StatusEffectInstance(
                                ModEffects.MANA_BOOST,
                                20 * 60, 0
                        )
                )
            )
                user.damage(new DamageSource(user.getRegistryManager().get(DamageTypes.MAGIC.getRegistryRef()).entryOf(DamageTypes.MAGIC)), 22);
        }

        if (user instanceof ServerPlayerEntity player)
            ModCriteria.DRINK_SPELL.trigger(player);

        return super.finishUsing(stack, world, user);
    }
}
