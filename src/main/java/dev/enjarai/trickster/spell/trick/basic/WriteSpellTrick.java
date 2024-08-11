package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SpellComponent;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.ImmutableItemBlunder;

import java.util.List;
import java.util.Optional;

public class WriteSpellTrick extends Trick {
    public WriteSpellTrick() {
        super(Pattern.of(1, 4, 7, 8, 5, 4, 3, 6, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return activate(ctx, fragments, false);
    }

    public Fragment activate(SpellContext ctx, List<Fragment> fragments, boolean closed) throws BlunderException {
        var spell = supposeInput(fragments, 0).flatMap(s -> supposeType(s, FragmentType.SPELL_PART));
        var player = ctx.source().getPlayer();

        return player.flatMap(serverPlayerEntity -> Optional.of(serverPlayerEntity.getOffHandStack())).map(stack -> {
            var newSpell = spell.map(s -> {
                var n = s.deepClone();
                n.brutallyMurderEphemerals();
                return n;
            });

            newSpell.ifPresentOrElse(s -> {
                if (!SpellComponent.setSpellPart(stack, s, closed)) {
                    throw new ImmutableItemBlunder(this);
                }
            }, () -> {
                if (!SpellComponent.modifyReferencedStack(stack, s -> {
                    if (s.get(ModComponents.SPELL).immutable()) {
                        return false;
                    }

                    s.remove(ModComponents.SPELL);
                    return true;
                })) {
                    throw new ImmutableItemBlunder(this);
                }
            });
            return BooleanFragment.TRUE;
        }).orElse(BooleanFragment.FALSE);
    }
}
