package dev.enjarai.trickster.spell.execution.source;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.execution.SpellExecutionManager;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.mana.MutableManaPool;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;

import java.util.*;
import java.util.function.Predicate;

public interface SpellSource {
    default Optional<ServerPlayerEntity> getPlayer() {
        return Optional.empty();
    }

    default Optional<Entity> getCaster() {
        return Optional.empty();
    }

    default Optional<ItemStack> getOtherHandStack() {
        return getOtherHandStack(i -> true);
    }

    default Optional<ItemStack> getOtherHandStack(Predicate<ItemStack> filter) {
        return Optional.empty();
    }

    default Optional<SlotFragment> getOtherHandSlot() {
        return Optional.empty();
    }

    default Optional<SpellExecutionManager> getExecutionManager() {
        return Optional.empty();
    }

    default BlockPos getBlockPos() {
        var pos = getPos();
        return new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
    }

    <T extends Component> Optional<T> getComponent(ComponentKey<T> key);

    float getHealth();

    float getMaxHealth();

    MutableManaPool getManaPool();

    Vector3d getPos();

    ServerWorld getWorld();

    Fragment getCrowMind();

    void setCrowMind(Fragment fragment);
}
