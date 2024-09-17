package dev.enjarai.trickster.item.memento;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.item.MementoItem;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import dev.enjarai.trickster.spell.mana.handler.AmethystManaHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.List;

public class AmethystMementoItem extends MementoItem {
    @Override
    protected void cast(World world, ServerPlayerEntity player, SpellPart spell, ItemStack stack, float maxMana) {
        ModEntityCumponents.CASTER
                .get(player)
                .getExecutionManager()
                .queue(new DefaultSpellExecutor(spell, new ExecutionState(List.of(), new SimpleManaPool(maxMana / 100), new AmethystManaHandler())));
    }
}
