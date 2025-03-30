package dev.enjarai.trickster.item;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.SpellExecutor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class SpellCoreItem extends Item {
    public SpellCoreItem() {
        super(new Settings().maxCount(1));
    }

    //TODO: add javadocs
    public int getExecutionBonus() {
        return (int) -Math.ceil(0.25 * Trickster.CONFIG.maxExecutionsPerSpellPerTick());
    }

    //TODO: add javadocs
    public boolean onRemoved(ServerWorld world, BlockPos pos, ItemStack stack, Optional<SpellExecutor> executor) {
        return false;
    }
}
