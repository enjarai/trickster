package dev.enjarai.trickster.spell.trick.func;

import java.util.Optional;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.IncompatibleSourceBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class GetSpellStateTrick extends Trick<GetSpellStateTrick> {
    public GetSpellStateTrick() {
        super(Pattern.of(3, 4, 5, 8, 6, 3),
                Signature.of(FragmentType.NUMBER.optionalOfArg(), GetSpellStateTrick::run, FragmentType.NUMBER.optionalOfRet()));
    }

    public Optional<NumberFragment> run(SpellContext ctx, Optional<NumberFragment> maybeSpellSlot) {
        var spellSlot = maybeSpellSlot.orElse(new NumberFragment(ctx.data().getSlot().orElseThrow(() -> new IncompatibleSourceBlunder(this))));
        var manager = ctx.source().getExecutionManager().orElseThrow(() -> new IncompatibleSourceBlunder(this));
        return manager.getSpellState(spellSlot.asInt()).map(NumberFragment::new);
    }
}
