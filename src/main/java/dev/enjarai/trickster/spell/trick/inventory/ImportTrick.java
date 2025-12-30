package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.blunder.MissingItemBlunder;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ItemTypeFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ImportTrick extends Trick<ImportTrick> {
    public ImportTrick() {
        super(Pattern.of(3, 0, 5, 6, 3, 2, 5, 8, 3), Signature.of(FragmentType.ITEM_TYPE, ArgType.ANY.variadicOfArg(), ImportTrick::run, RetType.ANY.executor()));
    }

    public SpellExecutor run(SpellContext ctx, ItemTypeFragment itemType, List<Fragment> args) {
        ItemStack importStack = null;

        var player = ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this));
        var inventory = player.getInventory();

        for (int i = 0; i < inventory.size(); i++) {
            var stack = inventory.getStack(i);

            if (stack.isOf(itemType.item()) && stack.contains(ModComponents.FRAGMENT)) {
                importStack = stack.copyWithCount(1);
                break;
            }
        }

        if (importStack == null || importStack.isEmpty()) {
            throw new MissingItemBlunder(this);
        }

        var component = importStack.get(ModComponents.FRAGMENT);
        //noinspection DataFlowIssue
        var spell = component.value() instanceof SpellPart part ? part : new SpellPart(component.value());
        return new DefaultSpellExecutor(spell, ctx.state().recurseOrThrow(args));
    }
}
