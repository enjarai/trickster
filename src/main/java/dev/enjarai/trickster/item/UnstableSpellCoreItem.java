package dev.enjarai.trickster.item;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.item.component.ModComponents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World.ExplosionSourceType;

public class UnstableSpellCoreItem extends SpellCoreItem {
    @Override
    public int getExecutionBonus() {
        return (int) Math.ceil(0.25 * Trickster.CONFIG.maxExecutionsPerSpellPerTick());
    }

    @Override
    public boolean onRemoved(ServerWorld world, BlockPos pos, ItemStack stack) {
        var comp = stack.get(ModComponents.SPELL_CORE);

        if (comp != null && !comp.executor().getCurrentState().isDelayed() && world.random.nextBoolean()) {
            world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 5f, true, ExplosionSourceType.BLOCK);
            return true;
        }

        return false;
    }
}
