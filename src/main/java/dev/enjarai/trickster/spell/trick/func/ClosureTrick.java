package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.Map.Hamt;
import dev.enjarai.trickster.spell.fragment.Map.MapFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.IncorrectFragmentBlunder;
import dev.enjarai.trickster.spell.blunder.MissingFragmentBlunder;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClosureTrick extends Trick {
    public ClosureTrick() {
        super(Pattern.of(5, 8, 7, 6, 3, 0, 1));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) {
        var executable = expectInput(fragments, FragmentType.SPELL_PART, 0);

        Map<Pattern, Fragment> replacements;
        if (fragments.size() >= 2 && fragments.get(1).type() == FragmentType.MAP) {
            var map = expectInput(fragments, FragmentType.MAP, 1).map();
            replacements = expectPatternMap(map);
        } else {
            replacements = new HashMap<>();

            int i = 1;
            while (i < fragments.size()) {
                var patternSpell = expectInput(fragments, FragmentType.SPELL_PART, i);

                i++;
                if (i >= fragments.size()) {
                    throw new MissingFragmentBlunder(this, i, Text.of("any"));
                }
                var fragment = expectInput(fragments, i);

                i++;
                if (patternSpell.glyph instanceof PatternGlyph patternGlyph) {
                    replacements.put(patternGlyph.pattern(), fragment);
                } else {
                    throw new IncorrectFragmentBlunder(this, i - 1, FragmentType.PATTERN.getName(), patternSpell.glyph);
                }
            }
        }

        var result = executable.deepClone();
        result.buildClosure(replacements);

        return result;
    }

    private Map<Pattern, Fragment> expectPatternMap(Hamt<Fragment, Fragment> map) {
        var replacements = new HashMap<Pattern, Fragment>();

        map.iterator().forEachRemaining(entry -> {
            if (entry.getKey() instanceof SpellPart spellKey && spellKey.glyph instanceof PatternGlyph pattern) {
                replacements.put(pattern.pattern(), entry.getValue());
            } else {
                throw new IncorrectFragmentBlunder(this, 1,
                        FragmentType.MAP.getName()
                                .append("<")
                                .append(FragmentType.PATTERN.getName())
                                .append(", ")
                                .append(Text.of("Any"))
                                .append(">"),
                        new MapFragment(map));
            }
        });

        return replacements;
    }
}
