package dev.enjarai.trickster.item;

import dev.enjarai.trickster.item.component.MementoChargeComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.SpellPart;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Optional;

public abstract class MementoItem extends Item {
    public MementoItem() {
        super(new Settings().component(ModComponents.MEMENTO_CHARGE, new MementoChargeComponent(500, 10)));
    }

//    @Override
//    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
//        finishUsing(user.getStackInHand(hand), world, user);
//        return TypedActionResult.consume(user.getStackInHand(hand));
//    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (user instanceof ServerPlayerEntity player) {
            var vel = player.getRotationVector().multiply(remainingUseTicks).multiply((double) 1 / getMaxUseTime(stack, user));
            world.addParticle(ParticleTypes.NOTE, player.getX(), player.getY(), player.getZ(), vel.x, vel.y, vel.z);
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof ServerPlayerEntity player) {
            Optional.ofNullable(stack.get(ModComponents.MEMENTO_CHARGE)).ifPresent(comp -> {
                if (comp.usesLeft() > 0) {
                    Optional.ofNullable(stack.get(ModComponents.SPELL)).ifPresent(spell -> {
                        cast(world, player, spell.spell(), stack, comp.maxMana());
                        comp.use(stack);
                    });
                }
            });
        }

        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        //noinspection DataFlowIssue
        if (stack.get(ModComponents.MEMENTO_CHARGE).usesLeft() > 0)
            return 30;

        return 0;
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        //noinspection DataFlowIssue
        entity.getWorld().createExplosion(null,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                Math.min(entity.getStack().get(ModComponents.MEMENTO_CHARGE).usesLeft(), 10),
                World.ExplosionSourceType.NONE);
    }

    protected abstract void cast(World world, ServerPlayerEntity player, SpellPart spell, ItemStack stack, float maxMana);
}
