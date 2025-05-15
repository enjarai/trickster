package dev.enjarai.trickster.spell.trick.string;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.StringFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class DecomposeStringTrick extends Trick<DecomposeStringTrick> {
    public DecomposeStringTrick() {
        super(Pattern.of(4, 8, 6, 3, 0, 2, 4, 3), Signature.of(ANY, DecomposeStringTrick::decompose));
    }

    public Fragment decompose(SpellContext ctx, Fragment fragment) throws BlunderException {
        //noinspection DeconstructionCanBeUsed
        if (fragment instanceof StringFragment string) {
            //noinspection unchecked,rawtypes
            return new ListFragment((ImmutableList) string.chars());
        } else {
            var builder = ImmutableList.<Fragment>builder();
            StringFragment.extractFromText(builder, fragment.asFormattedText());
            return new ListFragment(builder.build());
        }
    }
}
