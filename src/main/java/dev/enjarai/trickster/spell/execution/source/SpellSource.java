package dev.enjarai.trickster.spell.execution.source;

import com.mojang.authlib.GameProfile;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.execution.SpellExecutionManager;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.mana.ManaPool;
import eu.pb4.common.protection.api.CommonProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;

import java.util.*;
import java.util.function.Predicate;

public abstract class SpellSource {
    public abstract ManaPool getManaPool();

    public Optional<ServerPlayerEntity> getPlayer() {
        return Optional.empty();
    }

    public Optional<Entity> getCaster() {
        return Optional.empty();
    }

    public Optional<ItemStack> getOtherHandStack() {
        return getOtherHandStack(i -> true);
    }

    public Optional<ItemStack> getOtherHandStack(Predicate<ItemStack> filter) {
        return Optional.empty();
    }

    public Optional<SlotFragment> getOtherHandSlot() {
        return Optional.empty();
    }

    public Optional<SpellExecutionManager> getExecutionManager() {
        return Optional.empty();
    }

    public abstract <T extends Component> Optional<T> getComponent(ComponentKey<T> key);

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

    public GameProfile getResponsibleProfile() {
        return getPlayer().map(PlayerEntity::getGameProfile).orElse(CommonProtection.UNKNOWN);
    }
}
