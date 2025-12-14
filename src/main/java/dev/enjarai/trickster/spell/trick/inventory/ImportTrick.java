package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.blunder.MissingItemBlunder;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.slot.ItemTypeFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;
import java.util.Optional;

public class ImportTrick extends Trick<ImportTrick> {
    public ImportTrick() {
        super(Pattern.of(3, 0, 5, 6, 3, 2, 5, 8, 3), Signature.of(FragmentType.ITEM_TYPE, ArgType.ANY.variadicOfArg(), ImportTrick::run, RetType.ANY.executor()));
    }

    public SpellExecutor run(SpellContext ctx, ItemTypeFragment itemType, List<Fragment> args) {
        var stack = ctx.getStack(this, Optional.empty(), s -> s.isOf(itemType.item()) && s.contains(ModComponents.FRAGMENT));

        if (stack.isEmpty()) {
            throw new MissingItemBlunder(this);
        }

        var component = stack.get().get(ModComponents.FRAGMENT);
        var spell = component.value() instanceof SpellPart part ? part : new SpellPart(component.value());
        return new DefaultSpellExecutor(spell, ctx.state().recurseOrThrow(args));
    }
}
