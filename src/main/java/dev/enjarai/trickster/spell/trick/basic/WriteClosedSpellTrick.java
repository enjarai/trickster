package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.fragment.StringFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.Optional;

public class WriteClosedSpellTrick extends Trick<WriteClosedSpellTrick> {
    public WriteClosedSpellTrick() {
        super(Pattern.of(1, 4, 7, 8, 5, 4, 3, 6, 7, 3, 8, 6, 5, 7),
                Signature.of(ArgType.ANY, FragmentType.SLOT.optionalOfArg(), FragmentType.STRING.optionalOfArg(), WriteClosedSpellTrick::run, RetType.ANY));
    }

    public Fragment run(SpellContext ctx, Fragment input, Optional<SlotFragment> optionalSlot, Optional<StringFragment> optionalName) {
        return WriteSpellTrick.run(this, ctx, input, optionalSlot, optionalName, true);
    }
}
