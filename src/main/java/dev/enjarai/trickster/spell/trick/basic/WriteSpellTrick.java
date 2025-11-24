package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.ImmutableItemBlunder;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.blunder.OutOfRangeBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.StringFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.Optional;

public class WriteSpellTrick extends Trick<WriteSpellTrick> {
    public WriteSpellTrick() {
        super(Pattern.of(1, 4, 7, 8, 5, 4, 3, 6, 7), Signature.of(ArgType.ANY, FragmentType.SLOT.optionalOfArg(), FragmentType.STRING.optionalOfArg(), WriteSpellTrick::run, RetType.ANY));
    }

    public Fragment run(SpellContext ctx, Fragment input, Optional<SlotFragment> slot, Optional<StringFragment> name) {
        return run(this, ctx, input, slot, name, false);
    }

    public static Fragment run(Trick<?> self, SpellContext ctx, Fragment input, Optional<SlotFragment> optionalSlot, Optional<StringFragment> optionalName, boolean closed) {
        var player = ctx.source().getPlayer();
        var slot = optionalSlot.or(() -> ctx.source().getOtherHandSlot())
                .orElseThrow(() -> new NoPlayerBlunder(self));
        var name = optionalName.map(StringFragment::asText);
        var range = ctx.source().getPos().distance(slot.getSourceOrCasterPos(self, ctx));

        if (range > 16) {
            throw new OutOfRangeBlunder(self, 16.0, range);
        }

        slot.applyModifier(self, ctx, stack -> {
            var updated = FragmentComponent.write(stack, input, closed, player, name);
            return updated.orElseThrow(() -> new ImmutableItemBlunder(self));
        });
        return input;
    }
}
