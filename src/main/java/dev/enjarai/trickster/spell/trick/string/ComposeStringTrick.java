package dev.enjarai.trickster.spell.trick.string;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.CharFragment;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.StringFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class ComposeStringTrick extends Trick<ComposeStringTrick> {
    public ComposeStringTrick() {
        super(Pattern.of(4, 6, 8, 5, 2, 0, 4, 5), Signature.of(variadic(Fragment.class), ComposeStringTrick::compose));
    }

    public Fragment compose(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var builder = ImmutableList.<CharFragment>builder();

        for (var frag : fragments) {
            //noinspection DeconstructionCanBeUsed
            if (frag instanceof ListFragment list) {
                for (var frag2 : list.fragments()) {
                    StringFragment.extractFromText(builder, frag2.asFormattedText());
                }
            } else {
                StringFragment.extractFromText(builder, frag.asFormattedText());
            }
        }
        return new StringFragment(builder.build());
    }
}
