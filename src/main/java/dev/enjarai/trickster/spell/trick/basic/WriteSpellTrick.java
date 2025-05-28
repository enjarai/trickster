package dev.enjarai.trickster.spell.trick.basic;

import java.util.Optional;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.blunder.OutOfRangeBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.fragment.StringFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class WriteSpellTrick extends Trick<WriteSpellTrick> {
    public WriteSpellTrick() {
        super(Pattern.of(1, 4, 7, 8, 5, 4, 3, 6, 7), Signature.of(ANY, FragmentType.SLOT.optionalOf(), FragmentType.STRING.optionalOf(), WriteSpellTrick::run));
    }

    public Fragment run(SpellContext ctx, Fragment input, Optional<SlotFragment> slot, Optional<StringFragment> name) throws BlunderException {
        return run(this, ctx, input, slot, name, false);
    }

    public static Fragment run(Trick<?> self, SpellContext ctx, Fragment input, Optional<SlotFragment> optionalSlot, Optional<StringFragment> optionalName, boolean closed) throws BlunderException {
        input = input.applyEphemeral();

        var player = ctx.source().getPlayer();
        var slot = optionalSlot.or(() -> ctx.source().getOtherHandSlot())
                .orElseThrow(() -> new NoPlayerBlunder(self));
        var name = optionalName.map(StringFragment::asText);
        var range = ctx.source().getPos().distance(slot.getSourceOrCasterPos(self, ctx));

        if (range > 16) {
            throw new OutOfRangeBlunder(self, 16.0, range);
        }

        slot.writeFragment(input, closed, name, player, self, ctx);
        return input;
    }
}
