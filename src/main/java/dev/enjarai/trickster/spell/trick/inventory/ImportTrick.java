package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ItemTypeFragment;
import dev.enjarai.trickster.spell.trick.ExecutionTrick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.MissingItemBlunder;

import java.util.List;
import java.util.Optional;

public class ImportTrick extends ExecutionTrick<ImportTrick> {
    public ImportTrick() {
        super(Pattern.of(3, 0, 5, 6, 3, 2, 5, 8, 3), Signature.of(FragmentType.ITEM_TYPE, ANY_VARIADIC, ImportTrick::run));
    }

    public SpellExecutor run(SpellContext ctx, ItemTypeFragment itemType, List<Fragment> args) throws BlunderException {
        var stack = ctx.getStack(this, Optional.empty(), s -> s.isOf(itemType.item()) && s.contains(ModComponents.FRAGMENT));

        if (stack.isEmpty()) {
            throw new MissingItemBlunder(this);
        }

        var component = stack.get().get(ModComponents.FRAGMENT);
        var spell = component.value() instanceof SpellPart part ? part : new SpellPart(component.value());
        return new DefaultSpellExecutor(spell, ctx.state().recurseOrThrow(args));
    }
}
