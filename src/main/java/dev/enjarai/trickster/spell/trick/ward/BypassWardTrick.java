package dev.enjarai.trickster.spell.trick.ward;

import dev.enjarai.trickster.cca.ModUwuComponents;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.CannotBypassWardBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.WardFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class BypassWardTrick extends Trick<BypassWardTrick> {
    public BypassWardTrick() {
        //TODO: choose a better pattern
        super(Pattern.of(0, 5, 6), Signature.of(FragmentType.WARD, BypassWardTrick::bypass, FragmentType.WARD));
    }

    public WardFragment bypass(SpellContext ctx, WardFragment ward) {
        ctx.source()
                .getComponent(ModUwuComponents.WARD_BYPASS)
                .orElseThrow(() -> new CannotBypassWardBlunder(this))
                .apply(ctx.source().getWorld(), ward.uuid());
        return ward;
    }
}
