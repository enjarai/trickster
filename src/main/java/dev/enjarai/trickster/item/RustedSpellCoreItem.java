package dev.enjarai.trickster.item;

import java.util.List;
import java.util.Optional;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.execution.executor.ErroredSpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World.ExplosionSourceType;

public class RustedSpellCoreItem extends SpellCoreItem {
    @Override
    public int getExecutionBonus() {
        return (int) Math.ceil(0.25 * Trickster.CONFIG.maxExecutionsPerSpellPerTick());
    }

    @Override
    public boolean onRemoved(ServerWorld world, BlockPos pos, ItemStack stack, Optional<SpellExecutor> executor) {
        if (executor.isPresent()
                && !(executor.get() instanceof ErroredSpellExecutor)
                && !executor.get().getDeepestState().isDelayed()
                && world.random.nextBoolean()) {
            world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 5f, true, ExplosionSourceType.BLOCK);
            return true;
        }

        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("DISCONNECT WITH CAUTION").setStyle(Style.EMPTY.withItalic(true).withColor(Formatting.RED)));
    }
}
