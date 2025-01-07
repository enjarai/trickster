package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.IncorrectFragmentBlunder;
import dev.enjarai.trickster.spell.blunder.ShadowingBuiltinTrickBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.Tricks;
import io.vavr.Tuple2;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.List;

public class ImportTricksTrick extends Trick {

    public ImportTricksTrick() {
        super(Pattern.of(0, 4, 2, 8, 7, 6, 0, 1, 2, 7, 0));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var module = expectInput(fragments, FragmentType.MAP, 0);
        var imports = new HashMap<Pattern, SpellPart>();

        for (Tuple2<Fragment, Fragment> entry : module.map()) {
            Fragment key = entry._1;
            Fragment value = entry._2;

            if (key.type() == FragmentType.PATTERN && value.type() == FragmentType.SPELL_PART) {
                if (Tricks.lookup(((PatternGlyph) key).pattern()) == null) {
                    imports.put(((PatternGlyph) key).pattern(), (SpellPart) value);
                } else {
                    throw new ShadowingBuiltinTrickBlunder(this, (PatternGlyph) key);
                }
            } else throw new IncorrectFragmentBlunder(
                    this,
                    0,
                    Text.literal("{")
                            .append(FragmentType.PATTERN.getName())
                            .append(": ")
                            .append(FragmentType.SPELL_PART.getName())
                            .append("}"),
                    module);

        }

        ctx.state().getImportedTricks().putAll(imports);

        return VoidFragment.INSTANCE;
    }
}
