package dev.enjarai.trickster.spell.execution.source;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;

import java.util.*;
import java.util.function.Function;

public abstract class SpellSource {
    public abstract ManaPool getManaPool();

    public Optional<ServerPlayerEntity> getPlayer() {
        return Optional.empty();
    }

    public Optional<Entity> getCaster() {
        return Optional.empty();
    }

    public Optional<ItemStack> getOtherHandSpellStack() {
        return Optional.empty();
    }

    public float getMana() {
        return getManaPool().get();
    }

    public float getMaxMana() {
        return getManaPool().getMax();
    }

    public abstract float getHealth();

    public abstract float getMaxHealth();

    public abstract Vector3d getPos();

    public BlockPos getBlockPos() {
        var pos = getPos();
        return new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
    }

    public abstract ServerWorld getWorld();

    public abstract Fragment getCrowMind();

    public abstract void setCrowMind(Fragment fragment);
}
