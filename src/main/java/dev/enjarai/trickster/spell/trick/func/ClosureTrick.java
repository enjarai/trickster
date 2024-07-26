package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.IncorrectFragmentBlunder;
import dev.enjarai.trickster.spell.trick.blunder.MissingFragmentBlunder;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.List;

public class ClosureTrick extends Trick {
    public ClosureTrick() {
        super(Pattern.of(5, 8, 7, 6, 3, 0, 1));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var executable = expectInput(fragments, FragmentType.SPELL_PART, 0);
        var replacements = new HashMap<Pattern, Fragment>();

        int i = 1;
        while (i < fragments.size()) {
            var patternSpell = expectInput(fragments, FragmentType.SPELL_PART, i);

            i++;
            if (i >= fragments.size()) {
                throw new MissingFragmentBlunder(this, i, Text.of("Any"));
            }
            var fragment = expectInput(fragments, i);

            i++;
            if (patternSpell.glyph instanceof PatternGlyph patternGlyph) {
                replacements.put(patternGlyph.pattern(), fragment);
            } else {
                throw new IncorrectFragmentBlunder(this, i - 1, FragmentType.PATTERN.getName(), patternSpell.glyph);
            }
        }

        var result = executable.deepClone();
        result.buildClosure(replacements);

        return result;
    }
}
