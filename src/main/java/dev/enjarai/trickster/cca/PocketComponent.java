package dev.enjarai.trickster.cca;

import dev.enjarai.trickster.spell.SpellContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.ladysnake.cca.api.v3.component.Component;

import java.util.Optional;

public class PocketComponent implements Component {
    private final PlayerEntity player;

    public ItemStack getPocketed() {
        return pocketed;
    }

    public void setPocketed(ItemStack pocketed) {
        this.pocketed = pocketed;
    }

    private ItemStack pocketed;

    public PocketComponent(PlayerEntity player) {
        this.player = player;
        this.pocketed = ItemStack.EMPTY;
    }


    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        var serialized = tag.getCompound("wristpocket");
        pocketed = ItemStack.fromNbtOrEmpty(registryLookup, serialized);
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        var serialized = pocketed.encodeAllowEmpty(registryLookup);
        tag.put("wristpocket", serialized);
    }

    public void put(ItemStack item, SpellContext ctx) {
        drop(ctx);
        pocketed = item;
    }

    public void drop(SpellContext ctx) {
        ctx.source().offerOrDropItem(pocketed);
    }

    public void useOnBlock(BlockPos pos, Optional<Direction> face, Optional<Boolean> crouch, ServerWorld world) {
        useOnBlock(
            pos,
            face.orElse(Direction.UP),
            crouch.orElse(false),
            world
        );
    }

    public void useOnBlock(BlockPos pos, Direction dir, Boolean crouch, ServerWorld world) {
        var originalItem = player.getStackInHand(Hand.MAIN_HAND);
        var blockHitResult = new BlockHitResult(Vec3d.ofCenter(pos), dir, pos, false);
        var sneakState = player.isSneaking();

        player.setSneaking(crouch);
        player.setStackInHand(Hand.MAIN_HAND, pocketed);

        world.getBlockState(pos).onUse(world, player, blockHitResult);
        pocketed.useOnBlock(new ItemUsageContext(player, Hand.MAIN_HAND, blockHitResult));
        pocketed = pocketed.finishUsing(world, player);

        player.setSneaking(sneakState);
        player.setStackInHand(Hand.MAIN_HAND, originalItem);
    }

    public void useOnEntity(Entity entity, Optional<Boolean> crouch, World world) {
        useOnEntity(
            entity,
            crouch.orElse(false),
            world
        );
    }

    public void useOnEntity(Entity entity, Boolean crouch, World world) {
        var originalItem = player.getStackInHand(Hand.MAIN_HAND);
        var sneakState = player.isSneaking();

        player.setSneaking(crouch);
        player.setStackInHand(Hand.MAIN_HAND, pocketed);

        entity.interact(player, Hand.MAIN_HAND);
        if (entity instanceof LivingEntity livingEntity) {
            pocketed.useOnEntity(player, livingEntity, Hand.MAIN_HAND);
        }

        pocketed = pocketed.finishUsing(world, player);
        player.setStackInHand(Hand.MAIN_HAND, originalItem);
        player.setSneaking(sneakState);
    }

    public void attack(Entity entity, boolean crouch) {
        var originalItem = player.getStackInHand(Hand.MAIN_HAND);
        var sneakState = player.isSneaking();

        player.setSneaking(crouch);
        player.setStackInHand(Hand.MAIN_HAND, pocketed);

        player.attack(entity);
        if (entity instanceof LivingEntity livingEntity) {
            pocketed.postHit(livingEntity, player);
        }

        player.setSneaking(sneakState);
        player.setStackInHand(Hand.MAIN_HAND, originalItem);
    }
}
