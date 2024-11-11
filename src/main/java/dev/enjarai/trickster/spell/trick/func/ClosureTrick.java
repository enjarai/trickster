package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.util.Hamt;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.IncorrectFragmentBlunder;
import dev.enjarai.trickster.spell.blunder.MissingFragmentBlunder;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClosureTrick extends DistortionTrick {
    public ClosureTrick() {
        super(Pattern.of(5, 8, 7, 6, 3, 0, 1));
    }

    @Override
    public Fragment distort(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var executable = expectInput(fragments, FragmentType.SPELL_PART, 0);

        var map = expectInput(fragments, FragmentType.MAP, 1).map();

        var result = executable.deepClone();
        result.buildClosure(map.asMap());

        return result;
    }

    private Map<Pattern, Fragment> expectPatternMap(Hamt<Fragment, Fragment> map) throws IncorrectFragmentBlunder {
        var replacements = new HashMap<Pattern, Fragment>();

        map.iterator().forEachRemaining(entry -> {
            if (entry.getKey() instanceof PatternGlyph pattern) {
                replacements.put(pattern.pattern(), entry.getValue());
            } else {
                throw new IncorrectFragmentBlunder(this, 1,
                        Text.literal("{")
                                .append("pattern")
                                .append(": ")
                                .append(Text.of("any"))
                                .append("}"),
                        new MapFragment(map));
            }
        });

        return replacements;
    }
}
