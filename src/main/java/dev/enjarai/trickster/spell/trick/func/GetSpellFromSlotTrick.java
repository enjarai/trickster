
package dev.enjarai.trickster.spell.trick.func;

import java.util.Optional;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.blunder.IncompatibleSourceBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class GetSpellFromSlotTrick extends Trick<GetSpellFromSlotTrick> {
    public GetSpellFromSlotTrick() {
        super(Pattern.of(5, 4, 3, 0, 1, 4, 7, 6, 3),
                Signature.of(FragmentType.NUMBER.optionalOfArg(), GetSpellFromSlotTrick::run, FragmentType.SPELL_PART.optionalOfRet()));
    }

    public Optional<SpellPart> run(SpellContext ctx, Optional<NumberFragment> maybeSpellSlot) {
        var spellSlot = maybeSpellSlot.orElse(new NumberFragment(ctx.data().getSlot().orElseThrow(() -> new IncompatibleSourceBlunder(this))));
        var manager = ctx.source().getExecutionManager().orElseThrow(() -> new IncompatibleSourceBlunder(this));
        return manager.getSpell(spellSlot.asInt());
    }
}
