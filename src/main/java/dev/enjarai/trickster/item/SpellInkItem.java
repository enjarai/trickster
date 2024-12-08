package dev.enjarai.trickster.item;

import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.effects.ModEffects;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.consume.UseAction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class SpellInkItem extends Item {
    public SpellInkItem(Settings settings) {
        super(settings
                .component(DataComponentTypes.CONSUMABLE, ConsumableComponents.drink()
                        .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 60 * 20)))
                        .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.POISON, 60 * 20)))
                        .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.GLOWING, 60 * 20)))
                        .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60 * 20)))
                        .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(ModEffects.MANA_BOOST, 60 * 20)))
                        .build())
                .useRemainder(Items.GLASS_BOTTLE));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient()) {
            if (!user.addStatusEffect(new StatusEffectInstance(
                    ModEffects.MANA_BOOST,
                    20 * 60, 0
            ))) user.damage((ServerWorld) world,
                    new DamageSource(user.getRegistryManager().getOrThrow(DamageTypes.MAGIC.getRegistryRef()).getOrThrow(DamageTypes.MAGIC)), 22);
        }

        if (user instanceof ServerPlayerEntity player)
            ModCriteria.DRINK_SPELL.trigger(player);

        return super.finishUsing(stack, world, user);
    }
}
