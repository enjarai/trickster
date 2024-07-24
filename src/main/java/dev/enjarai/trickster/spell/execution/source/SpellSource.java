package dev.enjarai.trickster.spell.execution.source;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.mana.ManaLink;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;

import java.util.*;
import java.util.function.Function;

public abstract class SpellSource {
    protected final List<ManaLink> manaLinks = new ArrayList<>();

//    protected SpellSource(List<ManaLink> manaLinks) {
//        this.manaLinks.addAll(manaLinks);
//    }

    public abstract ManaPool getManaPool();

    // I am disappointed in myself for having written this.
    // Maybe I'll clean it up one day. -- Aurora D.
    public ItemStack getStack(Trick source, Optional<SlotFragment> optionalSlot, Function<Item, Boolean> validator) throws BlunderException {
        ItemStack result = null;

        if (optionalSlot.isPresent()) {
            if (!validator.apply(optionalSlot.get().getItem(source, this))) throw new ItemInvalidBlunder(source);
            result = optionalSlot.get().move(source, this);
        } else {
            var player = this.getPlayer().orElseThrow(() -> new NoPlayerBlunder(source));
            var inventory = player.getInventory();

            for (int i = 0; i < inventory.size(); i++) {
                var stack = inventory.getStack(i);

                if (validator.apply(stack.getItem())) {
                    result = stack.copyWithCount(1);
                    stack.decrement(1);
                    break;
                }
            }
        }

        if (result == null)
            throw new MissingItemBlunder(source);

        return result;
    }

    public void addManaLink(Trick source, ManaLink link) throws BlunderException {
        for (var registeredLink : manaLinks) {
            if (registeredLink.manaPool.equals(link.manaPool)) {
                throw new EntityInvalidBlunder(source); //TODO: better exception
            }
        }

        manaLinks.add(link);
    }

    public abstract void addManaLink(Trick source, LivingEntity target, float limit);

    public Optional<ServerPlayerEntity> getPlayer() {
        return Optional.empty();
    }

    public Optional<Entity> getCaster() {
        return Optional.empty();
    }

    public Optional<ItemStack> getOtherHandSpellStack() {
        return Optional.empty();
    }

    public void useMana(Trick source, float amount) throws BlunderException {
        if (!manaLinks.isEmpty()) {
            float totalAvailable = 0;
            float leftOver = 0;

            for (var link : manaLinks) {
                totalAvailable += link.getAvailable();
            }

            for (var link : manaLinks) {
                float available = link.getAvailable();
                float ratio = available / totalAvailable;
                float ratioD = amount * ratio;
                float used = link.useMana(source, ratioD);

                if (used < ratioD) {
                    leftOver += ratioD - used;
                }
            }

            amount = leftOver;
        }

        if (!getManaPool().decrease(amount)) {
            throw new NotEnoughManaBlunder(source, amount);
        }
    }

    public float getMana() {
        return getManaPool().get();
    }

    public float getMaxMana() {
        return getManaPool().getMax();
    }

    public abstract Vector3d getPos();

    public BlockPos getBlockPos() {
        var pos = getPos();
        return new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
    }

    public abstract ServerWorld getWorld();

    public abstract Fragment getCrowMind();

    public abstract void setCrowMind(Fragment fragment);
}
